#! /bin/sh
# Launcher script for the Reel Two continuous integration system (cvscheck).

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
if [ -f "$CODEHOME/$MAIN_MODULE/.svn" ]; then
    export SVNROOT=$(cd "$CODEHOME/$MAIN_MODULE"; svn info | sed -n "/^URL/s/^URL: //p")
else
    export SVNROOT="https://jumble.svn.sourceforge.net/svnroot/jumble/trunk/jumble"
fi
export LIB_MODULES="jumble/lib"     # Directory containing jarfiles that need to be in the classpath
export SRC_MODULES="jumble/src"     # Directory containing source code
export TEST_MODULES="jumble/test"   # Directory containing junit test source files
export RES_MODULES=" "              # Directory containing extra resources require (but not source code)
export JAVADOC_MODULES="jumble/src" # Which directories to create javadocs for

# Options controlling how things get run
export COMPILERS="javacall_1_5"
export JAVAC_OPTS="-source 1.5"
export JAVADOC_OPTS="-source 1.5"
export PLOT_PACKAGE_DEPENDENCIES_OPTS='jumble jumble 3 3'
if [ "$USER" == "syscheck" ]; then
    export EMAIL_NOTIFY=1
    export IM_NOTIFY=1
fi
export LEVEL=3
export DISABLE_JUMBLE=1    # Don't run jumble on itself

sh $SCRIPTS/cvscheck.sh cvscheck_main 2>&1


