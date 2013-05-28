#!/usr/bin/awk -f
BEGIN{
    FS = " ";
    OFS = "";
    ORS = "";
    print "articleData=[";
}{
    if (NR == 1){
        for (i=1; i<=NF; i+=1){
            name[i] = $i;
        }
        next;
    }
    if (NF == 0) next;
    if ("Cluster" == $1){
        groupid = $2;
    }else if ("PageId" == $1){
        print "{\"name\":\"", $2, "\",\"type\":", groupid, ",\"vector\":[", $3, "]},\n";
        split($3, arr, ",");
        for (i=1; i<=length(arr); i+=1){
            if (max[i] < arr[i]){
                max[i] = arr[i];
            }
        }
    }else{
        print "error", $1;
    }
}END{
    print "];\n";
    print "dimension=[";
    for (i=1; i<=length(max); i+=1){
        print "{\"name\":\"", name[i], "\",\"max\":", max[i], "},\n";
    }
    print "];\n"
 }
