#!/bin/sh
echo "Content-type: text/html"
echo

params=$(echo $QUERY_STRING | sed 's/query=//g' | tr '&' '\n')

#### Function definitions.

function getparam ()
{
    echo "$params" | grep "^$1=" | sed "s/.*=//g"
}
#echo -e "params are\n<PRE>$params\n</PRE>"

# Get display name of a / separated path
function getpackage ()
{
    local package="$(echo ${1%/} | tr '/' '.')"
    if [ "$package" == "" ]; then
        echo "root"
    else
        echo "$package"
    fi
}

function get_microavg ()
{
    loc=$1
    grep ${loc%/}'[/\t]' $rawfile | gawk '{n+=$2; x+=$1*$2} END {if (n==0) {print "-1"} else {print int(x/n)}}'
}
function get_macroavg ()
{
    loc=$1
    grep ${loc%/}'[/\t]' $rawfile | gawk '{n++; x+=$1} END {print int(x/n)}'
}
# Outputs a wee snippet of html showing the microaverage score, and icon
function insertscore ()
{
    local location=$1
    score=$(get_microavg $location)
    if [ $score == -1 ]; then
        score=$(get_macroavg $location)
    fi
    score2=$[$score/10]
    echo "$score% <img src=\"$jumblewebroot/$score2.gif\">"
}
function lastupdated ()
{
    #gawk '{print $4}' <$rawfile | sort -r | head -1 | tr -d '()'
    find $jumbledir/$packagepath -printf "%TY-%Tm-%Td %TH:%TM:%TS\n" | sort -r | head -1
}

function insertnavtable ()
{
    echo "<table bgcolor=#dddddd><tr><td>"

    # links to parent packages
    local parent=${packagepath%/*}
    local list="$parent"
    while [ "${parent%/*}" != "$parent" ]; do
        parent=${parent%/*}
        list="$parent $list"
    done
    local prev="/"
    echo -n "<a href=\"$jumblecgi\">/</a>"
    for l in $list; do
        l2=${l#$prev}
        echo -n "<a href=\"$jumblecgi&package=$l/\">$l2</a>/"
        prev="$l/"
    done
    echo ""

    echo "</td><td></td></tr>"

    # subpackage links/summaries
    subpackages=$(grep $jumbledir/$packagepath $rawfile | gawk '{print $3}' | sed "s|$jumbledir/$packagepath||g" | grep '/' | tr '/' ' ' | gawk '{print $1}' | sort | uniq)
    for subpackage in $subpackages; do
        echo "<tr><td align=right>$(insertscore $packagepath$subpackage)</td><td><a href=\"$jumblecgi&package=$packagepath$subpackage\">$subpackage</a></td></tr>"
    done
    echo "</table>"
}

function insertclasstable ()
{
    # class links/summaries
    echo "<table>"
    grep $jumbledir/$packagepath $rawfile | \
        gawk '{print $1" "$2" "$3" "$3" "$4}' | \
        sed -e "s|$jumbledir/$packagepath||" -e "s|$jumbledir/||" -e "s|%%|%|" | \
        gawk '{if (index($3,"/") == 0) print $0}' | \
        gawk '{image=int($1/10); if ($2=="0") { if ($1=="0%") { if (index($4,"gui") != 0) image="pointer"; else image="bad"; } else if ($1=="100%") image="free";}; print "<tr><td align=right>"$1" <img src=\"'"$jumblewebroot/"'"image".gif\"></td><td align=right>["$2"]</td><td><a href=\"'"$jumblecgi&class="'"$4"\">"$3"</a> "$5"</td></tr>"}'
    echo "</table>"
}

function dosummarypage ()
{
    if [ ! "$packagepath" ]; then
        packagepath=""
    elif [ "${packagepath%/}" == "$packagepath" ]; then
        packagepath="$packagepath/"
    fi

    cat <<EOF
<html>
<link rel=stylesheet type="text/css" href="/global.css">
<head>
<title>Jumble results for $checker $(getpackage "$packagepath")</title>
</head>
<body>

<div>
<table width="100%" cellpadding=0 align=center><tr>
<td>
EOF
    cat jumble.js
    cat <<EOF
</td>
<td><h1 align=center>$checker Jumble</h1></td>
</tr>
<tr><td colspan=2> <hr>
<a href="$jumblewebroot/index.html">Jumble</a> is a class level mutation testing tool working in conjunction with JUnit. The purpose of mutation testing is to provide a measure of the effectiveness of test cases. A single consequential mutation is performed on the code to be tested, the corresponding test cases are then executed. If the modified code fails the tests, then this increases confidence in the tests. Conversely, if the modified code passes the tests this indicates a testing deficiency.  This process is repeated for each of the possible mutation points and the overall test comprehensiveness is calculated.
<hr></td></tr>
</table>
EOF


    if [ ! "$checker" ]; then
        echo "<h2>No checker specified</h2>"
    elif [ ! -d "$jumbledir" ]; then
        echo "<h2>No jumble results dir for $checker</h2>"
    elif [ ! -f "$rawfile" ]; then
        echo "<h2>No jumble summary for $checker</h2>"
    else
        echo "<b>Module:</b> <a href=\"$checkerwebroot/$checker/\">$checker</a><br>"
        echo "<b>Package:</b> $(getpackage "$packagepath")"
        echo "<a href=\"$jumblecgi&package=$packagepath&enqueue=1\">(enqueue all classes in this package)</a><br>"
        echo "<b>Average jumble score:</b> $(insertscore $jumblesubdir/$packagepath)<br>"
        echo "<b>Last updated:</b> $(lastupdated)</p>"

        echo "<table><tr valign=top><td>"
        insertnavtable
        echo "</td><td>"
        insertclasstable
        echo "</td></tr></table>"
    fi

cat <<EOF
</div>
</body>
</html>
EOF
}

function doclassviewpage ()
{
    jumblefile=$checkerrootdir/$checker/$jumblesubdir/$classname
    classnamestd=$(echo $classname | sed 's|/|.|g')

    cat <<EOF
<html>
<link rel=stylesheet type="text/css" href="/global.css">
<head>
<title>Jumble results for $checker $classnamestd</title>
</head>
<body>

<div>
EOF

    if [ ! "$checker" ]; then
        echo "<h1>No checker specified</h1>"
    elif [ ! -d "$jumbledir" ]; then
        echo "<h1>No jumble results dir for $checker</h1>"
    else
        echo "<h1>Jumble results for $classnamestd</h1>"

        echo "<a href=\"$jumblecgi&class=$classname&enqueue=1\">Enqueue this class for a jumble run</a><br>"
        # -path option to find is deprecated, should use -wholename when find on giger supports it
        find "$checkerrootdir/$checker/javadocs" -path "*${classname}.html" | sed "s|$checkerrootdir|$checkerwebroot|g" | grep -v "filtered" | gawk '{ print "<a href=\""$0"\">View JavaDoc</a>"}'
        
        if [ ! -f "$jumblefile" ]; then
            echo "<h2>No jumble output file found $jumblefile</h2>"
        else
            # See if the src file is visible to us
            local srcfile
            if [ -f "$srcmap" ]; then
                local outerclass="$(echo "$classnamestd" | sed "s/$.*//g")"
                srcfile="$(grep -w "$outerclass" $srcmap | head -1 | gawk '{print $2}')"
                if [ ! -f "$srcfile" ]; then
                    srcfile=
                fi
            fi
            echo "<hr>"
            echo "<pre>"
            find $jumblefile -printf "Jumble run at %TY-%Tm-%Td %TH:%TM:%TS\n"
            if [ "$srcfile" ]; then
                cat $jumblefile | sed 's/FAIL: \([^:]\+\):\([0-9]\+\):/FAIL: <a href="#\2">\1:\2:<\/a>/g'
                echo "</pre>"
                echo "<hr>"
                cat <<EOF
<STYLE type="text/css">
TABLE,TD,TH {border-style:solid; border-color:black;} 
TD,TH {background:white;margin:0;line-height:100%;padding-left:0.5em;padding-right:0.5em;} 
TD {border-width:0 1px 0 0;} 
TH {border-width:1px 1px 1px 0;} 
TABLE.s {border-spacing:0; border-collapse:collapse;border-width:0 0 1px 1px; white-space:pre} 
TABLE.s {border-width:1px 0 1px 1px;} 
TABLE.s TD {font-family:courier,monospace;font-size:10pt;} 
TABLE.s TD {padding-left:0.25em;padding-right:0.25em;} 
TABLE.s TD.l {padding-left:0.25em;padding-right:0.25em;text-align:right;background:#F0F0F0;}
EOF
                failedlines=$(cat $jumblefile | grep "FAIL: " | sed 's/.*FAIL: \([^:]\+\):\([0-9]\+\):.*/\,#f\2/g' | sort | uniq | tr -d '\n' | sed "s/^,//g")
                if [ "$failedlines" ]; then
                    cat <<EOF
$failedlines {background: #FF9999}
EOF
                fi
                cat <<EOF
</STYLE>
EOF
                #echo "<pre>"
                echo "<table class=\"s\">"
                #cat "$srcfile" | sed -e "s/&/&amp;/g" -e "s/</\&lt;/g" -e "s/>/\&gt;/g" | gawk '{ print "<a name=\""NR"\" id=\"f"NR"\">"NR" "$0"</a>"}'
                cat "$srcfile" | sed -e "s/&/&amp;/g" -e "s/</\&lt;/g" -e "s/>/\&gt;/g" | gawk '{ print "<tr><td class=\"l\" id=\"f"NR"\"><a name=\""NR"\">"NR"</a></td><td id=\"f"NR"\">"$0"</td></tr>"}'
                echo "</table>"
                #echo "</pre>"
            else
                cat $jumblefile
                echo "</pre>"
            fi
            echo "<hr>"
        fi
    fi

cat <<EOF
</div>
</body>
</html>
EOF
}

function doclassenqueue ()
{
    jumblequeue=$checkerrootdir/$checker/jumblequeue
    classnamestd=$(echo $classname | sed 's|/|.|g')

    echo "<html>"
    echo "<head>"
    echo "<title>Jumble results for $checker $classnamestd</title>"
    echo "</head>"
    echo "<body>"

    if [ -f "$jumblequeue" ]; then
        echo "$classnamestd" | tee "${jumblequeue}.cgiqueue$$" >"${jumblequeue}.new"
        grep -vwf "${jumblequeue}.cgiqueue$$" <"$jumblequeue" >>"${jumblequeue}.new"
        rm "${jumblequeue}.cgiqueue$$"
        mv "${jumblequeue}.new" "$jumblequeue"
        chmod 666 "$jumblequeue"
        echo "<h2>$classname has been enqueued</h2>"
    else
        echo "<h2>No jumble queue file found $jumblequeue</h2>"
    fi

    echo "</body>"
    echo "</html>"
}

function dopackageenqueue ()
{
    jumblepqueue=$checkerrootdir/$checker/jumblepqueue
    packagenamestd=$(echo $packagepath | sed -e 's|/$||g' -e 's|/|.|g')

    echo "<html>"
    echo "<head>"
    echo "<title>Jumble results for $checker $packagenamestd</title>"
    echo "</head>"
    echo "<body>"

    if [ -f "$jumblepqueue" ]; then
        echo "$packagenamestd" | tee "${jumblepqueue}.cgiqueue$$" >"${jumblepqueue}.new"
        grep -vwf "${jumblepqueue}.cgiqueue$$" <"$jumblepqueue" >>"${jumblepqueue}.new"
        rm "${jumblepqueue}.cgiqueue$$"
        mv "${jumblepqueue}.new" "$jumblepqueue"
        chmod 666 "$jumblepqueue"
        echo "<h2>Package $packagenamestd has been enqueued</h2>"
    else
        echo "<h2>No jumble package queue file found $jumblepqueue</h2>"
    fi

    echo "</body>"
    echo "</html>"
}

# Get parameters we need
checker=$(getparam checker)
packagepath=$(getparam package)
classname=$(getparam class | tr '.' '/')
enqueue=$(getparam enqueue)

# Editable variables
jumblewebroot=/jumble                                   # Path on web server to jumble docs / images
checkerrootdir=/share/reeltwo/html/keachecker/html      # Path on filesystem to root of all checker output files
checkerwebroot=/keachecker/html                         # Path on web server to root of all checker output files
if [ -f jumble.params ]; then
    . jumble.params                                     # Load installation-specific version of the above parameters
fi

# These shouldn't need editing
jumblesubdir=jumble                                     # Name of subdir containing raw output files for each class
jumbledir=$checkerrootdir/$checker/$jumblesubdir        # Fully qualified directory
rawfile=$checkerrootdir/$checker/jumblesummary          # File containing jumble summarized output
srcmap=$checkerrootdir/$checker/jumblesrcmap            # File mapping class to source file
jumblecgi="$jumblewebroot/jumble.cgi?checker=$checker"  # Prefix for this cgi-script

if [ "$classname" ]; then
    if [ "$enqueue" == 1 ]; then
        doclassenqueue
    else
        doclassviewpage
    fi
else
    if [ "$enqueue" == 1 ]; then
        dopackageenqueue
    else
        dosummarypage
    fi
fi
