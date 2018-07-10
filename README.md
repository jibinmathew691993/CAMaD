# Relation extraction with weakly supervised learning based on process-structure-property-performance reciprocit

# Abstruct
In this study, we develop a computer-aided material design system to represent
and extract knowledge related to material design from natural language texts. A machine learning model is trained on a text corpus weakly labeled by minimal annotated relationship data (~100 labeled relationships) to extract knowledge from scientific articles. The knowledge is represented by relationships between scientific concepts, such as {annealing, grain size, strength}. 
The extracted relationships are represented as a knowledge graph formatted according to design charts, inspired by the process-structure-property-performance reciprocity. The design chart provides an intuitive effect of processes on properties and prospective processes to achieve the certain desired properties. 
Our system semantically searches the scientific literature and provides knowledge in the form of a design chart, and we hope it contributes more efficient developments of new materials. 

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



