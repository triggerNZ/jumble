#! /bin/sh

LOGNAME=${LOGNAME:-$USER}
if [ ! "$HOME" ]; then
    export HOME=/home/$LOGNAME
fi

export JAVA_HOME=/usr/local/java/jdk1.4
export SYSNAME=cvscheck_jumble


# Set variables for where to check out to and where to put results
if [ "$LOGNAME" == "syscheck" ]; then
    export WEBROOT=/share/reeltwo/html/keachecker
    export SCRIPTS=$WEBROOT/cvscheck
    export CODEHOME=$WEBROOT/${SYSNAME}_java
    export WEB=$WEBROOT/html/${SYSNAME}
else
    export CODEHOME=$HOME/reeltwo_sandboxes
fi

# Description of modules in CVS
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

# Options controlling how things get run
export JIKES_OPTS="+E +P -source 1.4 -deprecation"
if [ "$USER" == "syscheck" ]; then
    export EMAIL_NOTIFY=1
    export IM_NOTIFY=1
fi
export LEVEL=2
export DISABLE_JUMBLE=1    # Don't run jumble on itself

(
    (sh $SCRIPTS/cvscheck.sh cvscheck_main 2>&1)
) >/dev/null


