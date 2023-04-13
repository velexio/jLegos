#!/bin/bash

mvn clean deploy -P release scm:tag

git push

echo ""
echo "**** NOTE *****"
echo ""
echo "Remember to: "
echo "  - create PR to merge develop to master"
echo ""
