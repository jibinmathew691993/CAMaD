#Relation extraction with weakly supervised learning based on process-structure-property-performance reciprocity

##Abstract
In this study, we develop a computer-aided material design system to represent and extract knowledge related to material design from natural language texts. A machine learning model is trained on a text corpus weakly labeled by minimal annotated relationship data (~100 labeled relationships) to extract knowledge from scientific articles. The knowledge is represented by relationships between scientific concepts, such as {annealing, grain size, strength}. 

This repository is the source code in our study. You can replicate the distant supervised relation extraction task with this source code. 

###Citation
Takeshi Onishi, Takuya Kadohira, Ikumu Watanabe. Relation extraction with weakly supervised learning based on process-structure-property-performance reciprocity. Science and Technology of Advanced Materials. 2018;19(1):649-659.

The article is [HERE](https://www.tandfonline.com/doi/full/10.1080/14686996.2018.1500852?scroll=top&needAccess=true).


##Instruction
###1st step: The corpus
Our corpus is scientific literature in [Science Direct](https://www.sciencedirect.com).  More details of the corpus are on Section 4.2 of the article. 

The following command gives the corpus. 
~~~~
$ cd python
$ ./download.sh  [YOUR API KEY]
~~~~
NOTE: the script requests [YOUR Elsevier API key](https://dev.elsevier.com).

###2nd step: Preprocessing
Sentences in the corpus are labeled with relationships in PSPP charts as Section 4.

The following command pre-processes the corpus and generates *./data/preprocessed*. 
~~~~
$ cd java
$ ./preprocess.sh [POS TAGGER]
~~~~
NOTE: the script requests a part-of-speech tagger of [Stanford coreNLP](https://stanfordnlp.github.io/CoreNLP). Please use *stanford-corenlp-3.8.0-models/edu/stanford/nlp/models/pos-tagger/english-left3wo
rds/english-left3words-distsim.tagger* in the package. 

###3rd step: Held-out data
We split the data into four pairs of training and test. The following command generates *./data/cross*.
~~~~
$ cd python
$ ./split\_preprocessed.sh
~~~~

###4th step: Training
The model requires pre-trained word embedding, [GloVe](https://nlp.stanford.edu/projects/glove). Please place the embeddings on *.data/glove.6B.50d.txt* before the training/testing. 

The following command trains four models for each pair of training respectively.
~~~~
$ cd python
$ ./train.sh
~~~~
NOTE: Python3.6 and TensorFlow 1.2-GPU are required. 

###5th step: Test
The following command shows the test results.
~~~~
$ cd python
$ ./test.sh
~~~~

###Drawing a chart
The following script gives a PSPP design chart for your desired properties. The script requires your lrs file, output file name and property names. The lrs file is a list of scored sentences. The script outputs a PSPP design chart at an HTML file specified by the output file name using the scores in the lrs file. 
~~~
$ cd java
$ mvn exec:java -Dexec.mainClass="us.ttic.takeshi.nims.relation_extraction.online_demo.N_necks" -Dexec.args="--lrs [your_lrs_file] --html [output_file_name] --target_prp \"[property_1]\" \"[property_2]\""
~~~	

Each line of a LRS file is scored sentence and target factors in the sentence. Here each factor is indicated by "@[factor category]" and "[factor category]@". 
~~~
0.8	PRC2STR	@PRC cast PRC@ iron has a @STR pearlite matrix STR@ .	Casting Perlite matrix	"Wikipedia/Pearlite"
0.9	STR2PRP	the relative proportions of @STR martensite STR@ , ferrite , and cementite determine the mechanical properties such as @PRP tensile strength PRP@ .	Martensite Tensile strength	"Wikipedia/Austenite"
~~~


##Dependency and Reference
 * Java (openjdk 10.0.1)
 * Maven (Apache Maven 3.5.2)
 * Python (Python 3.6.5 :: Anaconda, Inc)
 * TensorFlow (GPU version 1.2)
 * [Elsevier Developers](https://dev.elsevier.com) 
 * [Stanford coreNLP](https://stanfordnlp.github.io/CoreNLP) 
 * [GloVe](https://nlp.stanford.edu/projects/glove) 

##Lisence
This source code is licensed under the GNU General Public License. The license allows many free uses.
