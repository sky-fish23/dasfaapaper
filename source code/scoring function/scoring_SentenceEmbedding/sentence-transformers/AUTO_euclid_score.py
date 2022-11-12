Kset = set()

with open("./AUTOMCS/MCS1.txt") as f:
    K1 = f.read().split("\n\n")
    for axiom in K1:
        Kset.add(axiom)
    f.close()

with open("./AUTOMCS/MCS2.txt") as f:
    K2 = f.read().split("\n\n")
    for axiom in K2:
        Kset.add(axiom)
    f.close()

with open("./AUTOMCS/MCS3.txt") as f:
    K3 = f.read().split("\n\n")
    for axiom in K3:
        Kset.add(axiom)
    f.close()

with open("./AUTOMCS/MCS4.txt") as f:
    K4 = f.read().split("\n\n")
    for axiom in K4:
        Kset.add(axiom)
    f.close()

with open("./AUTOMCS/MCS5.txt") as f:
    K5 = f.read().split("\n\n")
    for axiom in K5:
        Kset.add(axiom)
    f.close()

with open("./AUTOMCS/MCS6.txt") as f:
    K6 = f.read().split("\n\n")
    for axiom in K6:
        Kset.add(axiom)
    f.close()

K = list(Kset)
mc_K = [K1,K2,K3,K4,K5,K6]

print("data loaded!")

import sys

sys.path.append('..')
from sentence_transformers import SentenceTransformer, LoggingHandler
import numpy as np
import logging


'''
sim_model = Similarity()

def sim(s1, s2):
    return sim_model.get_score(s1, s2)

mat = [([0] * len(K)) for i in range(len(K))]
'''
sent_index = {}
index_sent = {}
index = 0
for sentence in K:
    sent_index[sentence] = index
    index_sent[index] = sentence
    index += 1
'''
for i in range(len(K)):
    for j in range(i, len(K)):
        mat[i][j] = sim(index_sent[i], index_sent[j])

for i in range(len(K)):
    for j in range(i):
        mat[i][j] = mat[j][i]
'''
                                                      
#model = SentenceTransformer('paraphrase-albert-small-v2')  # albert
model = SentenceTransformer('all-distilroberta-v1')          # RoBERTa

sentence_embeddings = model.encode(K)

# sent_bert_scores = model1.similarity(K, K)

mat = [([0] * len(K)) for i in range(len(K))]

def cos_sim(s1, s2):
    v1 = np.array(sentence_embeddings[sent_index[s1]])
    v2 = np.array(sentence_embeddings[sent_index[s2]])
    v = sum(v1*v2) / ((sum(v1**2) * sum(v2**2))**0.5)
    return (1+v) / 2

def euc_sim(s1, s2):
    v1 = np.array(sentence_embeddings[sent_index[s1]])
    v2 = np.array(sentence_embeddings[sent_index[s2]])
    return 1 / (1 + (sum((v1-v2)**2))**0.5)

for i in range(len(K)):
    for j in range(i, len(K)):
        mat[i][j] = euc_sim(index_sent[i], index_sent[j])

for i in range(len(K)):
    for j in range(i):
        mat[i][j] = mat[j][i]

print("similarity compute completed! ")

print("Sentence-BERT ########################################")
######################### Method 1 ###################################

def agg_all(Ki, alpha):
    res = 0
    for beta in Ki:
        res += mat[sent_index[beta]][sent_index[alpha]]
    return res / len(Ki)


def mc_all(alpha):
    return sum([(agg_all(Ki, alpha) + 1) for Ki in mc_K])

def score_all(Ki):
    res = 0
    for alpha in Ki:
        mc = mc_all(alpha)
        res += mc
    return res


cnt = 1
for Ki in mc_K:
    print("Method 1 K{0}  score: {1}".format(cnt, score_all(Ki)))
    cnt += 1

######################### Method 2 ###################################

def equalHead(alpha, beta):
    if len(alpha) == 0 and len(beta) == 0:
        return True
    if len(alpha) == 0 or len(beta) == 0:
        return False
    return alpha.split()[0] == beta.split()[0]

def equalTail(alpha, beta):
    if len(alpha) == 0 and len(beta) == 0:
        return True
    if len(alpha) == 0 or len(beta) == 0:
        return False
    return alpha.split()[-1] == beta.split()[-1]

def H(Ki, alpha):
    h = []
    for beta in Ki:
        if equalHead(beta, alpha):
            h.append(beta)
    return h

def T(Ki, alpha):
    t = []
    for beta in Ki:
        if equalTail(beta, alpha):
            t.append(beta)
    return t

def agg_local_H(Ki, alpha):
    h = H(Ki, alpha)
    if len(h) == 0:
        return 0
    else:
        return sum([mat[sent_index[alpha]][sent_index[beta]] for beta in h]) / len(h)

def agg_local_T(Ki, alpha):
    t = T(Ki, alpha)
    if len(t) == 0:
        return 0
    else:
        return sum([mat[sent_index[alpha]][sent_index[beta]] for beta in t]) / len(t)


def mc_local(alpha):
    res = 0
    for Ki in mc_K:
        if alpha in Ki:
            res += ((agg_local_H(Ki, alpha)+agg_local_T(Ki, alpha))/2 + 1)
    return res

def score_local(Ki):
    res = 0
    for alpha in Ki:
        mc = mc_local(alpha)
        res += mc
    return res

cnt = 1
for Ki in mc_K:
    print("Method 2 K{0}  score: {1}".format(cnt, score_local(Ki)))
    cnt += 1
