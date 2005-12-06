#! /bin/sh

export HOME=/home/syscheck
export JAVA=/usr/local/java/jdk
export PATH=$PATH:$JAVA/bin:$HOME/bin:/usr/local/bin
export CVSROOT=:pserver:syscheck@giger:/home/cvs-repository2

export WEBROOT=/share/reeltwo/html/keachecker
export SCRIPTS=$WEBROOT/cvscheck
export SYSNAME=cvscheck_jumble
export CODEHOME=$WEBROOT/${SYSNAME}_java
export WEB=$WEBROOT/html/${SYSNAME}
export EMAIL_NOTIFY=1


export MAIN_MODULE="jumble"
export SRC_MODULES="jumble/src"
export LIB_MODULES="jumble/lib"
export TEST_MODULES="jumble/test"
export DOC_MODULES=" "
export JAVADOC_MODULES="jumble/src"
export JIKES_OPTS="+E +P -source 1.4 -deprecation"
export LEVEL=2
export DISABLE_JUMBLE=1    # Don't run jumble on itself

(
    (sh $SCRIPTS/cvscheck.sh cvscheck_main 2>&1)
) >/dev/null



