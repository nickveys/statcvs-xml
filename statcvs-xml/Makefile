LIBS = $(shell find lib/ -name "*.jar")
CP = .:src:$(shell echo $(LIBS) | sed -e 's/ /:/g'):/usr/local/j2sdk1.4.1_01/jre/lib/rt.jar
JDEPENDS = src/net/sf/statcvs/Main.java $(shell find src -name "*Output.java")

all : 
	jikes -classpath $(CP) +D +Predundant-modifiers +F $(JDEPENDS)

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

run : 
	/usr/local/j2sdk1.4.1_01/jre/bin/java -classpath $(CP) net.sf.statcvs.Main \
	  studium.cvslog ~/projects/studium -output-dir xdocs/statcvs -output-suite xdoc
