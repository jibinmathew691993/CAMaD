#!/bin/bash
#======================================
#	test.sh
# this script test models
#======================================

#cross data
c="../data/cross"
if [ ! -d $c ]; then
	echo "no cross dir : "
	echo "the process aborted"
	exit
fi

#model dir
m="../data/model"
if [ ! -d $m ]; then
	echo "model dir does not exist"
	echo "the process aborted"
	exit
fi

#word vec
w="../data/glove.6B.50d.txt"
if [ ! -f $w ];then
	echo "word vector does not exist :"
	echo "please place the word vector on $w"
	echo "please visis https://nlp.stanford.edu/projects/glove for the word vector"
	echo "the process aborted"
	exit
fi

#vocb
v="../data/word.vocb"
if [ ! -f $v ];then
	echo "no vocb file : $v"
	echo "the process aborted"
	exit 
fi

#models
a=`ls ../data/model/cluster.resnet_default_0-*.index`
b=`ls ../data/model/cluster.resnet_default_1-*.index`
c=`ls ../data/model/cluster.resnet_default_2-*.index`
d=`ls ../data/model/cluster.resnet_default_3-*.index`

#test
python src/Cross_test_material.py --config  config/cluster/resnet_default.py --models ${a%.index} ${b%.index} ${c%.index} ${d%.index} --test_dirs ../data/cross/cross_0/test ../data/cross/cross_1/test ../data/cross/cross_2/test ../data/cross/cross_3/test
