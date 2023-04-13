#!/bin/bash

mvn clean deploy -P release
mvn scm:tag

#git push --tags
#git push origin develop

echo ""
echo "**** NOTE *****"
echo ""
echo "Remember to: "
echo "  - push w/ tags"
echo "  - create PR to merge develop to master"
echo ""
