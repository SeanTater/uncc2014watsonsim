#!/usr/bin/env python

################################################################################
DATA_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.tar.gz"
PGBACKUP_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.pgdump"
GRADLE_URL = "https://services.gradle.org/distributions/gradle-2.2.1-bin.zip"
INDRI_URL = "https://dl.dropboxusercontent.com/u/92563044/watsonsim/indri-5.8.tar.gz"
SRPARSER_URL = "http://nlp.stanford.edu/software/stanford-srparser-2014-08-28-models.jar"
FUSEKI_URL = "http://apache.osuosl.org/jena/binaries/jena-fuseki-1.1.1-distribution.tar.gz"
DBPEDIA_URLS = [
    "http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/dbpedia_2014.owl.bz2",
    "http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/instance_types_en.nt.bz2",
    "http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/labels_en.nt.bz2",
    # We don't use this one yet:
    "http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/en/instance_types_heuristic_en.nt.bz2",
    # This last one is from DBPedia NLP, we don't use it yet either
    "http://wifo5-04.informatik.uni-mannheim.de/downloads/datasets/genders_en.nt.bz2"
    ]
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
        parser.add_argument("--no-fuseki",
            action="store_false",
            dest='fuseki',
            default=True,
            help="Don't use Fuseki to index DBPedia")
        parser.add_argument("--no-srparser",
            action="store_false",
            dest='srparser',
            default=True,
            help="Don't download the NLP Shift Reduce Parser Models.")
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
            action='store_true', 
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
            wget.download(GRADLE_URL)
            unpack(os.path.basename(GRADLE_URL), then_delete)
        if args.srparser:
            # Less than 5 minutes probably
            wget.download(SRPARSER_URL)
        if args.postgres:
            # Maybe about 5 minutes
            installPostgres()
        if args.fuseki:
            wget.download(FUSEKI_URL)
            unpack(os.path.basename(FUSEKI_URL, then_delete))
        
        #http://apache.osuosl.org/jena/binaries/jena-fuseki-1.1.1-distribution.tar.gz
        #java -cp jena-fuseki-1.1.1/fuseki-server.jar tdb.tdbloader --tdb=jena-lucene.ttl *.owl *.nt
        #java -cp jena-fuseki-1.1.1/fuseki-server.jar jena.textindexer --desc=../jena-lucene.ttl
        
        if args.indri:
            # Maybe 15 minutes
            wget.download(INDRI_URL)
            unpack(os.path.basename(INDRI_URL), then_delete)
            #installIndri
        if args.database:
            # Several hours
            wget.download(PGBACKUP_URL)
            #restorePgbackup
        if args.indices:
            # Several more hours
            wget.download(DATA_URL)
            unpack(os.path.basename(DATA_URL), then_delete) # A stretch since URL's are not paths

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
