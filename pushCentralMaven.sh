#!/bin/bash

mvn clean
mvn release:prepare
mvn release:perform

git push --tags
git push origin develop

echo "Remember to create PR to merge develop to master"
