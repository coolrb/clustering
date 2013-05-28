#!/usr/bin/awk -f
BEGIN{
    FS = " ";
    OFS = "";
    ORS = "";
}{
    if (NR == FNR){
        new[$1] = $2
    }else{
        if (FNR == 1){
            for (i=1; i<=NF; i+=1){
                name[i] = $i;
            }
            next;
        }
        if (NF == 0) next;
        if ("Cluster" == $1){
            print "\n", $1, $2, "\n";
        }else if ("PageId" == $1){
            system("echo "new[$2]"| base64 -d");
            print "\n"
        }else{
            print "error", $1;
        }
    }
}
