#!/bin/bash


mvn clean deploy -P release scm:tag

if [[ $? == 0 ]]; then
git push


echo ""
echo "**** NOTE *****"
echo ""
echo "Remember to: "
echo ""
echo "  - create PR to merge develop to master"
echo ""
fi
