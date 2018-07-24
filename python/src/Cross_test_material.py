'''
Created on Feb 14, 2018

@author: tonishi
'''
import tensorflow as tf
import sys
from importlib import import_module
import test_material
from data.Relation import Relation
from data.Sentence import Sentence
from data.Vocb import Word_Dict
from data.Vocb import Freq_Dictionary



def unit_test_cluster(config_file, model_file, test_dir):
  relation_instances = []
  
  #load config file
  if config_file.endswith(".py"): config_file = config_file[:-3]
  config_file = config_file.replace("/", ".")
  config =  import_module(config_file)
  
  #======================
  #  Data Preparation
  #======================
  relations = Relation.load_binary()
  test_clusters  = Sentence.load_clusters(test_dir, relations)

  
  #=======================
  #  Ling data
  #=======================
  vocb = Freq_Dictionary.load_from_file(config.word_vocb, Word_Dict)
  
  
  #=======================
  #  load sess
  #=======================
  with tf.Graph().as_default():
    session_conf = tf.ConfigProto(
      allow_soft_placement=config.allow_soft_placement,
      log_device_placement=config.log_device_placement)
    sess = tf.Session(config=session_conf)
    with sess.as_default():
      # model
      model = config.model(config, vocb)
      sess.run(tf.global_variables_initializer())
      loader = tf.train.Saver()
      loader.restore(sess, model_file)
      
      
      test_numeric = {}
      for c in test_clusters:
        test_numeric[c] = model.enumerate( test_clusters[c] )
      relation_instances=test_material.test_cluster(test_numeric, test_clusters, model, sess)
    
  return relation_instances
      


'''
  for each option, you can put list like
    --configs xxx.config yyy.config ...
'''
CONFIG  = "--config"
MODELS = "--models"
TEST_DIRS = "--test_dirs"

if __name__ == '__main__':
  
  #arguments
  config_files = []
  config_file = None
  model_files = []
  test_dirs = []
  i = 0
  while i<len(sys.argv):
    if sys.argv[i]==CONFIG:
      i+=1
      config_file=sys.argv[i]        
    elif sys.argv[i]==MODELS:
      while i+1<len(sys.argv) and not sys.argv[i+1].startswith("--"):
        i+=1
        model_files += [ sys.argv[i] ]
    elif i+1<len(sys.argv) and sys.argv[i]==TEST_DIRS:
      while i+1<len(sys.argv) and not sys.argv[i+1].startswith("--"):
        i+=1
        test_dirs += [ sys.argv[i] ]
    i+=1
  
  if not config_file is None:
    config_files = [config_file]*len(model_files)
  
  n_cross = len(config_files)
  if len(model_files)!=n_cross:
    print("invalid model files : ", model_files)
    exit()
  if len(test_dirs)>0 and len(test_dirs)!=n_cross:
    print("invalid test dirs : ", test_dirs)
    exit()
  print("n_cross : ", n_cross)
  
  relation_instances = []
  for n in range(n_cross):
    print( "config :", config_files[n] )
    print( "model :", model_files[n] )
    if len(test_dirs)>0:print( "test data :", test_dirs[n] ) 
    print( "---" )
    relation_instances += unit_test_cluster(config_files[n], model_files[n], test_dirs[n])
  
  print("cross_test :")
  test_material.print_dist(relation_instances)
  test_material.print_precision_and_recall(relation_instances)
  test_material.print_accuracies(relation_instances)
  
