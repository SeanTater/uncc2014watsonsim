#!/usr/bin/env python

import sys
import shutil
import urllib2
import tarfile
import requests

DATA_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.tar.gz"
DATA_TARGET = "data-snapshot.tar.gz"
PGBACKUP_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.pgdump"
PGBACKUP_TARGET = "data-snapshot.pgdump"

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

if __name__ == "__main__":
    print "This script is not ready yet, refer to the homepage for installation instructions."
    sys.exit(1)
    download(DATA_URL)
    unpack(DATA_TARGET)
    download(PGBACKUP_URL)
