#!/bin/sh
if test $# -eq 0
then
    echo "Usage: $0 sourceforge_username"
    exit 1
fi
USER=$1
WEBSITE=shell.sourceforge.net:/home/groups/j/ju/jumble/htdocs
shift
echo "Copying *.html *.gif *.css jumble_icon.png to $USER@$WEBSITE"
scp *.html *.gif *.css jumble_icon.png $USER@$WEBSITE

echo "NOTE: you may need to correct the permissions of any new files"
echo "Eg. ssh shell.sourceforge.net"
echo "    cd /home/groups/j/ju/jumble/htdocs"
echo "    chmod 664 *.*"

