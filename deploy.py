import os
import shutil
import os.path
import zipfile
import subprocess
import sys

# Create a deployment directory, which can be copied directly to
# desktop computers

holyokefw_dir = '../holyokefw'
deploy_dir = './deploy'
dist_dir = './dist'

dist_lib = os.path.join(dist_dir, 'lib')

try :
	shutil.rmtree(deploy_dir)
except :
	pass

deploy_lib = os.path.join(deploy_dir, 'lib')

os.mkdir(deploy_dir)
os.mkdir(deploy_lib)

# Assume all files are .jar
jar_dirs = [ \
	os.path.join(holyokefw_dir, 'lib'), 
	os.path.join(holyokefw_dir, 'dist'),
	'./lib']

for dir in jar_dirs :
	for f in os.listdir(dir) :
		if os.path.isdir(os.path.join(dir,f)) : continue
		shutil.copyfile(os.path.join(dir, f), os.path.join(deploy_lib, f))


# Copy offstagearts.jar, modifying MANIFEST along the way
ijar = zipfile.ZipFile(os.path.join(dist_dir, 'offstagearts.jar'), 'r')
ojar = zipfile.ZipFile(os.path.join(deploy_dir, 'offstagearts.jar'), 'w')
for ii in ijar.filelist :
	data = ijar.read(ii.filename)
	if ii.filename == 'META-INF/MANIFEST.MF' :
		# Append to MANIFEST.MF
#		manifest = [data[0:-1]]
		manifest = ['Manifest-Version: 1.0\n']
		manifest.append('Main-Class: offstage.launch.DeployLaunch\n')

		deploy_lib_files = os.listdir(deploy_lib)
		manifest.append('Class-Path:')
		for f in deploy_lib_files :
			manifest.append('  lib/')
			manifest.append(f)
			manifest.append('\n')		# MANIFEST.MF must end in new line
		data = ''.join(manifest)
#		print data
	ojar.writestr(ii.filename, data)


# --------------------------------------------------------
# This stuff is specific to Ballet Theatre

launcher_dir = os.path.expanduser('~/db.offstagearts.org/launchers')
shutil.copyfile(os.path.join(launcher_dir, 'offstagearts-ballettheatre.jar'), os.path.join(deploy_dir, 'launcher.jar'))

sitecode_file = '../oa_jmbt/dist/oa_jmbt.jar'
shutil.copyfile(sitecode_file, os.path.join(deploy_dir, 'sitecode.jar'))

if False :
#if True :
	print 'Starting gsync to Google Drive...'
	local_bin = os.path.expanduser('~/.local/bin')
#	cmd = [os.path.join(local_bin, 'gsync'), '-r', '--verbose', 'deploy/', 'drive://JMBTOffice/OffstageArts']
	cmd = [os.path.join(local_bin, 'gsync'), '-r', '--verbose', 'deploy/offstagearts.jar', 'drive://JMBTOffice/OffstageArts']
	subprocess.call(cmd, stdout=sys.stdout, stderr=sys.stderr)

