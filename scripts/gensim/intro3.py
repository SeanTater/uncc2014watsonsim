import logging
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
from gensim import corpora, models, similarities

### Preprocessing
'''documents = ["Human machine interface for lab abc computer applications",
              "A survey of user opinion of computer system response time",
              "The EPS user interface management system",
              "System and human system engineering testing of EPS",
              "Relation of user perceived response time to error measurement",
              "The generation of random binary unordered trees",
              "The intersection graph of paths in trees",
              "Graph minors IV Widths of trees and well quasi ordering",
              "Graph minors A survey"]'''

class MyCorpus(object):
	def __iter__(self):
		for line in open('word8-lines.short'):
			# assume there's one document per line, tokens separated by whitespace
			yield dictionary.doc2bow(line.lower().split())

corpus = MyCorpus()


# remove common words and tokenize
stoplist = set('for a of the and to in'.split())
'''texts = [[word for word in document.lower().split() if word not in stoplist]
      for document in documents]

# remove words that appear only once
from collections import defaultdict
frequency = defaultdict(int)
for text in texts:
	for token in text:
		frequency[token] += 1

texts = [[token for token in text if frequency[token] > 1]
      for text in texts]

from pprint import pprint   # pretty-printer
pprint(texts)

### Get the dictionary and an id for every word
dictionary = corpora.Dictionary(texts)
dictionary.save('/tmp/deerwester.dict') # store the dictionary, for future reference
print(dictionary)'''


# collect statistics about all tokens
dictionary = corpora.Dictionary(line.lower().split() for line in open('word8-lines.short'))
# remove stop words and words that appear only once
stop_ids = [dictionary.token2id[stopword] for stopword in stoplist
         if stopword in dictionary.token2id]
once_ids = [tokenid for tokenid, docfreq in dictionary.dfs.iteritems() if docfreq == 1]
dictionary.filter_tokens(stop_ids + once_ids) # remove stop words and words that appear only once
dictionary.compactify() # remove gaps in id sequence after words that were removed
print(dictionary)


### Create the corpus out of the documents
'''corpus = [dictionary.doc2bow(text) for text in texts]'''
corpora.MmCorpus.serialize('/tmp/mycorpus.mm', corpus) # store to disk, for later use


### Creating the index
tfidf = models.TfidfModel(corpus)
corpus_tfidf = tfidf[corpus]
lsi = models.LsiModel(corpus_tfidf, id2word=dictionary, num_topics=300) # initialize an LSI transformation
corpus_lsi = lsi[corpus_tfidf] # create a double wrapper over the original corpus: bow->tfidf->fold-in-lsi

index = similarities.SparseMatrixSimilarity(lsi[corpus], num_features=10000)

## Get a query
query = raw_input("Search: ")
vec = dictionary.doc2bow(query.lower().split())

sims = index[lsi[vec]]


print(sorted(list(enumerate(sims)), key=lambda x: -x[1])[:20])
