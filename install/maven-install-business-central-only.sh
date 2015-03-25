#!/bin/bash
URL="file:///home/$USER/.m2/repository"

# This script extracts the necessary libraries from the distributable

DISTRO=$1
if [ "x$DISTRO" == "x" ]; then
  echo "Please pass distro zip as a parameter, ie:"
  echo "  maven-install-business-central-only.sh jboss-bpms-6.0.2.GA-redhat-5-deployable-generic.zip"
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
elif [[ $binaryVersion =~ 6.0.3.+ ]]; then 
  internalVersion="6.0.3-redhat-6"
fi

rm -rf tmp
unzip $DISTRO -d tmp
find tmp -name "*.zip" -type f -exec sh -c 'unzip -d tmp {}' ';'
find tmp -name "business-central.war" -exec sh -c 'cd {}; zip -r ../../business-central.war *' ';'


# This script installs the necessary libraries from the drools runtime into a maven repo

# business-central webapp
mvn deploy:deploy-file -Dfile=tmp/business-central.war -DgroupId=org.kie -DartifactId=kie-drools-wb-distribution-wars -Dclassifier=tomcat7.0 -Dversion=$internalVersion -Dpackaging=war -DgeneratePom=true -DcreateChecksum=true -Durl=$URL

