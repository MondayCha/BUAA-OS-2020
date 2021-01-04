#!/bin/bash
# Task5
# mkdir file{1..100}
a=1
while [ $a -le 100 ]
do
        if [ $a -gt 70 ]
        then
            rm -rf file$a
        elif [ $a -gt 40 ]
        then
            mv file$a newfile$a
        fi
        a=$[$a+1]
done



