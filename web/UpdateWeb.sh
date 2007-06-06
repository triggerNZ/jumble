#!/bin/sh
if test $# -eq 0
then
    echo "Usage: $0 sourceforge_username"
    exit 1
fi
USER=$1
WEBSITE=shell.sourceforge.net:/home/groups/j/ju/jumble/htdocs
shift
echo "Copying *.html *.gif to $USER@$WEBSITE"
scp *.html *.gif $USER@$WEBSITE
