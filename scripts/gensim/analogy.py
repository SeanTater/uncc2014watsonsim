#!/usr/bin/env python
from vstore import VStore
from argparse import ArgumentParser
import numpy as np
import code
from sklearn.decomposition import PCA

class Analogy(object):
	def __init__(self, model):
		self.model = model
		self.pca = None
	
	def w(self, word):
		'''Get the vector for a word - a short alias'''
		r = self.model.get(word)
		if r is None:
			print "Model {} has no vector for {}.".format(self.model, word)
		return r
	
	def pcaw(self, word):
		if self.pca is None: return None
		return self.pca.transform(self.w(word))
	
	def context(self, words, components=100):
		self.pca = PCA(components)
		self.pca.fit([self.w(x) for x in words])
		
	@staticmethod
	def sim(left, right):
		'''Compare two dense vectors using cosine similarity'''
		if left is not None and right is not None:
			return (
				np.sum(left*right) /
				(np.sqrt(np.sum(left**2)) * np.sqrt(np.sum(right**2)))
			)
		return None

if __name__ == '__main__':
	np.set_printoptions(threshold=20)

	parser = ArgumentParser(description="Perform simple algebra on words")
	parser.add_argument("--dbfile",
		default="vectors.lmdb",
		help="use this database file to get vectors")
	parser.add_argument("model", action="store",
 		help="compare using this model database (e.g. glove)")
	args = parser.parse_args()
	model = VStore(args.dbfile, args.model)

	analogy = Analogy(model)
	w = analogy.w
	pcaw = analogy.pcaw
	context = analogy.context
	sim = analogy.sim
	print "What follows is a python prompt."
	print "w('elicidate') --> vector for `elicidate`"
	print "w('mogrify') + w('frobnicate') --> vector sum"
	print "    same for -, *, /, **, etc as usual for numpy"
	print "sim(w('republican'), w('democrat')) -> society in a 32bit float"
	print "    (actually a simple cosine similarity)"
	code.interact(local=vars())
