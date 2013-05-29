#!/usr/local/bin/python
import sys
import re
import base64
import nltk
from nltk.stem.porter import PorterStemmer
import fileinput

stemmer = PorterStemmer()
# stopwords = nltk.corpus.stopwords.words('english')
stopwords = ['i', 'me', 'my', 'myself', 'we', 'our', 'ours', 'ourselves', 'you', 'your', 'yours', 'yourself', 'yourselves', 'he', 'him', 'his', 'himself', 'she', 'her', 'hers', 'herself', 'it', 'its', 'itself', 'they', 'them', 'their', 'theirs', 'themselves', 'what', 'which', 'who', 'whom', 'this', 'that', 'these', 'those', 'am', 'is', 'are', 'was', 'were', 'be', 'been', 'being', 'have', 'has', 'had', 'having', 'do', 'does', 'did', 'doing', 'a', 'an', 'the', 'and', 'but', 'if', 'or', 'because', 'as', 'until', 'while', 'of', 'at', 'by', 'for', 'with', 'about', 'against', 'between', 'into', 'through', 'during', 'before', 'after', 'above', 'below', 'to', 'from', 'up', 'down', 'in', 'out', 'on', 'off', 'over', 'under', 'again', 'further', 'then', 'once', 'here', 'there', 'when', 'where', 'why', 'how', 'all', 'any', 'both', 'each', 'few', 'more', 'most', 'other', 'some', 'such', 'no', 'nor', 'not', 'only', 'own', 'same', 'so', 'than', 'too', 'very', 's', 't', 'can', 'will', 'just', 'don', 'should', 'now']

def stem(w):
    nw = stemmer.stem_word(w.lower())
    if len(nw) < 3 or nw in stopwords or re.match('^[0-9]*$', nw):
        return ''
    return nw

if __name__ == '__main__':
    for line in fileinput.input():
        a = {}
        l = line.split('\t')
        if len(l) != 3:
            continue
        fid = l[0].strip()
        title = base64.b64decode(l[1].strip())
        content = base64.b64decode(l[2].strip())
        for i in re.split('[^0-9A-Za-z]', title):
            w = stem(i)
            if not w : continue
            if a.has_key(w):
                a[w] += 5
            else:
                a[w] = 5
        for i in re.split('[^0-9A-Za-z]', content):
            w = stem(i)
            if not w : continue
            if a.has_key(w):
                a[w] += 1
            else:
                a[w] = 1
        for k,v in a.items():
            sys.stdout.write('%s\t%s\t%s\n' % (fid, k, v))
