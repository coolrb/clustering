#!/bin/bash
cd $(dirname "$0")
WORKDIR=`pwd`
SPIDERDIR=${WORKDIR}/spider
MAPREDDIR=${WORKDIR}/mapred
KMEANSDIR=${WORKDIR}/kmeans
CHARTDIR=${WORKDIR}/chart

function q {
    echo "ERROR!" $1
    exit 1
}

function c {
    if [ $? != 0 ]; then
        q $1
    fi
}

function cf {
    if [ ! -f $1 ]; then
        q "file $1 not found"
    fi
}

function env_checker {
    java -version 2>&1 | grep 1.7 > /dev/null || q "java version >= 1.7 needed"
    python --version 2>&1 | grep 2.7 > /dev/null || q "python version >= 2.7 needed"
    g++ --version | grep "4\." > /dev/null || q "g++ version >= 4.2 needed"
    awk --version 2>&1 > /dev/null || q "awk needed"
}

# spider by Chao Peng
function spider {
    echo "start spider ..."
    cd $SPIDERDIR
    java -jar Spider.jar
    c "spider run error"
}

# mapper by Xiangrong Hao & reducer by Xue Tian
function mapred {
    echo "start mapreduce ..."
    cd $MAPREDDIR
    cf ${SPIDERDIR}/all_news.txt
    cat ${SPIDERDIR}/all_news.txt | python mapper.py | python reducer.py > vector_map
    c "mapred run error"
}

# k-means by Hone Zhang
function kmeans {
    echo "start kmeans .."
    cd $KMEANSDIR
    cf ${MAPREDDIR}/vector_map
    g++ kmeans.cpp -o kmeans
    ./kmeans ${MAPREDDIR}/vector_map result
    c "kmeans run error"
}

# chart by Xiaokang Sun
function chart {
    echo "start chart .."
    cd $CHARTDIR
    cf ${KMEANSDIR}/result
    awk -f format.awk ${SPIDERDIR}/all_news.txt  ${KMEANSDIR}/result > data
    awk -f formatjs.awk ${SPIDERDIR}/all_news.txt  ${KMEANSDIR}/result> data.js
}

env_checker
spider
mapred
kmeans
chart
