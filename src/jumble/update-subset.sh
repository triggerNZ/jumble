#!/bin/bash
#
# Extracts the jumble scores for a specified subset
# $1 name of the subset
# Uses dep_$1 to find the roots for computing all the dependencies
# Puts the results to be used by the cgi scripts into raw_$1

#TODO get the root dependencies below from the build.xml file

jikes-1.18 $JIKES_OPTS -nowrite +DR=.tmp_0 $(ls $(for x in $(cat dep_$1) ; do echo -n " java/src/$x" ; done))

# make safer for case where jikes fails
if [ -r .tmp_0 ]; then
    egrep -v "^   " <.tmp_0 |\
    awk '{x = substr($3, 2, length($3) - 2); print x}' |\
    sort >$1_dep.txt
    
    cat raw2 |\
        awk '{ x=$3; sub("^jumbles/", "", x); print x, $0}' |\
        sort >.tmp_1

    join $1_dep.txt .tmp_1 |\
        awk '{print $2, $3, $4}' >raw_$1
fi
