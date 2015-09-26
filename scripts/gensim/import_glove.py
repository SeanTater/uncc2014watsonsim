#!/usr/bin/env python
# Loads GloVe data into a VStore
import sys
import argparse
import numpy
from vstore import VStore

parser = argparse.ArgumentParser(description="Load GloVe vectors into LMDB")
parser.add_argument("--name", action="store", type=str, default="glove",
	help="name of the database into which to load the vectors")
parser.add_argument("--dbfile", action="store", type=str, default="vectors.lmdb",
	help="shared database filename")
parser.add_argument("--merge", action="store_true",
	help="merge the new dataset, rather than replacing")
parser.add_argument("source", type=file,
	help="uncompressed GloVe dataset")
args = parser.parse_args()

# Invert control in order to use one transaction
table = VStore(args.dbfile, args.name)
table.drop()
def loader():
	for loaded, line in enumerate(args.source):
		line = line.split()
		name = line.pop(0)
		## Tokenization errors can cause a word to be too long for lmdb
		if len(name) > 100:
			continue
		if loaded % 10000 == 0:
			print "Loaded {} rows".format(loaded)

		yield name, numpy.array(line, dtype=numpy.float32)
table.load(loader())
print "Finished loading"
