#!/usr/bin/env python
from matplotlib import pyplot as plt
from sklearn.decomposition import PCA, KernelPCA
from sklearn.manifold import Isomap, TSNE
from analogy import Analogy
from vstore import VStore

a = Analogy(VStore("vectors.lmdb", "big-glove"))

buf = ""
linebuf = raw_input("Please enter some words to plot, or empty for a canned list: ")
while linebuf:
	buf += linebuf + " "
	linebuf = raw_input("... ")


labels = buf.split() \
    or "doctor nurse politician senator lawyer barrister defend accuse heal treat cure elect vote".split() 

vs = [a.w(x) for x in labels if a.w(x) is not None  ]
flatplot = TSNE(2)
ps = flatplot.fit_transform(vs)

plt.title("Reduced vector space model")
plt.xlabel("First Principal Component")
plt.ylabel("Second Principal Component")
plt.scatter(ps[:, 0], ps[:, 1])
for (x, y), label in zip(ps, labels):
    print "plotting %f, %f, %s" %(x, y, label)
    plt.annotate(label, xy = (x, y), xytext = (0, 0), textcoords = 'offset points')

plt.show()
