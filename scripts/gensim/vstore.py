#!/usr/bin/env python
#  Provides a nice wrapper for GloVe data and other already-processed vectors
import lmdb
import numpy

class VStore(object):
	_allenvs = {}
	def __init__(self, filename, name):
		''' Create a lmdb-backed VStore using a cached environment '''
		if filename not in self._allenvs:
			self._allenvs[filename] = lmdb.Environment(filename,
				map_size=1<<30,
				max_dbs=100)
		self._env = self._allenvs[filename]
		self._db = self._env.open_db(name);

	def _txn(self, write=False):
		''' Convenience method for making a transaction '''
		return self._env.begin(self._db, write=write)

	def get(self, name, default=None):
		''' Get a vector by name '''
		with self._txn() as txn:
			r = txn.get(name)
			if r is None:
				return default
			else:
				return numpy.frombuffer(r, dtype=numpy.float32)

	def drop(self):
		'''Drop everything in a database'''
		with self._txn(write=True) as txn:
			txn.drop(self._db, delete=False)

	def put(self, name, value):
		''' Put a vector into the entry for name '''
		with self._txn(write=True) as txn:
			txn.put(name, numpy.getbuffer(value))

	def load(self, gen):
		with self._txn(write=True) as txn:
			for name, value in gen:
				txn.put(name, numpy.getbuffer(value))
