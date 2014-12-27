#!/bin/csh -f 

set TYPE = $1
set OUTPUT = $2
set THRESHOLD = $3
# ./eval.sh Dev devoutput.txt 40
./trec_eval-8.0/trec_eval -q -c ${TYPE}-T${THRESHOLD}.judgement $OUTPUT > ! ${OUTPUT}.treceval
tail -29 ${OUTPUT}.treceval
exit 0
