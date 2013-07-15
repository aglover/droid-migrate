#!/bin/bash

[ -z "$DROID_MIGRATE_HOME" ] && echo "Need to set DROID_MIGRATE_HOME" && exit 1;

CLASSPATH=$DROID_MIGRATE_HOME/lib/freemarker-2.3.20.jar:$DROID_MIGRATE_HOME/lib/jopt-simple-4.5.jar:$DROID_MIGRATE_HOME/dist/droid-migrate-all.jar

java -cp $CLASSPATH com.b50.migrations.DroidMigrate $1 $2 $3 $4 $5 $6
