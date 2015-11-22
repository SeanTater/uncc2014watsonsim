import numpy as np
import time
import math
from sklearn.svm import SVC
wout = np.load("wek2.npy")[:10000]
# X = input features
# y = output gold-standard prediction
# q = question id (for collation)
X, y, q = np.delete(wout, [8, 46], axis=1), wout[:, 8], wout[:, 46]
# border between test and training data
border = len(y) * 2/3

# Spacing of the parameters we are trying to visualize (C and gamma)
base = 10 ** (1/10.)
exp_range = range(-60, 61)

def svc((C, gamma)):
    s = SVC(C=C, gamma=gamma, probability=True)
    start = time.time()
    s.fit(X[:border], y[:border])
    train_time = time.time() - start
    pred = s.predict_proba(X[border:])[:, 0]
    test_time = (time.time() - start) - train_time

    # This is the literal is-it-the-right-answer  binary score.
    # This measure is what we try to maximize but its relation to question
    # accuracy is complicated
    accu = np.sum((pred > 0.5) == y) / len(y)

    ###  This is the actual question prediction error, in bits
    # First, find the probabilities
    pred_y = pred * y[border:] # These are the probabilities for right answers
    pred_y = pred_y[pred_y.nonzero()]   # the same, stripped of 0's
    mean_bits = np.mean(-np.log(pred_y) / np.log(2))  # measured in mean bits

    ### This is the literal accuracy - it gets complicated
    # Sort the answers by probability, descending (only getting the indices)
    confidence_order = np.argsort(pred)
    # This indexing trick always takes the last assignment for each index
    # This will hold the index of the best answer for each question
    best_answer = np.zeros(np.max(q.astype(int))+1)
    best_answer[q[confidence_order].astype(int)] = confidence_order
    # Take the average correctness of the best answer
    accu_by_q = y[border:][best_answer.astype(int)].mean()

    return [C, gamma, accu, mean_bits, accu_by_q, train_time, test_time]

import code

def multi():
    from multiprocessing import Pool
    p = Pool(40)
    ins = [(base**i, base**j) for i in exp_range for j in exp_range]
    with open("svmresults-largeimage-smallset.log", "w") as o:
        for row in p.imap_unordered(svc, ins):
            print '\t'.join(map(str, row))
            o.write('\t'.join(map(str, row)) + '\n')

code.interact(local=vars())
