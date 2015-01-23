#!/bin/bash

DATA_URL="https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.tar.gz"
GRADLE_URL="https://services.gradle.org/distributions/gradle-2.2.1-bin.zip"
PGBACKUP_URL="https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.pgdump"
DELETE_ARCHIVE_AFTER=y

#
GRADLE_TARGET=`basename "$DATA_URL"`
DATA_TARGET=`basename "$DATA_URL"`
PGBACKUP_TARGET=`basename "$PGBACKUP_TARGET"`

# Download Gradle
wget "https://services.gradle.org/distributions/gradle-2.2.1-bin.zip"
unzip "$GRADLE_TARGET"

# Download Data
echo "Downloading archives (varies, maybe about 75GB). It will take a while."
wget "$DATA_URL" "$PGBACKUP_URL"
echo "Decompressing data archive"

if tar -Jxvf "$DATA_TARGET" && test $DELETE_ARCHIVE_AFTER = y
then
  rm "$PGBACKUP_TARGET"
fi
