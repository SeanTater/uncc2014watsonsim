#!/usr/bin/env python
import logging
import os
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
from gensim import corpora, models, similarities, matutils
from stoplist import stoplist
from vstore import VStore
import digestion


SOURCE = "wiki3"

### Create the corpus out of the documents
if os.path.exists(SOURCE+'.corpus.mm'):
    # Query mode
    unidict = corpora.Dictionary.load(SOURCE = ".dict")
    unilsi = models.LsiModel.load(SOURCE + '.unilsimodel')
    uniindex = similarities.MatrixSimilarity.load(SOURCE + ".matsim")
else:
    # Index mode
    # collect statistics about all tokens
    unidict = digestion.line_dict(SOURCE);
    filter_ids = set(unidict.token2id[stopword] for stopword in stoplist
        if stopword in unidict.token2id) # stopwords
    filter_ids.update(set([unidict.token2id[fragment] for fragment in unidict.token2id
        if len(fragment) == 1])) # short words
    filter_ids.update(set([tokenid for tokenid, docfreq in unidict.dfs.iteritems()
        if docfreq == 1])) # hepax legomena
    unidict.filter_tokens(filter_ids) # remove stop words and words that appear only once
    unidict.compactify() # remove gaps in id sequence after words that were removed
    unidict.save(SOURCE + '.dict')
    print(unidict)

    #unidict = corpora.Dictionary.load("word8.dict")

    ### Preprocessing
    unicorpus = digestion.LineCorpus(SOURCE, unidict)

    ### Creating the index
    tfidf = models.TfidfModel(unicorpus)
    corpus_tfidf = tfidf[unicorpus]
    '''
    print "using LSI"
    unilsivstore = VStore(SOURCE+"vectors.lmdb", "lsi")
    unilsi = models.LsiModel(corpus_tfidf, chunksize=1000000, id2word=unidict, num_topics=300) # initialize an LSI transformation
    unilsi.save(SOURCE + '.unilsimodel')
    unilsi.print_topics(20)
    unilsivstore.drop()
    unilsivstore.load(
        (unidict[idnum].encode(), matutils.sparse2full(unilsi[[(idnum, 1)]], 300))
        for idnum in unidict)

    print "using LSI"
    unilsivstore = VStore(SOURCE+"vectors.lmdb", "lsi-notfidf")
    unilsi = models.LsiModel(unicorpus, chunksize=1000000, id2word=unidict, num_topics=300) # initialize an LSI transformation
    unilsi.save(SOURCE + '.unilsi_notfidfmodel')
    unilsi.print_topics(20)
    unilsivstore.drop()
    unilsivstore.load(
        (unidict[idnum].encode(), matutils.sparse2full(unilsi[[(idnum, 1)]], 300))
        for idnum in unidict)
    
    
    print "using LDA"
    unildavstore = VStore(SOURCE+"vectors.lmdb", "mini-plain-lda")
    unilda = models.LdaMulticore(unicorpus, id2word=unidict, num_topics=300, chunksize=25000, passes=10, iterations=50, workers=40, batch=True) # Lda
    unilda.save(SOURCE+'.unilda_model')
    unilda.print_topics(20)
    unildavstore.drop()
    unildavstore.load(
        (unidict[idnum].encode(), matutils.sparse2full(unilda[[(idnum, 1)]], 300))
        for idnum in unidict)
    '''

    print "using W2V"
    uniw2vvstore = VStore(SOURCE+"vectors.lmdb", "w2v-word8-lines")
    uniw2v = models.word2vec.Word2Vec([line.split() for line in open('word8-lines.short')], size=300, window=5, min_count=5, workers=8)
    uniw2v.save('word8-lines.short.uniw2vmodel')
    uniw2vvstore.drop()
    uniw2vvstore.load(
        (rnidict[idnum].encode(), matutils.sparse2full(uniw2v[[(idnum, 1)]], 300))
         for idnum in unidict)

    #uniindex = similarities.MatrixSimilarity(unilsi[unicorpus], num_features=300)
    #uniindex.save('word8-lines.short.matsim')

## Get a query
import sys
sys.exit()
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
    leftlsi = unilsi[leftvec]
    rightlsi = unilsi[rightvec]
    #leftlda = unilda[leftvec] # matutils.sparse2full(..., 300)
    #rightlda = unilda[rightvec]

    return {"lsi": matutils.cossim(leftlsi, rightlsi),}
        #"lda": matutils.cossim(leftlda, rightlda)}
print wordsim(left, right)
import code
code.interact(local=vars())
