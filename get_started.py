#!/usr/bin/env python

################################################################################
DATA_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.tar.gz"
PGBACKUP_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.pgdump"
GRADLE_URL = "https://services.gradle.org/distributions/gradle-2.2.1-bin.zip"
################################################################################
# Needs requests, wget
################################################################################
import platform
import requests
import shutil
from subprocess import call, check_call
import sys
import tarfile
import urllib2
import wget

# Download a file and give a little feedback on the screen at the same time.
def download(url, name):
    print "Downloading %s. 1 \".\" = 1 MB: " % url
    # We know the URLs beforehand and this method works fine
    base = url.split('/')[-1]
    with open(base, 'wb') as out:
        r = requests.get(url, stream=True)
        # Even a megabyte at a time is a pretty busy screen from what I see.
        for block in r.iter_content(1024 * 1024):
            sys.stdout.write('.')
            out.write(block)

# Unpack a file and delete the original
def unpack(ar):
    print "Unpacking %s" %ar
    tarfile.open(ar).extractall()
    os.remove(ar)

def install_postgres():
    if platform.system() == "Linux":
        dist = platform.dist()[0]
        try:
            if dist == "Fedora":
                check_call("sudo yum install postgres-9.3".split())
            elif dist == "Ubuntu":
                check_call("sudo apt-get install postgres-9.3".split())
        except CalledProcessError e:
            print e
            print "Opening a shell to allow you to install and setup Postgres manually."
            print 'Use "exit 1" to abort installation'
            check_call(os.environ.get("SHELL", "sh"))
    else:
        print "Can only install Postgres on Linux (yet)."

if __name__ == "__main__":
    print "This script is not ready yet, refer to the homepage for installation instructions."
    sys.exit(1)
    download(DATA_URL)
    unpack(DATA_TARGET)
    download(PGBACKUP_URL)
