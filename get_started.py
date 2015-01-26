#!/usr/bin/env python

################################################################################
DATA_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.tar.gz"
PGBACKUP_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.pgdump"
GRADLE_URL = "https://services.gradle.org/distributions/gradle-2.2.1-bin.zip"
INDRI_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/indri-5.8.tar.gz"
################################################################################
# Needs requests, wget
################################################################################
import platform
import requests
import shutil
from subprocess import call, check_call
import sys
import tarfile
import zipfile
import urllib2
import wget

def download(url, name):
    """ Download a file and give a little feedback on the screen at the same time. """
    print "Downloading %s. 1 \".\" = 1 MB: " % url
    # We know the URLs beforehand and this method works fine
    base = url.split('/')[-1]
    with open(base, 'wb') as out:
        r = requests.get(url, stream=True)
        # Even a megabyte at a time is a pretty busy screen from what I see.
        for block in r.iter_content(1024 * 1024):
            sys.stdout.write('.')
            out.write(block)

def unpack(ar, delete):
    """ Unpack a file and delete the original """
    print "Unpacking %s" %ar
    if ar.endswith("tar"):
        tarfile.open(ar).extractall()
    elif ar.endswith("zip"):
        zipfile.Zipfile(ar, "r").extractall()
    else:
        print "Could not recognize file format of %s. Aborting unpack." %ar
        return # Skip the possible delete
    if delete: os.remove(ar)

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

def ask(prompt):
    return raw_input(prompt + " | ")[0].lower() in ('y', 't')

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(description="Setup the Watsonsim question answering system.")
    parser.add_argument("--no-indices",
        action="store_false",
        dest='indices',
        default=True,
        help="Don't download indices (50-75GB).")
    parser.add_argument("--no-indri",
        action="store_false",
        dest='indri',
        default=True,
        help="Don't compile and install Indri search engine.")
    parser.add_argument("--no-database",
        action="store_false",
        dest='database',
        default=True,
        help="Don't download and unpack the central database dump.")
    parser.add_argument("--no-postgres",
        action="store_false",
        dest='postgres',
        default=True,
        help="Don't install postgresql server (which would be from the repository).")
    parser.add_argument("--no-gradle",
        action="store_false",
        dest='gradle',
        default=True,
        help="Don't download and install gradle.")
    parser.add_argument("-d", "--delete-archives",
        action='store_true", 
        dest='delete-archives',
        default=False,
        help="Delete the downloaded archives and build directories when finished.")
    args = parser.parse_args()
    
    print "This script is not ready yet, refer to the homepage for installation instructions."
    sys.exit(1)
    if not ask("Are you sure you want to start? It may take many hours and 150+ GB of disk space. "):
        sys.exit(1)
    
    # The theory here is to do the smallest tasks first.
    if args.gradle:
        # Less than 5 minutes
        download(GRADLE_URL)
        unpack(os.path.basename(GRADLE_URL), then_delete)
    if args.postgres:
        # Maybe about 5 minutes
        pass
        #installPostgres
    if args.indri:
        # Maybe 15 minutes
        download(INDRI_URL)
        unpack(os.path.basename(INDRI_URL), then_delete)
        #installIndri
    if args.database:
        # Several hours
        download(PGBACKUP_URL)
        #restorePgbackup
    if args.indices:
        # Several more hours
        download(DATA_URL)
        unpack(os.path.basename(DATA_URL), then_delete) # A stretch since URL's are not paths
