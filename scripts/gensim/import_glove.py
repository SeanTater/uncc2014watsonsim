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
parser.add_argument("source", type=file, mode="r",
	help="uncompressed GloVe dataset")
args = parser.parse_args()

table = VStore(args.dbfile, args.name)
for loaded, line in enumerate(args.source):
	line = line.split()
	name = line.pop(0)
	table.put(name, numpy.array(line, dtype=numpy.float32))
	if loaded % 1000 == 0:
		print "Loaded {} rows".format(loaded)

print "Finished {}.".format(loaded)
