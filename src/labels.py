	# Create temporary table of IDs for this mailing list \
	# These are primaryentityids (i.e. the people we REALLY want to send to) \
sql = \
	" CREATE TEMPORARY TABLE _mailings (\n" +\
	"  orderid serial,\n" +\
	"  entityid int4 NOT NULL,\n" +\
	"  firstname varchar(100),\n" +\
	"  lastname varchar(100),\n" +\
	"  ename varchar(100),\n" +\
	"  addressto varchar(100),\n" +\
	"  address1 varchar(100),\n" +\
	"  address2 varchar(100),\n" +\
	"  city varchar(50),\n" +\
	"  state varchar(50),\n" +\
	"  zip varchar(30),\n" +\
	"  sendentityid int4,\n" +\
	"  country varchar(50),\n" +\
	"  line1 varchar(100),\n" +\
	"  line2 varchar(100),\n" +\
	"  line3 varchar(100),\n" +\
	"  isgood bool,\n" +\
	"  addressto2 varchar(100)\n" +\
	");\n" +\
	" delete from _mailings;" +\
	" insert into _mailings (entityid) select entityid from entities where entityid in (%s);\n" +\
\
	"	update _mailings\n" +\
	"	set firstname = p.firstname,\n" +\
	"	lastname = p.lastname\n" +\
	"	from entities p\n" +\
	"	where p.entityid = _mailings.entityid;\n"

	# ========= Set addressto from multiple sources
	# 1. Try customaddressto
sql = sql +\
	"	update _mailings\n" +\
	"	set addressto = customaddressto\n" +\
	"	from entities p\n" +\
	"	where p.entityid = _mailings.entityid\n" +\
	"	and p.customaddressto is not null\n" +\
	"	and addressto is null;\n"

	# 2. Try pre-computed names
sql = sql +\
	"	update _mailings\n" +\
	"	set addressto = ename\n" +\
	"	where addressto is null and ename is not null;\n"

	# 3. Try addressto as name of person
sql = sql +\
	"	update _mailings\n" +\
	"	set addressto = \n" +\
	"		coalesce(p.firstname || ' ', '') ||\n" +\
	"		coalesce(p.middlename || ' ', '') ||\n" +\
	"		coalesce(p.lastname, '')\n" +\
	"	from entities p\n" +\
	"	where _mailings.entityid = p.entityid\n" +\
	"	and addressto is null;\n"

	# 4. Try addressto as name of organization
sql = sql +\
	"	update _mailings\n" +\
	"	set addressto = p.name\n" +\
	"	from organizations p\n" +\
	"	where _mailings.entityid = p.entityid\n" +\
	"	and addressto is null;\n"

	# Set the rest of the address\n" +\
sql = sql +\
	"	update _mailings\n" +\
	"	set address1 = e.address1,\n" +\
	"	address2 = e.address2,\n" +\
	"	city = e.city,\n" +\
	"	state = e.state,\n" +\
	"	zip = e.zip,\n" +\
	"	country = e.country\n" +\
	"	from entities e\n" +\
	"	where _mailings.entityid = e.entityid;\n"

	# ================ Check that logical label is good
sql = sql +\
	" update _mailings set isgood = true;\n" +\
\
	" update _mailings set address1=null where address1='';\n" +\
	" update _mailings set address2=null where address2='';\n" +\
	" update _mailings set zip=null where zip='';\n" +\
	" update _mailings set city=null where city='';\n" +\
	" update _mailings set state=null where state='';\n" +\
	\
	" update _mailings set isgood = false where" +\
	" addressto is null" +\
	" or (address1 is null and address2 is null)" +\
	" or (zip is null and trim(country) = 'USA')" +\
	" or city is null" +\
	" or state is null;\n"

	# ================== Set physical lalbel
sql = sql +\
	" update _mailings set line1=trim(addressto), line2=trim(address1), line3=trim(address2)" +\
	" where address1 is not null and address2 is not null;\n" +\
\
	" update _mailings set line1=null, line2=trim(addressto), line3=trim(address2)" +\
	" where address1 is null and address2 is not null;\n" +\
\
	" update _mailings set line1=null, line2=trim(addressto), line3=trim(address1)" +\
	" where address1 is not null and address2 is null;\n"

	# ================ Select, and then drop temp tables
sql = sql +\
	" select * from _mailings where isgood\n" +\
	" order by lastname, firstname;"
#	" drop table _mailings;"
