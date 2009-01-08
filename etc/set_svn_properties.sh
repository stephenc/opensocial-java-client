#!/bin/bash
# this script will set the proper svn properties on all the files in the tree
# It pretty much requires a gnu compatible xargs (for the -r flag).  Running
# on Linux is probably the best option

find . -path '*/.svn' -prune -o  -name "*.htm*" -print0 | xargs -0  -r  svn propset svn:mime-type text/html
#

svn propset svn:ignore -F etc/svn-ignores .
svn propset svn:ignore -F etc/svn-ignores java
svn propset svn:ignore -F etc/svn-ignores java/android
svn propset svn:ignore -F etc/svn-ignores java/samples
svn propset svn:ignore -F etc/svn-ignores java/src
