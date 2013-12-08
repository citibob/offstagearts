
.PHONY : build

dist/offstagearts.jar :
	ant jar

offstagearts : dist/offstagearts.jar
	rm -rf offstagearts
	mkdir -p offstagearts/jars
	cd offstagearts/jars; \
	ln -s ../../dist/offstagearts.jar .; \
	ln -s ../../lib/*.jar .; \
	ln -s ../../../holyokefw/lib/*.jar .; \
	ln -s ../../../holyokefw/dist/holyokefw.jar .
	cd offstagearts; \
	ln -s ../scripts/offstagearts .; \
	ln -s $(HOME)/offstagearts/launchers/offstagearts-ballettheatre.jar .

offstagearts.tar.gz : offstagearts
	tar cvfzh offstagearts.tar.gz offstagearts
