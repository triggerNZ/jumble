#!/bin/bash
#
# Perform mutation testing on the specified class.
#
# Produces output vaguely resembling JUnit.  A "T" indicates
# the test timed out (this is not a bad thing, it only means
# the mutation made the code take a long time). A "M" means
# the given mutation was not picked up by the tests.

# maximum number of test iterations to perform
MAX_TESTS=50

# options for types of mutations to include
OPTIONS="-k -r"

# find path to this implementation, needed for index file
#MYPATH=$(which $0)
#MYPATH="${MYPATH%/*}"

# attempt to find a time command we can use, this is necessary
# because some older versions do not support -f
TIME=time
if $TIME -f "%e" 2>&1 | grep -q 'command not found'; then
    TIME=$(which time)
    if $TIME -f "%e" 2>&1 | grep -q 'command not found'; then
        echo "Cannot find usuable time command"
        exit
    fi
fi

if [ "$JUMBLEDB" = "" ]; then
    JUMBLEDB=~/.jumbledb
fi

if [ "$1" = "-clean" ]; then
    /bin/rm -f $JUMBLEDB >&/dev/null
    shift
fi

if [ "$1" = "" ]; then
    echo "Usage: $0 [-clean] [-x methods-to-exclude] fully-qualified-class-name*"
    exit
fi

EXCLUDE=""
if [ "$1" = "-x" ]; then
    EXCLUDE="-x $2"
    shift
    shift
fi

while [ "${1%%[^-]*}" = "-" ]; do
    EXCLUDE=" $1"
    shift
done

if [ ! -r $JUMBLEDB ]; then
    find ~/*java/ -print | grep '\.class$' >$JUMBLEDB
    if [ -d ~/chaos/ ]; then
        find ~/chaos/ -print | grep '\.class$' >>$JUMBLEDB
    fi
fi
    
# usage timelimit time command
function timelimit() {
    time=$1
    shift
    ($*) &
    jp=$!
    (sleep $time; kill -9 $jp; echo "TIME LIMIT EXPIRED") &
    sp=$!
    wait $jp
    kill -9 $sp >&/dev/null
}
    
while [ "$1" != "" ]; do
    # strip extension
    TARGET=$1
    shift
    TARGET=${TARGET%%.class}
    TARGET=${TARGET%%.java}
    
    FULL=$(grep $TARGET.class $JUMBLEDB | head -1)
    echo "Mutating $TARGET"
    SRC=${FULL%%.class}.java
    
    if [ -r "$SRC" ] && grep -q "interface *${TARGET##*/}" <"$SRC"; then
        echo "Score: 100 (INTERFACE)"
        continue
    fi
    
    TEST=${TARGET%%\$*}"Test"
    TEST=$(echo $TEST | sed 's/Abstract/Dummy/')
    
    # get a count of the mutation points
    mp=$(java jumble.Jumbler $OPTIONS $EXCLUDE -c $TARGET)
    
    if ! grep -q "$TEST\.class" $JUMBLEDB; then
        echo "Mutation points = $mp"
        echo "Score: 0 (NO TEST CLASS)"
        continue
    fi
    #echo "Test class is $TEST"
    
    # quick exit if no mutations can be performed
    if [ "$mp" = "0" ]; then
        echo "Score: 100 (NO MUTATIONS POSSIBLE)"
        continue
    fi
    
    # get a time limit for the tests
    TEST_RUN=$($TIME -f "__TIMEX__ %e" java $TEST 2>&1)
    if echo "$TEST_RUN" | grep -q "Exception in thread .main. java.lang.NoSuchMethodError: main"; then
        echo "Mutation points = $mp"
        echo "Score: 0 (NO main IN TESTS)"
        continue
    elif echo "$TEST_RUN" | grep -q "FAILURES"; then
        echo "Mutation points = $mp"
        echo "Score: 0 (TEST CLASS IS BROKEN)"
        continue
    fi
    TEST_TIME=$(echo "$TEST_RUN" | grep __TIMEX__ | gawk '{print $2}')
    TEST_TIME=$[${TEST_TIME%%.*}*2+10]
    echo "Mutation points = $mp, unit test time limit ${TEST_TIME}s"
    
    # make an initial compile to ensure class is up to date
    JS=${FULL%%.*}
    jikes +P -source 1.4 -deprecation +E ${JS%%\$*}.java
    n=0
    fail=0
    test_vector=""

    if [ "$mp" = "" ]; then
        echo "Problem getting number of mutation points"
        continue
    fi
    
    # compute set of mutation numbers to try
    if [ "$mp" -gt $MAX_TESTS ]; then
        m=$MAX_TESTS
        while [ $n -lt $MAX_TESTS ]; do
            r=$[$RANDOM%$mp]
            if ! echo $test_vector | grep -q " $r "; then
                test_vector=$test_vector" $r "
                n=$[$n+1]
            fi
        done
    else
        while [ $n -lt "$mp" ]; do
            test_vector=$test_vector" $n"
            n=$[$n+1]
        done
        m=$mp
    fi
    
    # try each of the selected mutations in turn
    for n in $test_vector; do
        echo -n "."
        RUN_RESULT=$(timelimit $TEST_TIME java -ea jumble.Jumbler $OPTIONS $EXCLUDE $TARGET $n $TEST 2>&1)
        if echo "$RUN_RESULT" | grep -q "TIME LIMIT EXPIRED"; then
            echo -n "T"
        elif echo "$RUN_RESULT" | grep -q 'FAIL:'; then
            fail=$[fail+1]
            echo "M $(echo "$RUN_RESULT" | grep 'FAIL:' | head -1)"
        elif ! echo "$RUN_RESULT" | grep -q 'PASS'; then
            # this case shouldn't happen, print details
            echo "$RUN_RESULT"
        fi
    done
    echo
    echo "Score: " $[100-100*$fail/$m]
    echo
done
