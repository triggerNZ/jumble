#! /bin/sh

LOGNAME=${LOGNAME:-$USER}
if [ ! "$HOME" ]; then
    export HOME=/home/$LOGNAME
fi

export JAVA_HOME=/usr/local/java/jdk1.5
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

# Description of modules in subversion
export MAIN_MODULE="jumble"
export CVSROOT=
if [ -f "$CODEHOME/$MAIN_MODULE/.svn/entries" ]; then
    export SVNROOT=$(cat "$CODEHOME/$MAIN_MODULE/.svn/entries" | sed '/^ *url=/!d;s/^ *url="//;s/".*$//')
else
    export SVNROOT="svn://giger/home/svn/$MAIN_MODULE/trunk"
fi
export SRC_MODULES="jumble/src"
export LIB_MODULES="jumble/lib"
export TEST_MODULES="jumble/test"
export DOC_MODULES=" "
export JAVADOC_MODULES="jumble/src"

# Options controlling how things get run
export COMPILERS="javacall_1_5"
export JAVAC_OPTS="-source 1.5"
export JAVADOC_OPTS="-source 1.5"
export PLOT_PACKAGE_DEPENDENCIES_OPTS='jumble jumble 3 3'
if [ "$USER" == "syscheck" ]; then
    export EMAIL_NOTIFY=1
    export IM_NOTIFY=1
fi
export LEVEL=2
export DISABLE_JUMBLE=1    # Don't run jumble on itself

#(
    (sh $SCRIPTS/cvscheck.sh cvscheck_main 2>&1)
#) >/dev/null


