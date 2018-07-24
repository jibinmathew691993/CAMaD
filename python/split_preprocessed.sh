#!/bin/bash
#======================================
#	split_preprocessed.sh
# split preprocessed data into train / test. 
# this script gives you 4 train/test pairs
#======================================

c="../data/cross"
if [ -d $c ]; then
	echo "cross dir exists : $c"
	echo "the process aborted"
	exit
fi
p="../data/preprocessed"
if [ ! -d $p ]; then
	echo "no preprocessed dir : $p" 
	echo "the process aborted"
	exit
fi
python src/data/Held_out_Data.py --dirs "../data/preprocessed/chart_01","../data/preprocessed/chart_02","../data/preprocessed/chart_03","../data/preprocessed/chart_04" --out "../data/cross"
