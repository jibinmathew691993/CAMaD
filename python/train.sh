#!/bin/bash
#======================================
#	train.sh
# this script train a model with default setting
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
if [ -d $m ]; then
	echo "model dir already exists"
	echo "the process aborted"
	exit
fi
mkdir $m

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


python src/Train_cluster.py --config config/cluster/resnet_default.py --train_dir ../data/cross/cross_0/train --test_dir ../data/cross/cross_0/test --suffix 0 
python src/Train_cluster.py --config config/cluster/resnet_default.py --train_dir ../data/cross/cross_1/train --test_dir ../data/cross/cross_1/test --suffix 1 
python src/Train_cluster.py --config config/cluster/resnet_default.py --train_dir ../data/cross/cross_2/train --test_dir ../data/cross/cross_2/test --suffix 2 
python src/Train_cluster.py --config config/cluster/resnet_default.py --train_dir ../data/cross/cross_3/train --test_dir ../data/cross/cross_3/test --suffix 3

