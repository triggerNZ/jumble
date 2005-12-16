#!/bin/sh

# $1 is name of directory containing jumble output files
# $2 is name of output file
function jumblesummary ()
{
    echo "Scanning Jumble outputs"
    local dir="$1"
    local summaryfile="$2"
    local total=0
    find $dir -type f | while read file; do
        local date=$(find "$file" -printf "%TY-%Tm-%Td")
        local score=$(grep '^Score:' "$file" | sed 's/.*Score: //' | gawk '{print $1}')
        if [ "$score" != "" ]; then
            total=$[$total+$score]
        fi
        local mp=$(grep '^Mutation points =' "$file" | gawk '{print $4}' | tr -d ',')
        if [ "$mp" = "" ]; then
            mp=0
            sc="free"
        elif grep -q '(NO TEST CLASS)' "$file"; then
            if grep -q -i gui "$file"; then
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
        local f="${file#$dir/}"
        echo -e "<img src=$sc.gif> $score% [$mp] <a href=\"$file\">$f</a> ($date)<br>"
    done | sort -k6 | tr '><[]' '    ' | gawk '{print $3"\t"$4"\t'"$dir/"'"$7"\t"$9}' >$summaryfile
}
