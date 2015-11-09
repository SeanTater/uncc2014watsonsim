#!/usr/bin/env python
#  Provides a nice wrapper for GloVe data and other already-processed vectors
import lmdb
import numpy
numpy.set_printoptions(threshold=20)

class VStore(object):
	_allenvs = {}
	def __init__(self, filename, name):
		''' Create a lmdb-backed VStore using a cached environment '''
		if filename not in self._allenvs:
			self._allenvs[filename] = lmdb.Environment(filename,
				map_size=100<<30,
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

	def read(self):
		''' Get all the vectors from the database '''
		with self._txn(write=False) as txn:
			for key, value in txn.cursor():
				yield (key, numpy.frombuffer(value, dtype=numpy.float32))

	def load(self, gen):
		''' Put() into the database many (name, vector) pairs '''
		with self._txn(write=True) as txn:
			try:
				for name, value in gen:
					txn.put(name, numpy.getbuffer(value))
			except lmdb.BadValsizeError as e:
				print name, value.shape, value
