#!/bin/bash
URL="file:///home/$USER/.m2/repository"

# This script extracts the necessary libraries from the distributable

DISTRO=$1
if [ "x$DISTRO" == "x" ]; then
  echo "Please pass distro zip as a parameter, ie:"
  echo "  maven-install-drools6.sh jboss-bpms-6.0.2.GA-redhat-5-deployable-generic.zip"
  exit
fi

binaryVersion=$(echo $DISTRO | grep -oE "([0-9]{1}\.)*GA" | sed "s/GA/BRMS/")

# hackery to switch the version numbers
if [[ $binaryVersion =~ 6.0.0.+ ]]; then
  internalVersion="6.0.0-redhat-9"
elif [[ $binaryVersion =~ 6.0.1.+ ]]; then
  internalVersion="6.0.2-redhat-6"
elif [[ $binaryVersion =~ 6.0.2.+ ]]; then 
  internalVersion="6.0.3-redhat-4"
fi

rm -rf tmp
unzip $DISTRO -d tmp
find tmp -name "*.zip" -type f -exec sh -c 'unzip -d tmp {}' ';'
find tmp -name "business-central.war" -exec sh -c 'cd {}; zip -r ../../business-central.war *' ';'
#mv tmp/business-central.war tmp/business-central-0.0.0-redhat-0.war
#mv tmp/business-central-0.0.0-redhat-0.war tmp/*engine


# This script installs the necessary libraries from the drools runtime into a maven repo

# business-central webapp
mvn deploy:deploy-file -Dfile=tmp/business-central.war -DgroupId=org.drools -DartifactId=business-central -Dversion=$internalVersion -Dpackaging=war -DgeneratePom=true -DcreateChecksum=true -Durl=$URL


for filename in $(find . -type f -name "kie-tomcat-integration*.[w|j]ar")
do
  version=$(echo $filename | grep -oE "[0-9]+.[0-9]+.[0-9]+-redhat-[0-9]+")
  packaging=$(echo $filename | grep -oE "(.ar)$")
  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.kie -DartifactId=kie-tomcat-integration -Dversion=$version -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
done

for filename in $(find . -type f -name "kie-maven-plugin*.[w|j]ar")
do
  version=$(echo $filename | grep -oE "[0-9]+.[0-9]+.[0-9]+-redhat-[0-9]+")
  packaging=$(echo $filename | grep -oE "(.ar)$")
  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.kie -DartifactId=kie-maven-plugin -Dversion=$version -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
done


cd tmp/*engine

FILES=$(find . -type f -wholename "*$internalVersion.[w|j]ar" | grep -v "/lib/")
echo -e "\nInstalling the following files:"
for filename in $FILES
do
  echo "  $filename"
done
echo ""

# plugin must have the built version, not the distro version
#PLUGIN=*kie-maven-plugin*
#for filename in $PLUGIN
#do
#  version=$(echo $filename | grep -oE "[0-9]+.[0-9]+.[0-9]+-redhat-[0-9]+")
#  packaging=$(echo $filename | grep -oE "(.ar)$")
#  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.kie -DartifactId=kie-maven-plugin -Dversion=${version} -Dpackaging=$packaging -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
#done

for filename in $FILES
do
  version=$(echo $filename | grep -oE "[0-9]+.[0-9]+.[0-9]+-redhat-[0-9]+")
  groupId=$(echo $filename | sed "s/\.\///" | grep -oE "^[a-z]+")
  artifactId=$(echo $filename | sed "s/\.\///" | grep -oE "(.+[0-9])" | sed "s/[0-9]\.[0-9]\.[0-9]-redhat-[0-9]//" )
  packaging=$(echo $filename | grep -oE "(.ar)$")
  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.${groupId} -DartifactId=${artifactId%?} -Dversion=$version -Dpackaging=$packaging -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
done


cd lib

ECJ=*ecj*
for filename in $ECJ
do
#  ecjVersion=$(echo $filename | grep -oE "([0-9]{1})\.([0-9]{1})\.([0-9]{1})")
#  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.eclipse.jdt.core.compiler -DartifactId=ecj -Dversion=$ecjVersion -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.eclipse.jdt.core.compiler -DartifactId=ecj -Dversion=$internalVersion -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
done

MVEL=*mvel2*
for filename in $MVEL
do
#  mvelVersion=$(echo $filename | grep -oE "([0-9]{1})\.([0-9]{1})\.([0-9]{1}).([A-Za-z])*([0-9])*")
#  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.mvel -DartifactId=mvel2 -Dversion=$mvelVersion -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.mvel -DartifactId=mvel2 -Dversion=$internalVersion -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
done

ANTLR=*antlr-runtime*
for filename in $ANTLR
do 
#  antlrVersion=$(echo $filename | grep -oE "([0-9]{1})\.([0-9]{1})")
#  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.antlr -DartifactId=antlr-runtime -Dversion=$antlrVersion -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.antlr -DartifactId=antlr-runtime -Dversion=$internalVersion -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
done

EXCEL=*poi*
for filename in $EXCEL
do
#  excelVersion=$(echo $filename | grep -oE "([0-9]{1})\.([0-9]{1})\.([0-9]{1})")
#  mvn deploy:deploy-file -Dfile=$filename -DgroupId=jxl -DartifactId=jxl -Dversion=$jxlVersion -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
  artifactId=$(echo $filename | grep -oE "^[^0-9]+")
  mvn deploy:deploy-file -Dfile=$filename -DgroupId=org.apache.poi -DartifactId=${artifactId%?} -Dversion=$internalVersion -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true -Durl=$URL
done

cd -
