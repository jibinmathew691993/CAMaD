# Relation extraction with weakly supervised learning based on process-structure-property-performance reciprocit

# Paper Replication
Instruction to replicate our results.

1.  Find your API key to download the corpus.
2.  Find Pos-tagger
3.  Find and put Glove embeddings into `./data/glove.6B.50d.txt`. 
4.  Run the following script.

```
$ ./go.sh [your_api_key] [pos_tagger]
```
e.g.) `$ ./go.sh "ffwefasdifsojiaf" ~/stanford-corenlp-3.8.0-models/edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger`

NOTE) the code will take a few hours. 


# Files
* data/annotation/...dic
NOTE) here they are factors collected by dictionary mannor since the code replicates the relation extraction subtask. 

# Major requrements
* Java (openjdk 10.0.1)
* Maven (Apache Maven 3.5.2)
* Python (Python 3.6.5 :: Anaconda, Inc)
* TensorFlow (GPU version 1.7)


# Related link
Corpus ( [Elsevier Developers](https://dev.elsevier.com) )

Pos tagger ( [Stanford coreNLP](https://stanfordnlp.github.io/CoreNLP) )

Pre-train Word embedding ( [GloVe](https://nlp.stanford.edu/projects/glove) ) 

# Citation



