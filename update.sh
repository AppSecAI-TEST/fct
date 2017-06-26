#!/bin/bash

git pull

for f in `ls`
do
	if [ -d $f ]
	then
		cd $f
		mvn clean install
		cd ..
	fi
done
