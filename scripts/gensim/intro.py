import logging
import os
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
from gensim import corpora, models, similarities, matutils
from stoplist import stoplist

### Create the corpus out of the documents
if os.path.exists('word8-lines.short.corpus.mm'):
    # Query mode
    unidict = corpora.Dictionary.load("word8-lines.short.dict")
    #unicorpus = corpora.MmCorpus('word8-lines.short.corpus.mm')
    unilsi = models.LsiModel.load('word8-lines.short.unilsimodel')
    uniindex = similarities.MatrixSimilarity.load("word8-lines.short.matsim")
else:
    # Index mode
    # collect statistics about all tokens
    unidict = corpora.Dictionary(line.lower().split() for line in open('word8-lines.short'))
    # remove stop words and words that appear only once
    stop_ids = [unidict.token2id[stopword] for stopword in stoplist
        if stopword in unidict.token2id]
    once_ids = [tokenid for tokenid, docfreq in unidict.dfs.iteritems()
        if docfreq == 1]
    unidict.filter_tokens(stop_ids + once_ids) # remove stop words and words that appear only once
    unidict.compactify() # remove gaps in id sequence after words that were removed
    unidict.save('word8-lines.short.dict')
    print(unidict)

    ### Preprocessing
    class MyCorpus(object):
        def __len__(self): # this is O(n)
            i=0
            for line in open("word8-lines.short"):
                i += 1
            return i

    	def __iter__(self):
    		for line in open('word8-lines.short'):
    			# assume there's one document per line, tokens separated by whitespace
    			yield unidict.doc2bow(line.lower().split())

    unicorpus = MyCorpus()
    #corpora.MmCorpus.serialize('word8-lines.short.corpus.mm', unicorpus) # store to disk, for later use

    ### Creating the index
    tfidf = models.TfidfModel(unicorpus)
    corpus_tfidf = tfidf[unicorpus]

    print "using LSI"
    unilsi = models.LsiModel(corpus_tfidf, id2word=unidict, num_topics=300) # initialize an LSI transformation
    unilsi.save('word8-lines.short.unilsimodel')
    unilsi.print_topics(20)

    #print "using LDA"
    #unilda = models.LdaModel(corpus_tfidf, id2word=unidict, num_topics=300) # Lda
    #unilda.save('word8-lines.short.unildamodel')
    #unilda.print_topics(20)
    #corpus_lsi = unilsi[corpus_tfidf] # create a double wrapper over the original corpus: bow->tfidf->fold-in-lsi

    uniindex = similarities.MatrixSimilarity(unilsi[unicorpus], num_features=300)
    uniindex.save('word8-lines.short.matsim')

## Get a query
'''
query = raw_input("Search: ")
while query:
    vec = unidict.doc2bow(query.lower().split())

    sims = uniindex[unilsi[vec]]
    print(sorted(list(enumerate(sims)), key=lambda x: -x[1])[:20])
    query = raw_input("Search: ")
'''
left = raw_input("Part 1: ")
right = raw_input("Part 2: ")

def wordsim(left, right):
    leftvec = unidict.doc2bow(left.lower().split())
    rightvec = unidict.doc2bow(right.lower().split())
    leftlsi = unilsi[leftvec] #
    rightlsi = unilsi[rightvec]
    #leftlda = unilda[leftvec] # matutils.sparse2full(..., 300)
    #rightlda = unilda[rightvec]

    return {"lsi": matutils.cossim(leftlsi, rightlsi),}
        #"lda": matutils.cossim(leftlda, rightlda)}
print wordsim(left, right)
import code
code.interact(local=vars())
