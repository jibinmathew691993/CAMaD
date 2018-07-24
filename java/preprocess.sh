#!/bin/bash
#=======================================
#	preprocess.sh
#	this preprocess corpus
#	1. tokenizing
#	2. targeting factors
#	3. finding sentences with factors
#=======================================

#postager
t=$1
if [ -z $t ]; then
	echo "pos tagger is required"
	echo "preferred : stanford-corenlp-3.8.0-models/edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger"
	echo "please visit https://stanfordnlp.github.io/CoreNLP for the detail" 
	echo "the process aborted"
	exit
fi

#corpus
c="../data/corpus"
if [ ! -d $c ]; then
	echo "no corpus : $c"
	echo "the process aborted"
	exit
fi

#charts and factors
a="../data/annotation"
if [ ! -d $a ]; then
	echo "no annotation data : $a"
	echo "the process aborted"
	exit
fi


#output
d="../data/preprocessed"
if [ -d $d ]; then
	echo "dir already exist : $d"
	echo "the process aborted"
	exit
fi
mkdir $d


export MAVEN_OPTS=-Xmx8g
mvn compile

#first chart
mkdir "../data/preprocessed/chart_01"
mvn exec:java -Dexec.mainClass="Targeted_Clustered" -Dexec.args="-sc_dir ../data/corpus -prc ../data/annotation/processing.dct -str ../data/annotation/structure.dct -prp ../data/annotation/property.dct -relation ../data/annotation/relation.csv -pos_tagger $t -source \"Ikumu : p7\" -out ../data/preprocessed/chart_01"

#second chart
mkdir "../data/preprocessed/chart_02"
mvn exec:java -Dexec.mainClass="Targeted_Clustered" -Dexec.args="-sc_dir ../data/corpus -prc ../data/annotation/processing.dct -str ../data/annotation/structure.dct -prp ../data/annotation/property.dct -relation ../data/annotation/relation.csv -pos_tagger $t -source \"Ikumu : p9\" -out ../data/preprocessed/chart_02"

#third chart
mkdir "../data/preprocessed/chart_03"
mvn exec:java -Dexec.mainClass="Targeted_Clustered" -Dexec.args="-sc_dir ../data/corpus -prc ../data/annotation/processing.dct -str ../data/annotation/structure.dct -prp ../data/annotation/property.dct -relation ../data/annotation/relation.csv -pos_tagger $t -source \"Cybermaterials: materials by design and accelerated insertion of materials : p8\" -out ../data/preprocessed/chart_03"

#fourth 
mkdir "../data/preprocessed/chart_04"
mvn exec:java -Dexec.mainClass="Targeted_Clustered" -Dexec.args="-sc_dir ../data/corpus -prc ../data/annotation/processing.dct -str ../data/annotation/structure.dct -prp ../data/annotation/property.dct -relation ../data/annotation/relation.csv -pos_tagger $t -source \"Cybermaterials: materials by design and accelerated insertion of materials : p11\" -out ../data/preprocessed/chart_04"
