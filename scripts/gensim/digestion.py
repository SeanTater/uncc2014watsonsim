from gensim import corpora
import re

def filter_alnum(text):
    return re.findall("\w+", text)

def cbow_dict(source):
    return corpora.Dictionary([[w] for w in open(source).read().lower().split()])

def line_dict(source):
    return corpora.Dictionary([filter_alnum(l) for l in open(source)])

class CBOWCorpus(object):
    def __init__(self, source, dictionary):
        self.dictionary = dictionary
        self.words = [ w for w in open(source).read().split() if w not in stoplist]

    def __len__(self): # this is O(n)
        return len(self.words)-4

    def __iter__(self):
        for i in xrange(len(self.words)-4):
            yield self.dictionary.doc2bow(self.words[i:i+4])

class LineCorpus(object):
    def __init__(self, source, dictionary):
        self.source = source
        self.dictionary = dictionary

    def __len__(self):
        i=0
        for line in open(self.source):
            i += 1
        return i

    def __iter__(self):
        for line in open(self.source):
            yield self.dictionary.doc2bow(filter_alnum(line))
