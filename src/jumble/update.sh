#!/bin/bash
#
# Does Jumble testing on its own CVS checkout

export JHOME=/share/reeltwo/html/jumble
export JUMBLEDB="$JHOME/.jumbledb"
export LANG=en_C
export CLASSPATH=$JHOME/java/src:$JHOME/java/test:$JHOME/java/internal:$JHOME/java/jumble/src:$JHOME/java/jumble/test:$JHOME/java/rplot/src:$JHOME/java/rplot/test:$JHOME/java/client/genesis/src:$JHOME/java/client/genesis/test:$JHOME/java/client/agrinet/src:$JHOME/java/client/agrinet/test:$JHOME/java/client/comma/src:$JHOME/java/client/comma/test:$JHOME/java/client/openeye/src:$JHOME/java/client/openeye/test$(cd $JHOME/java/client/genesis/lib; ls -1 *.jar | grep -v reeltwo.jar | gawk "{print \":$JHOME/java/client/genesis/lib/\"\$1}" | tr -d '\n')$(cd $JHOME/java/client/agrinet/lib; ls -1 *.jar | grep -v reeltwo.jar | gawk "{print \":$JHOME/java/client/agrinet/lib/\"\$1}" | tr -d '\n')$(cd $JHOME/java/jar; ls -1 *.jar | gawk "{print \":$JHOME/java/jar/\"\$1}" | tr -d '\n')$(cd $JHOME/java/client/comma/lib; ls -1 *.jar | gawk "{print \":$JHOME/java/client/comma/lib/\"\$1}" | tr -d '\n')
export JIKESPATH=$CLASSPATH:/usr/local/java/jdk/jre/lib/rt.jar

function jikesall ()
{
   jikes +E +Pno-switchcheck -source 1.4 `find -name "*.java"`
}
    
function javacall ()
{
   javac -source 1.4 `find -name "*.java"`
}
    
function cleanall ()
{
   find -name "*.class" -print -exec rm {} \;
}
    
echo "#CVS update"
cd java; cleanall >&/dev/null; cd -
cd java; cvs -q up 2>&1 | grep -v 'jumble/X.*class'; cd -
echo "#Compiling R2"
cd java/src; jikesall; cd -
#cd java/internal; jikesall; cd -
cd java/test; jikesall; cd -
echo "#Compiling Jumble"
cd java/jumble/src; jikesall; cd -
cd java/jumble/test; jikesall; cd -
echo "#Compiling RPlot"
cd java/rplot/src; jikesall; cd -
cd java/rplot/test; jikesall; cd -
echo "#Compiling Genesis"
cd java/client/genesis/src; jikesall; cd -
cd java/client/genesis/test; jikesall; cd -
echo "#Compiling Agrinet"
cd java/client/agrinet/src; jikesall; cd -
# because of some problem with cactus.jar need to use javac here
cd java/client/agrinet/test; javacall; cd -
echo "#Compiling OpenEye"
cd java/client/openeye/build; make; cd -
cd java/client/openeye/lib; . setup.sh; cd -
cd java/client/openeye/src; jikesall; cd -
cd java/client/openeye/test; jikesall; cd -

echo "#Building Jumble database"
find java -type f -name "*.class" >$JUMBLEDB

echo "#Building class lists"
FILES=$(find jumbles -mindepth 2 -type f)
find java -mindepth 2 -type f -name "*.class" | grep 'src/' >classlist

echo "#Checking for obsoletes"
removed=n
echo "$FILES" | sed -e 's|jumbles/||g' -e 's/\.txt/.class/g' >greplist
sed -e 's|.*/src/||' -e 's|java/internal/||' <classlist >c2list
obsolete=$(grep -v -F -f c2list greplist)
if [ "$obsolete" != "" ]; then
    echo "Obsoletes: $obsolete"
    removed=y
    /bin/rm -f $(echo "$obsolete" | sed -e 's/^/jumbles\//' -e 's/\.class/.txt/') >&/dev/null
fi

# all the grepping below is slow is there a better way?
echo "#Checking for new or updated classes to Jumble"
for file in $(cat c2list); do
    f=${file%.class}
    j=$(grep "${file%%$*}"'$' $JUMBLEDB | head -1)
    j=${j%.class}.java
    jt=$(grep "${f%%\$*}Test.class"'$' $JUMBLEDB | head -1)
    jt=${jt%.class}.java
    if [ ! -r $JHOME/jumbles/$f.txt ]; then
        mkdir -p $JHOME/jumbles/${f%/*}
        $JHOME/java/jumble/src/jumble/jumble.sh $file | tee $JHOME/jumbles/$f.txt
    elif [ "$j" -nt $JHOME/jumbles/$f.txt ]; then
        $JHOME/java/jumble/src/jumble/jumble.sh $file | tee $JHOME/jumbles/$f.txt
    elif [ "$jt" -nt $JHOME/jumbles/$f.txt ]; then
        $JHOME/java/jumble/src/jumble/jumble.sh $file | tee $JHOME/jumbles/$f.txt
    fi
done
/bin/rm -f greplist c2list >&/dev/null

if [ $removed = y ]; then
    FILES=$(find jumbles -mindepth 2 -type f)
fi

echo "#Scanning Jumble outputs"
TOTAL=0

for file in $FILES; do
    if ! grep -q '(INTERFACE)' $file; then
        score=$(grep '^Score:' $file | gawk '{print $2}')
        if [ "$score" != "" ]; then
            TOTAL=$[$TOTAL+$score]
        fi
        mp=$(grep '^Mutation points =' $file | gawk '{print $4}' | tr -d ',')
        if [ "$mp" = "" ]; then
            mp=0
            sc="free"
        elif grep -q '(NO TEST CLASS)' $file; then
            if grep -q -i gui $file; then
                sc="pointer"
            elif [ $mp = 0 ]; then
                sc="free"
            else
                sc="bad"
            fi
        else
            sc=${score%[0-9]}
            if [ "$sc" = "" ]; then
                sc="0"
            fi
        fi
        f=${file#jumbles/}
        f=${f%.txt}
        date=$(find $file -printf "%TY-%Tm-%Td")
        echo -e "<img src=$sc.gif> $score% [$mp] <a href=\"$file\">$f</a> ($date)<br>"
    fi
done >raw

# raw2 - cutdown raw that the cgi-bin script will like
sort -k6 raw | tr '><[]' '    ' | gawk '{print $3"\t"$4"\tjumbles/"$7"\t"$9}' > raw2

count=$(echo "$FILES" | wc -w)
mean=$[$TOTAL/$count]
echo $(date +"%Y%m%d %H:%M") $count $TOTAL >>history.log
rm -f hs_err* >&/dev/null
