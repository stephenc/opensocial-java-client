#!/bin/bash
# this script will set the proper svn properties on all the files in the tree
# It pretty much requires a gnu compatible xargs (for the -r flag).  Running
# on Linux is probably the best option

find . -path '*/.svn' -prune -o  -name "*.htm*" -print0 | xargs -0  -r  svn propset svn:mime-type text/html
#