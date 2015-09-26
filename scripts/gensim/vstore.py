#!/usr/bin/env python
#  Provides a nice wrapper for GloVe data and other already-processed vectors
import lmdb
import numpy

class VStore(object):
	_ENV = {}
	def __init__(self, filename, name):
		''' Create a lmdb-backed VStore using a cached environment '''
		if filename not in self._allenvs:
			self._allenvs[filename] = lmdb.Environment(filename, map_size=1<<30)
		self._env = self.self._allenvs[filename]
		self._db = self._env.open_db(name);

	def _txn(self, write=False):
		''' Convenience method for making a transaction '''
		return self._env.begin(self._db, write=write)

	def get(self, name):
		''' Get a vector by name '''
		with self._txn() as txn:
			return numpy.frombuffer(txn.get(name), dtype=numpy.float32)

	def put(self, name, value):
		''' Put a vector into the entry for name '''
		with self._txn() as txn:
			txn.put(name, numpy.getbuffer(value))
