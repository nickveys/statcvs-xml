JAVA_HOME =

CVS_LOG = cvs.log
CVS_PATH = .
STATCVS_ARGS = -viewcvs http://cvs.berlios.de/cgi-bin/viewcvs.cgi/statcvs-xml/statcvs-xml/ -verbose

LIBS = $(shell find lib/ -name "*.jar")
CP = .:src:$(shell echo $(LIBS) | sed -e 's/ /:/g'):$(JAVA_HOME)/jre/lib/rt.jar
JDEPENDS = src/net/sf/statcvs/Main.java $(shell find src -name "*Renderer.java") \
           src/net/sf/statcvs/util/ConsoleOutErrHandler.java
SRC_DIST = net/sf/statcvs/*.properties net/sf/statcvs/web-files/*png \
           net/sf/statcvs/web-files/*css

ifeq ($(shell test -r Makefile.local && echo "true"),true)
  include Makefile.local
endif

all : 
	jikes -classpath $(CP) +D +Predundant-modifiers +F $(JDEPENDS) $(JAVAC_OPTS)

dev : makefiles cvsignore tags 

clean :
	find src/ -type f \( -name "*.class" -o -name "*.[ou]" \) \
	  -exec rm -f {} \;

cvsignore:
	@set -e; \
	for i in `find src -type d \! -name CVS`; do \
	  m=$$i/.cvsignore; \
	  echo "writing $$m"; \
	  echo "*.class" > $$m; \
	  echo "Makefile" >>$$m; \
	  echo ".cvsignore" >>$$m; \
	done

jar:
	rm -rf tmp
	mkdir tmp
	make JAVAC_OPTS="$(JAVAC_OPTS) -d tmp" all 
	cp README tmp/
	(cd src/; tar -cf - $(SRC_DIST)) | tar -C tmp/ -xf -
	for i in $(LIBS); do \
	  unzip -d tmp/ $$i; \
	  rm -rf tmp/META-INF; \
	done
	echo "Main-Class: net.sf.statcvs.Main" > tmp/Manifest
	jar cvmf tmp/Manifest statcvs-xml.jar -C tmp/ .

makefiles :
	@set -e; \
	for i in `find src -type d \! -name CVS`; do \
	  m=$$i/Makefile; \
	  s=`echo $$i/ | sed 's/[^\/]//g;s/\//\.\.\//g'`; \
	  echo "writing $$m"; \
	  echo "ROOT_DIRECTORY=$$s" > $$m; \
	  echo "include $$s/Makefile.include" >> $$m; \
	done

tags :
	find -name "*.java" | xargs etags

cvs.log:
	cvs log > cvs.log

html : 
	mkdir -p out-html
	$(JAVA_HOME)/bin/java -classpath $(CP) net.sf.statcvs.Main \
	  $(CVS_LOG) $(CVS_PATH) $(STATCVS_ARGS) -output-dir out-html \
	  -output-suite html

xdoc : 
	mkdir -p xdocs/statcvs
	$(JAVA_HOME)/bin/java -classpath $(CP) net.sf.statcvs.Main \
	  $(CVS_LOG) $(CVS_PATH) $(STATCVS_ARGS) -output-dir xdocs/statcvs-xml -output-suite xdoc

xml : 
	mkdir -p out-xml
	$(JAVA_HOME)/bin/java -classpath $(CP) net.sf.statcvs.Main \
	  $(CVS_LOG) $(CVS_PATH) $(STATCVS_ARGS) -output-dir out-xml -output-suite 

deploy :
	cvs log > cvs.log
	make all
	make xdoc
	maven site:deploy
