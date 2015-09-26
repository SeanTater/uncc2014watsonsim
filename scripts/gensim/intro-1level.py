import logging
import os
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
from gensim import corpora, models, similarities

# remove common words and tokenize
stoplist = set('for a of the and to in'.split())

### Create the corpus out of the documents
if os.path.exists('word8-lines.short.corpus.mm'):
    dictionary = corpora.Dictionary.load("word8-lines.short.dict")
    corpus = corpora.MmCorpus('word8-lines.short.corpus.mm')
    lsi = models.LsiModel.load('word8-lines.short.lsimodel')
    index = similarities.MatrixSimilarity.load("word8-lines.short.matsim")
else:
    # collect statistics about all tokens
    dictionary = corpora.Dictionary(line.lower().split() for line in open('word8-lines.short'))
    # remove stop words and words that appear only once
    stop_ids = [dictionary.token2id[stopword] for stopword in stoplist
             if stopword in dictionary.token2id]
    once_ids = [tokenid for tokenid, docfreq in dictionary.dfs.iteritems() if docfreq == 1]
    dictionary.filter_tokens(stop_ids + once_ids) # remove stop words and words that appear only once
    dictionary.compactify() # remove gaps in id sequence after words that were removed
    dictionary.save('word8-lines.short.dict')
    print(dictionary)

    ### Preprocessing
    class MyCorpus(object):
        def __len__(self):
            i=0
            for line in open("word8-lines.short"):
                i += 1
            return i

    	def __iter__(self):
    		for line in open('word8-lines.short'):
    			# assume there's one document per line, tokens separated by whitespace
    			yield dictionary.doc2bow(line.lower().split())

    corpus = MyCorpus()
    corpora.MmCorpus.serialize('word8-lines.short.corpus.mm', corpus) # store to disk, for later use

    ### Creating the index
    tfidf = models.TfidfModel(corpus)
    corpus_tfidf = tfidf[corpus]
    lsi = models.LsiModel(corpus_tfidf, id2word=dictionary, num_topics=300) # initialize an LSI transformation
    lsi.save('word8-lines.short.lsimodel')
    #corpus_lsi = lsi[corpus_tfidf] # create a double wrapper over the original corpus: bow->tfidf->fold-in-lsi

    index = similarities.MatrixSimilarity(lsi[corpus], num_features=300)
    index.save('word8-lines.short.matsim')

## Get a query
query = raw_input("Search: ")
while query:
    vec = dictionary.doc2bow(query.lower().split())

    sims = index[lsi[vec]]
    print(sorted(list(enumerate(sims)), key=lambda x: -x[1])[:20])
    query = raw_input("Search: ")
