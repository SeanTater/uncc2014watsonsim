#!/bin/bash

# Options ######################################################################
DATA_URL="https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.tar.gz"
GRADLE_URL="https://services.gradle.org/distributions/gradle-2.2.1-bin.zip"
PGBACKUP_URL="https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.pgdump"

################################################################################

GRADLE_TARGET=`basename "$DATA_URL"`
DATA_TARGET=`basename "$DATA_URL"`
PGBACKUP_TARGET=`basename "$PGBACKUP_TARGET"`

install_postgres() {
  if lsb_release -a | grep -q "Ubuntu 14.10"
  then
    echo "Detected Ubuntu 14.10."
    echo "Installing dependencies for starting."
    sudo apt-get update
    sudo apt-get install postgresql-9.4
  else
    cat <<END
      This script hasn't been tested with your distribution.
      Please make sure the following are installed:
      PostgreSQL Server 9.3+
END
  fi
}

load_gradle() {
  # Download Gradle
  wget "$GRADLE_URL"
  unzip "$GRADLE_TARGET"
}

load_data() {
  DELETE=$1
  echo "Downloading archives (varies, maybe about 75GB). It will take a while."
  wget "$DATA_URL"
  echo "Decompressing data archive"
  if tar -Jxvf "$DATA_TARGET" && test $DELETE
  then
    rm "$PGBACKUP_TARGET"
  fi
}

restore_postgres() {
  DELETE=$1
  if $DELETE && pg_restore $2 <$PGBACKUP_TARGET
  then
    rm $PGBACKUP_TARGET
  fi
}

read_bool() {
  echo "$1 [Y/n]: "
  read out
  if echo $out | egrep -qi '[yt]'
  then
    return "true"
  else
    return "false"
  fi
}

#### main() ####################################################################

cat <<END
  This install script installs Watsonsim and its associated data.
  To do this, it:
    Installs PostgreSQL server using local repositories
    Downloads, compiles, installs:
      Indri, libSVM, Eclipse and Gradle
    Downloads Java dependencies using Gradle
    Makes an eclipse project
    Downloads indexes (30GB+ download, 50GB+ on disk)
    Downloads a database (unknown download, 70GB+ on disk)
    
  This install script is designed for Ubuntu and Fedora Linux.
  If you have the right dependencies, you can probably run it on other
  distributions as well. It probably won't handle others (e.g. cygwin).
END

read_bool "Do you want to continue?" || exit 0

# Ask all the questions FIRST
LOAD_GRADLE=`read_bool "Download Gradle?"`
INSTALL_POSTGRES=`read_bool "Install Postgres?"`
LOAD_POSTGRES=`read_bool "Download Database?"`
RESTORE_POSTGRES=`read_bool "Restore Database (overwrites contents)?"`
if $RESTORE_POSTGRES
then
  pg_restore --help
  cat <<END
  There are many options for restoring a database backup.
  For example, consider:
    -U username    -h host    -p port   -d database
END
  echo "The filename will be filled in automatically (as $PGBACKUP_TARGET)."
  echo -n "Type in your options: pg_restore "
  read PGBACKUP_OPTS
fi
LOAD_DATA=`read_bool "Download Indexes?"`
DELETE_AFTER=`read_bool "Delete downloaded archives after uncompressing?"`

$LOAD_GRADLE && load_gradle
$INSTALL_POSTGRES && install_postgres
$LOAD_POSTGRES && wget "$PGBACKUP_URL"
$RESTORE_POSTGRES && restore_postgres $DELETE_AFTER "$PGBACKUP_OPTS"
$LOAD_DATA && load_data $DELETE_AFTER
