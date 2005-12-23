#! /bin/sh

LOGNAME=${LOGNAME:-$USER}
if [ ! "$HOME" ]; then
    export HOME=/home/$LOGNAME
fi

export JAVA=/usr/local/java/jdk
export SYSNAME=cvscheck_jumble


if [ "$LOGNAME" == "syscheck" ]; then
    export WEBROOT=/share/reeltwo/html/keachecker
    export SCRIPTS=$WEBROOT/cvscheck
    export CODEHOME=$WEBROOT/${SYSNAME}_java
    export WEB=$WEBROOT/html/${SYSNAME}
    export EMAIL_NOTIFY=1
else
    export CODEHOME=$HOME/reeltwo_sandboxes
fi

export MAIN_MODULE="jumble"
if [ -f "$CODEHOME/$MAIN_MODULE/CVS/Root" ]; then
    export CVSROOT=$(cat "$CODEHOME/$MAIN_MODULE/CVS/Root")
else
    export CVSROOT=:pserver:$LOGNAME@giger:/home/cvs-repository2
fi
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


