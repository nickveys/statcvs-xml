#!/bin/bash

# run this script from the statcvs directory to update the statcvs
# vendor branch

set -e

rm -rf tmp
mkdir -p tmp/src/net/sf/statcvs/output

cp -a src/net/sf/statcvs/model/ src/net/sf/statcvs/input/ \
  src/net/sf/statcvs/util/ tmp/src/net/sf/statcvs
cp src/net/sf/statcvs/output/*Integration.java tmp/src/net/sf/statcvs/output

find tmp/ -name "*.class" -or -name "CVS" -print0 | xargs -0 rm -rf 

TAG=statcvs-`date +%Y%m%d`
cd tmp && cvs -d squig@cvs.statcvs-xml.berlios.de:/cvsroot/statcvs-xml \
  import -kk statcvs-xml2 statcvs $TAG
