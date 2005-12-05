#!/bin/bash

cp *.gif update.sh update-subset.sh key.html dep_* /share/reeltwo/html/jumble
chmod 0666 /share/reeltwo/html/jumble/{*.gif,update.sh,key.html}
chmod u+x /share/reeltwo/html/jumble/update.sh
