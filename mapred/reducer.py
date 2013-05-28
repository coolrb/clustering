#!/usr/local/bin/python
import sys
import math
import fileinput

tf = {}
idf = {}
wfilter = {}
for line in fileinput.input():
    f, w, n = line.split()
    n = int(n)
    if not tf.has_key(f):
        tf[f] = {}
    tf[f][w] = n

    if wfilter.has_key(w):
        wfilter[w] += n
    else:
        wfilter[w] = n
        
    if idf.has_key(w):
        idf[w] += 1
    else:
        idf[w] = 1

# l = idf.keys()
l = [i for i in idf.keys() if wfilter[i] > 8]

idf_len = len(l)
for k,v in tf.items():
    for k1, v1 in v.items():
        r = v1 * math.log(idf_len / idf[k1]) / math.log(2)
        tf[k][k1] = r

sys.stdout.write('%s\n' % '\t'.join(l))
for k,v in tf.items():
    sys.stdout.write('%s' % k)
    for i in l:
        if v.has_key(i):
            sys.stdout.write('\t%s' % v[i])
        else:
            sys.stdout.write('\t0')
    sys.stdout.write('\n')
