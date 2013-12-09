import sys
import datetime
import collections
import re
import textwrap
import locale
#locale.setlocale(locale.LC_ALL, 'en_US.UTF-8')
import labels
from java.sql import SQLException
import java.sql.Types
import codecs
import string

# ---------------------------------------------------------

# See for use of JDBC: https://wiki.python.org/jython/DatabaseExamples

def format_money(money, presym='$', postsym=''):
	if abs(money) < .001 : money = abs(money)	# Eliminate "$-0.00"
	fmt = '%0.2f' % money
	dot = string.find(fmt, '.')
	ret = []
	if money < 0 :
		ret.append('(')
		p0 = 1
	else :
		p0 = 0
	ret.append(presym)
	p1 = (dot-p0) % 3 + p0
	if (p1 == p0) : p1 += 3
	while True :
		ret.append(fmt[p0:p1])
		if p1 == dot : break
		ret.append(',')
		p0 = p1
		p1 += 3
	ret.append(fmt[dot:])	# decimals
	ret.append(postsym)
	if money < 0 : ret.append(')')
	else: ret.append(' ')
	return ''.join(ret)

# http://stackoverflow.com/questions/16675118/more-general-way-of-generating-pyodbc-queries-as-a-dict
def dict_cursor(db) :
	if db is None : return
	for row in db :
		if row is None :
			yield None
		else :
			yield dict((t[0], value) for t, value in zip (db.description, row))

def dict_rs(rs) :
	rsmd = rs.getMetaData()
	ncol = rsmd.getColumnCount()
	col_names = [rsmd.getColumnName(i) for i in range(1, ncol+1)]
	col_types = [rsmd.getColumnType(i) for i in range(1, ncol+1)]
	while rs.next() :
		ret = dict()
		for i in range(0, ncol) :
			val = rs.getObject(i+1)
			typei = col_types[i]
			if typei == java.sql.Types.NUMERIC :
				val = val.doubleValue()
			ret[col_names[i]] = val
#		print ret
#		print type(ret['amount'])
		yield ret


def iter_rss(st) :
	n = 0
	while True :
		rs = st.getResultSet()
		if rs is None :
			# Not an update or a select --- we're done with all result sets
			if st.getUpdateCount() == -1 : break
			# It was an update --- go on to next result set
		else :
			yield rs
		st.getMoreResults(java.sql.Statement.KEEP_CURRENT_RESULT)

def xstr(s):
    if s is None:
        return ''
    return s
#    return str(s)

## This is not defined in Jython
#def next(iter, *args) :
#	if len(args) > 0 :
#		try :
#			return iter.next()
#		except StopIteration :
#			return args[0]
#	else :
#		return iter.next()

# ---------------------------------------------------------

student_nameRE = re.compile(r'.*?Tuition for (.*?) \(')
class Account(object) :
	def __init__(self, entityid) :
		self.entityid = entityid
		self.lastname = ''
		self.firstname = ''
		self.trans = []			# Sorted by date
		self.matches = []	# (bill, payment, match_amount) tuples
		self.term_tuition = 0
		self.term_scholarship = 0
		self.registration_fee = 0
		self.payer_name = ''
		self.students = set()

	def add_trans(self, tr) :
		if tr['description'] is None :
			tr['description'] = 'Payment, thank you!'

		# Account for the 'Outstanding Balance' hack we did 2009-08-01
		if tr['description'] == 'Outstanding Balance' :
			self.trans[:] = []	# Clear in place

		# Add to list of students for this record
		match = student_nameRE.match(tr['description'])
		if match is not None :
			student = match.group(1)
			self.students.add(student)

		tr['pamount'] = int(round(float(tr['amount']) * 100.))	# Int in pennies
		self.trans.append(tr)

	def match_trans(self) :
		"""Call this after all transactions have been added."""

		unmatched_bills = collections.deque()
		current_bills = collections.deque()
		unmatched_pymt = collections.deque()


		# Sort by date (if not already sorted)
# NO: SQL already sorts it more sophisticated
#		self.trans.sort(key=lambda x : x['date'])

		# Separate into bills and payments
		for tr in self.trans :
			tr['pmatched'] = 0	# Will end up equal and opposite to pamount
			if tr['pamount'] < 0 :
				unmatched_bills.append(tr)
			else :
				unmatched_pymt.append(tr)

		# Match payments against bills
		while len(unmatched_pymt)> 0 and \
			(len(unmatched_bills) + len(current_bills)) > 0:

			# Move bills into the "current" category, according to
			# our simulated date
			date = unmatched_pymt[0]['date']
			while len(unmatched_bills) > 0 and unmatched_bills[0]['date'] <= date :
				current_bills.append(unmatched_bills.popleft())

			# Make future bills current if nothing is current
			if len(current_bills) == 0 :
				current_bills.append(unmatched_bills.popleft())


			# Grab a payment to match up
			py = unmatched_pymt[0]
			py_net = py['pamount'] + py['pmatched']

			# (Match to oldest bill, but also sometimes to youngest)
			if (py['transtype'] == 'tuition' or py['transtype'] == 'adj') :
#				print py
				# Match the newest bill
				bl = current_bills[-1]
				pop_bl_fn = current_bills.pop
			else :
				# Match the oldest bill
				bl = current_bills[-0]
				pop_bl_fn = current_bills.popleft


			# Match it
			bl_net = bl['pamount'] + bl['pmatched']
			py_net = py['pamount'] + py['pmatched']
#			print '(%d %d) = %d     (%d %d) = %d' % (
#				bl['pamount'], bl['pmatched'], bl['pamount'] + bl['pmatched'],
#				py['pamount'], py['pmatched'], py['pamount'] + py['pmatched'])
			match_amt = min(-bl_net, py_net)
			bl['pmatched'] += match_amt
			py['pmatched'] -= match_amt
			self.matches.append((bl, py, match_amt))

			# Move records to matched collection
			bl_net = bl['pamount'] + bl['pmatched']
			py_net = py['pamount'] + py['pmatched']
			if bl_net == 0 :
				pop_bl_fn()
			if py_net == 0 :
				unmatched_pymt.popleft()

	# @param soft_start_dt Start listing transactions at this point, if it seems necessary
	# @param hard_start_dt Always list transactions starting from here
	def get_relevant_trans(self, soft_start_dt, hard_start_dt) :
		# Look for the most recent item that is not fully matched.
		# Start from there, with "balance carried forward"
		itr = iter(self.trans)
		tr = next(itr, None)
		pbal = 0
		unmatched_encountered = False
		while True :
			if tr is None :
				return []		# Everything is matched!
			if tr['pamount'] + tr['pmatched'] != 0 :
				unmatched_encountered = True
				break			# Go on to relevant transactions
			if tr['date'] >= soft_start_dt :
				break			# Go on to relevant transactions

			pbal += tr['pamount']

			# Go on...
			tr = next(itr, None)

		# We now have the start of our relevant area
		relevant = []
		carryover = pbal
		while tr is not None :
			relevant.append(tr)
			pbal += tr['pamount']
			if (not unmatched_encountered) and tr['date'] < hard_start_dt :
				if pbal == 0 :
					carryover = 0
					relevant[:] = []	# Clear at zero balance points

			tr = next(itr, None)

		if carryover != 0 :
			tr = {'date' : relevant[0]['date'],
				'description' : 'Outstanding Balance/Credit',
				'pamount' : carryover,
				'pmatched' : 0}
			relevant = [tr] + relevant

		return relevant


# ---------------------------------------------------------
def str2date(s) :
	if s is None :
		return None
	else :
		return datetime.date(int(s[0:4]), int(s[4:6]), int(s[6:8]))

def run_report_java(conn, termid, soft_start_str, hard_start_str, asof_str, end_str, statements_txt, labels_txt) :

#	termid = 597		# YDP 13/14
#	soft_start_dt = datetime.date(2012, 7, 1)
#	hard_start_dt = datetime.date(2013, 8, 1)
#	asof_dt = datetime.date(2013, 11, 18)
#	end_dt = datetime.date(2020, 1, 1)				# end_dt >= asof_dt

	soft_start_dt = str2date(soft_start_str)
	hard_start_dt = str2date(hard_start_str)
	asof_dt = str2date(asof_str)
	end_dt = str2date(end_str)

	print 'Report Dates str:', soft_start_str, hard_start_str, asof_str, end_str
	print 'Report Dates:', soft_start_dt, hard_start_dt, asof_dt, end_dt

	run_report(conn, termid, soft_start_dt, hard_start_dt, asof_dt, end_dt,
		statements_txt, labels_txt)

# ---------------------------------------------------------

def run_report(conn, termid, soft_start_dt, hard_start_dt, asof_dt, end_dt,
	statements_txt, labels_txt) :
#	db = CursorWrapper(conn.cursor())
#	db = conn.cursor()
	db = conn.createStatement()

	#entityid = 39841		# payer
	#soft_start_dt = datetime.date(2012, 7, 1)
	#hard_start_dt = datetime.date(2013, 8, 1)
	#asof_dt = datetime.date(2013, 11, 18)
	#end_dt = datetime.date(2020, 1, 1)				# end_dt >= asof_dt

	#termid = 597		# YDP 13/14

	if end_dt is None : end_dt = datetime.date('2200-01-01')
	if asof_dt is None : asof_dt = end_dt


	# Get transaction records.  No acbal2 records are filled in,
	# so this makes things easier
	sql = \
		" select ac.actranstypeid,actt.name as transtype," +\
		" ac.cr_entityid as entityid, e.lastname, e.firstname," +\
		" e.orgname, e.isorg," +\
		" ac.date,amt.amount as amount,ac.description,ac.actransid,ac.termid" +\
		" from actrans2 ac, actrans2amt amt, actypes, entities e, actranstypes actt" +\
		" where actt.actranstypeid = ac.actranstypeid" +\
		" and ac.actransid = amt.actransid and amt.assetid = 0\n" +\
		" and e.entityid = ac.cr_entityid and not e.obsolete\n" +\
		" and ac.cr_actypeid = actypes.actypeid and actypes.name = 'school'\n" +\
		"           UNION" +\
		" select ac.actranstypeid,actt.name as transtype," +\
		" ac.db_entityid as entityid, e.lastname, e.firstname," +\
		" e.orgname, e.isorg," +\
		" ac.date,-amt.amount as amount,ac.description,ac.actransid,ac.termid" +\
		" from actrans2 ac, actrans2amt amt, actypes, entities e, actranstypes actt" +\
		" where actt.actranstypeid = ac.actranstypeid" +\
		" and ac.actransid = amt.actransid and amt.assetid = 0\n" +\
		" and e.entityid = ac.db_entityid and not e.obsolete\n" +\
		" and ac.db_actypeid = actypes.actypeid and actypes.name = 'school'\n" +\
		" order by isorg desc,lastname,firstname,entityid,date,amount desc;\n"

	accounts = {}
	rs = db.executeQuery(sql)
	for row in dict_rs(rs) :
		entityid = row['entityid']
		ac = accounts.get(entityid, None)
		if ac is None :
			ac = Account(entityid)
			accounts[entityid] = ac
		ac.add_trans(row)


	# rss[1] = Basic tuition and scholarship info for the term
	sql = \
		" select r.entityid0 as payerid,sum(tuition) as tuition, sum(scholarship) as scholarship\n" +\
		" from termregs tr, rels_o2m r\n" +\
		" where groupid = 597" +\
		" and tuition is not null and tuition > 0\n" +\
		" and tr.groupid = r.temporalid\n" +\
		" and tr.entityid = r.entityid1\n" +\
		" and r.relid = (select relid from relids where name = 'payerof')" +\
		" group by payerid;\n"
	rs = db.executeQuery(sql)

	for row in dict_rs(rs) :
		entityid = row['payerid']
		ac = accounts.get(entityid, None)
		if ac is None :
			ac = Account(entityid)
			accounts[entityid] = ac

		ac.term_tuition = float(row['tuition'])
		ac.term_scholarship = float(row['scholarship'])

	# -----------------------------------------------------------
	# Collect Incidental info
	sentityids = ",".join(str(x) for x in accounts.keys())
	sql = \
		" select e.entityid," +\
		" e.firstname, e.lastname," +\
		" (case when e.firstname is null then '' else e.firstname || ' ' end ||" +\
		" case when e.lastname is null then '' else e.lastname end) as payername\n" +\
		" from entities e where e.entityid in (%s)" \
		% (sentityids)

	rs = db.executeQuery(sql)
	for row in dict_rs(rs) :
		entityid = row['entityid']
		accounts[entityid].payer_name = row['payername']
		accounts[entityid].lastname = xstr(row['lastname'])
		accounts[entityid].firstname = xstr(row['firstname'])

	# -----------------------------------------------------------
	# Labels
	out = codecs.open(labels_txt, 'w', 'utf-8')

	# See: citibob.sql.SqlBatch
	sql = labels.sql % (sentityids)
	db.execute(sql)
	rs = iter_rss(db).next()
	for row in dict_rs(rs) :
		out.write(row['addressto'])
		out.write('\n')
		out.write(xstr(row['address1']))
		out.write('\n')
		if row['address2'] is not None :
			out.write(row['address2'])
			out.write('\n')
		out.write('%s, %s    %s' % (row['city'], row['state'], row['zip']))
		out.write('\n')
		if row['address2'] is None :
			out.write('\n')
		out.write('\n')
		out.write('\n')
	out.close()

	# -----------------------------------------------------
	# Write main stuff
	out = codecs.open(statements_txt, 'w', 'utf-8')


	aaccts = accounts.values()
	aaccts.sort(key=lambda x : x.lastname + ':' + x.firstname)

	for ac in aaccts :

		#print ac.carryover
		ac.match_trans()
		reltrans = ac.get_relevant_trans(soft_start_dt, hard_start_dt)
		if len(reltrans) == 0 :
			continue

	#	out.write('=======================================\n')
		out.write('\f')
		out.write('Jose Mateo Ballet Theatre: Statement of Account\n')
		out.write('\n')
		out.write('Date: %s\n' % asof_dt)
		out.write('Student\'s Name: %s\n' % ", ".join(ac.students))
		out.write('Bill To: %s\n' % ac.payer_name)
		out.write('Account ID: %d\n' % ac.entityid)
		out.write('\n')
		out.write('Term Tuition: $%1.2f\n' % ac.term_tuition)
		if ac.term_scholarship != 0 :
			out.write('Term Scholarship: $%1.2f\n' % ac.term_scholarship)
		out.write('\n')
		pbal = 0
		fmt = '{:10}  {:<50}  {:>10}  {:>10}\n'
		fmt2 = '{:10}  {:>50}  {:>10}  {:>10}\n'
		out.write('Current Charges\n')
		header = fmt.format('Date', 'Description', 'Amount', 'Balance')
		out.write(header)
		out.write('-'*len(header))
		out.write('\n')
		wrapper = textwrap.TextWrapper()
		wrapper.width = 50
		wrapper.subsequent_indent = '    '
		itr = iter(reltrans)
		tr = next(itr, None)
		while tr is not None :
			if tr['date'] > asof_dt : break
			pbal += tr['pamount']
			desc_lines = wrapper.wrap(tr['description'])
			out.write(fmt.format(
				str(tr['date']), desc_lines[0],
				format_money(tr['pamount'] * -.01),
				format_money(pbal * -.01)))
			for desc in desc_lines[1:] :
				out.write(fmt.format('', desc, '', ''))
			tr = next(itr, None)

		out.write('-'*len(header))
		out.write('\n')
		if pbal == 0 :
			out.write(fmt2.format('', 'No Payment Due Now', '', ''))
		elif pbal > 0 :
			out.write(fmt2.format('', 'Outstanding Credit, No Payment Due Now', '', format_money(-pbal*-.01)))
		else :
			out.write(fmt2.format('','Payment Due','',format_money(pbal*-.01)))


		# --------- Future Charges
		if tr is not None :
			out.write('\n')
			out.write('\n')
			out.write('\n')
			out.write('Future Charges\n')
			out.write(fmt.format('Date', 'Description', 'Amount', 'Balance'))

			out.write('-'*len(header))
			out.write('\n')

			while tr is not None :
				pbal += tr['pamount']
				desc_lines = wrapper.wrap(tr['description'])
				out.write(fmt.format(
					str(tr['date']), desc_lines[0],
					format_money(tr['pamount'] * -.01),
					format_money(pbal * -.01)))
				for desc in desc_lines[1:] :
					out.write(fmt.format('', desc, '', ''))
				tr = next(itr, None)

		out.write('\n')
		out.write('NOTE: Installment payments more than 30 days past due will be assessed\n1.5% per month finance charge.\n')

	out.close()

	db.close()

# ------------------------------------------------------------------
# Main (test) program
if __name__ == '__main__' :
	import psycopg2
	conn = psycopg2.connect("host=127.0.0.1 port=5432 dbname=ballettheatre_offstagearts user=ballettheatre")

	termid = 597		# YDP 13/14
	soft_start_dt = datetime.date(2012, 7, 1)
	hard_start_dt = datetime.date(2013, 8, 1)
	asof_dt = datetime.date(2013, 11, 18)
	end_dt = datetime.date(2020, 1, 1)				# end_dt >= asof_dt

	run_report(conn, termid, soft_start_dt, hard_start_dt, asof_dt, end_dt)
