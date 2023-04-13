#!/bin/bash

mvn clean deploy -P release scm:tag

#git push --tags
git push

echo ""
echo "**** NOTE *****"
echo ""
echo "Remember to: "
echo "  - push w/ tags"
echo "  - create PR to merge develop to master"
echo ""
