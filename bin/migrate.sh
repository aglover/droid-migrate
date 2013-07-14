#!/bin/bash


CLASSPATH=./../lib/freemarker-2.3.20.jar:./../lib/jopt-simple-4.5.jar:./../dist/droid-migrate-all.jar

java -cp $CLASSPATH com.b50.migrations.DroidMigrate $1 $2 $3 $4 $5 $6
