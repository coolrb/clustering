#include <iostream>
#include <fstream>
#include <cmath>
#include <sstream>
#include <cstdio>
#include <ctime>
#include <cstring>
#include <cstdlib>
#include <algorithm>
#include <vector>
using namespace std;
#define SZ(v) ((int)(v).size())
const double eps = 1e-10;
const int K = 20;
int sgn(double x) {
    return (x > eps) - (x < -eps);
}

struct Page {
    string id;
    vector<double> features;
    
    Page() {
        id = "";
    }
};

vector<Page> pageList;

bool cmp(Page a, Page b) {
    int n = SZ(a.features);
    for (int i = 0; i < n; ++i) {
        double x = a.features[i], y = b.features[i];
        if (sgn(x - y) == 0) continue;
        return sgn(x - y) < 0;
    }
    return true;
}

bool getInput(char fileName[]) {
    pageList.clear();
    // string fileName;
    // cin >> fileName;
    // ifstream myFile(fileName.c_str());
    ifstream myFile(fileName);
    if (!myFile) return false;
    string vt, id;
    getline(myFile, vt);
    while (getline(myFile, vt)) {
        istringstream sin(vt);
        Page x;
        sin >> id;
        x.id = id;
        double tmp;
        while (sin >> tmp) {
            x.features.push_back(tmp);
        }
        pageList.push_back(x);
    }
    return true;
}

double getDist(const Page &mean, const Page &list) {
    double sum = 0;
    for (int i = 0; i < SZ(mean.features); ++i) {
        double tmp = mean.features[i] - list.features[i];
        sum += tmp * tmp;
    }
    return sqrt(sum);
}

double calc(const Page *means, const vector<Page> cluster[K]) {
    double res = 0;
    for (int i = 0; i < K; ++i) {
        for (int j = 0; j < SZ(cluster[i]); ++j) {
            res += getDist(means[i], cluster[i][j]);
        }
    }
    return res;
}

int classify(const Page *means, const Page &list) {
    int res = 0;
    double x = getDist(means[0], list);
    for (int i = 1; i < K; ++i) {
        double tmp = getDist(means[i], list);
        if (sgn(tmp - x) < 0) {
            x = tmp;
            res = i;
        }
    }
    return res;
}

void findMeans(Page *means, const vector<Page> cluster[K]) {
    int *t = new int[K]; 
    for (int i = 0; i < K; ++i) {
        memset(t, 0, sizeof(t));
        for (int j = 0; j < K; ++j) {
            for (int k = 0; k < SZ(cluster[i]); ++k) {
                t[j] += cluster[i][k].features[j];
            }
            if (SZ(cluster[i])) t[j] /= SZ(cluster[i]);
        }
        if (SZ(cluster[i])) for (int j = 0; j < K; ++j) means[i].features.push_back(t[j]);
    }
    delete(t);
}

void print(const vector<Page> cluster[K]) {
    for (int i = 0; i < K; ++i) {
        printf("Cluster %d:\n", i + 1);
        for (int j = 0; j < SZ(cluster[i]); ++j) {
            Page res = cluster[i][j];
            cout << "PageId " << res.id << " (";
            if (!SZ(res.features)) {
                printf(")\n");
                continue;
            }
            for (int k = 0; k < SZ(res.features); ++k) {
                if (k) printf(",");
                printf("%.10lf", res.features[k]);
            }
            printf(")\n");
        }
        puts("");
    }
}

bool ok(const Page *oldMeans, const Page *newMeans) {
    int n = SZ(oldMeans[0].features);
    for (int i = 0; i < K; ++i) {
        for (int j = 0; j < n; ++j) {
            if (sgn(oldMeans[i].features[j] - newMeans[i].features[j]) != 0) return false;
        }
    }
    return true;
}

void kmeans() {
    bool *used = new bool[SZ(pageList)];
    vector<Page> cluster[K];
    Page means[K];
    //srand(time(0));
    srand(1000);
    int cnt = 0;
    while (cnt < K) {
        int point = rand() % SZ(pageList);
        if (!used[point]) {
            means[cnt] = pageList[point];
            used[point] = true;
            ++cnt;
        }
    }
    delete(used);
    for (int i = 0; i < SZ(pageList); ++i) {
        int clas = classify(means, pageList[i]);
        cluster[clas].push_back(pageList[i]);
    }
    Page newMeans[K];
    for (int i = 0; i < K; ++i) newMeans[i] = means[i];
    Page oldMeans[K];
    while (!ok(oldMeans, newMeans)) {
        findMeans(means, cluster);
        for (int i = 0; i < K; ++i) cluster[i].clear();
        for (int i = 0; i < SZ(pageList); ++i) {
            int clas = classify(means, pageList[i]);
            cluster[clas].push_back(pageList[i]);
        }
        for (int i = 0; i < K; ++i) {
            oldMeans[i] = newMeans[i];
            newMeans[i] = means[i];
        }
    }
    print(cluster);
}

int main(int argc, char * argv[]) {
    freopen(argv[2], "w", stdout);
    bool init = getInput(argv[1]);
    sort(pageList.begin(), pageList.end(), cmp);
    if (init) kmeans();
    else printf("The file is empty!\n");
    return 0;
}

