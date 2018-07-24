#!/bin/bash
#======================================================
#		download.sh
# this is script to download corpus files.
# NOTE) it requires API key (see: https://dev.elsevier.com)
#======================================================

#API KEY
k=$1
if [ -z $k ]; then
	echo "API key is required for the first argument."
	echo "Please visit (https://dev.elsevier.com) for API key"
	echo "the process aborted"
	exit
fi

c="../data/corpus"
l="../data/doi.lst"
if [ -d $c ]; then
	echo "dir exists : $c"
	echo "the process aborted"
	exit
fi
if [ ! -f $l ]; then
	echo "doi file does not exist : $l"
	echo "the process aborted"
	exit
fi
mkdir $c
python src/science_direct_crowl.py --out_dir $c --lst $l --api_key "$k"

