#!/usr/bin/env python

################################################################################
GRADLE_URL = "https://services.gradle.org/distributions/gradle-2.2.1-bin.zip"
################################################################################
# Needs requests, wget
################################################################################
import platform
import requests
from   setuptools import setup, Command
import shutil
from   subprocess import call, check_call
import sys
import tarfile
import urllib2
import zipfile
class Download(Command):
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
    
    def installPostgres():
        if platform.system() == "Linux":
            dist = platform.dist()[0]
            try:
                if dist == "Fedora":
                    check_call("sudo yum install postgres-9.3".split())
                elif dist == "Ubuntu":
                    check_call("sudo apt-get install postgres-9.3".split())
            except CalledProcessError as e:
                print e
                print "Opening a shell to allow you to install and setup Postgres manually."
                print 'Use "exit 1" to abort installation'
                check_call(os.environ.get("SHELL", "sh"))
        else:
            print "Can only install Postgres on Linux (yet)."
    
    def ask(prompt):
        return raw_input(prompt + " | ")[0].lower() in ('y', 't')

    def run():
        import argparse
        import wget
        parser = argparse.ArgumentParser(description="Setup the Watsonsim question answering system.")
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
        args = parser.parse_args()
        
        print "This script is not ready yet, refer to the homepage for installation instructions."
        sys.exit(1)
        if not ask("Are you sure you want to start? It may take many hours and 150+ GB of disk space. "):
            sys.exit(1)
        
        # The theory here is to do the smallest tasks first.
        if args.gradle:
            # Less than 5 minutes
            wget.download(GRADLE_URL)
            unpack(os.path.basename(GRADLE_URL), then_delete)
        if args.postgres:
            # Maybe about 5 minutes
            installPostgres()
        
        #http://apache.osuosl.org/jena/binaries/jena-fuseki-1.1.1-distribution.tar.gz
        #java -cp jena-fuseki-1.1.1/fuseki-server.jar tdb.tdbloader --tdb=jena-lucene.ttl *.owl *.nt
        #java -cp jena-fuseki-1.1.1/fuseki-server.jar jena.textindexer --desc=../jena-lucene.ttl

setup(
    name="Watsonsim Question Answering System",
    version="0.5",
    author="Sean Gallagher",
    author_email="stgallag@gmail.com",
    url="http://github.com/SeanTater/uncc2014watsonsim",
    setup_requires = [
        'wget>=2.2',
        'requests>=2.2.1'
    ],
    install_requires = [
        'psycopg2>=2.4.5'
    ],
    cmdclass={"download": Download}
)
