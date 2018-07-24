
import tensorflow as tf
import numpy as np
np.random.seed(3)
import os
import sys
from importlib import import_module
from data.Vocb import Freq_Dictionary
from data.Vocb import Word_Dict
from data.Sentence import Sentence
from data.Relation import Relation
import test_material



NAME_SPLITTER="-"
def save_model(saver, sess, base_name, num):
  '''
  saver : Tensorflow.train.Saver()
  sess : Tensorflow.Session()
  basae_name : str
  num : int
  '''
  #timeStamp = datetime.datetime.fromtimestamp(time.time()).strftime('%m_%d_%H')
  name = base_name+NAME_SPLITTER+"%012d"%num
  saver.save(sess, save_path=name)
  print( "model_saved : ", name )
  

CONFIG = "--config"
TRAIN_DIR = "--train_dir"
TEST_DIR = "--test_dir"
SUFFIX = "--suffix"
if __name__ == '__main__':
  
  #arguments
  config_file = None
  train_dir = None
  test_dir = None
  suffix = None
  i = 0
  while i<len(sys.argv):
    if sys.argv[i]==CONFIG:
      i+=1
      config_file = sys.argv[i]
    elif sys.argv[i]==TRAIN_DIR:
      i+=1
      train_dir = sys.argv[i]
    elif sys.argv[i]==TEST_DIR:
      i+=1
      test_dir = sys.argv[i]
    elif sys.argv[i]==SUFFIX:
      i+=1
      suffix = sys.argv[i]
    i+=1
  
  #load config file
  if config_file.endswith(".py"): config_file = config_file[:-3]
  config_file = config_file.replace("/", ".")
  config =  import_module(config_file)
  base_name = config_file.replace("config.", "")
  
  #suffix
  if not suffix is None: base_name+="_"+suffix
  
  #over write
  if not train_dir is None:
    config.train_dir = train_dir
    print("train_dir over write : ", config.train_dir)
  if not test_dir is None:
    config.test_dir = test_dir
    print("test_dir over write : ", config.test_dir)
  
  
  
  #======================
  #  Data Preparation
  #======================
  relations = Relation.load_binary()
  train_clusters = Sentence.load_clusters(config.train_dir, relations)
  test_clusters  = Sentence.load_clusters(config.test_dir, relations)
  
  #train_seq = train_clusters.keys()*config.num_epochs
  train_seq = [ t for t in train_clusters.keys() for i in range(config.num_epochs) ]
  train_seq = np.random.permutation(train_seq)
  
  #=======================
  #  Ling data
  #=======================
  vocb = Freq_Dictionary.load_from_file(config.word_vocb, Word_Dict)
  
  
  #======================
  #  Load model which is saved 
  #======================
  #check saving dir
  if not os.path.isdir(config.save_dir):
    print("dir to save is not existing : ", config.save_dir)
    #exit() 
  


  #=====================
  #  Start Training
  #=====================
  with tf.Graph().as_default():
    session_conf = tf.ConfigProto(
      allow_soft_placement=config.allow_soft_placement,
      log_device_placement=config.log_device_placement)
    sess = tf.Session(config=session_conf)
    with sess.as_default():
      tf.set_random_seed(3)
      # model
      model = config.model(config, vocb)

      # Define Training procedure
      global_step = tf.Variable(0, name="global_step", trainable=False)
      optimizer = config.optimizer
      grads_and_vars = optimizer.compute_gradients(model.get_loss())
      train_op = optimizer.apply_gradients(grads_and_vars, global_step=global_step)
      sess.run(tf.global_variables_initializer())
      
      # load parameters
      print( "parameter initialized" ) 

      saver =tf.train.Saver(tf.global_variables(), max_to_keep=1000)
      
      #  data -> numeric
      train_numeric = {}
      for c in train_clusters:
        train_numeric[c] = model.enumerate( train_clusters[c] )
      test_numeric = {}
      for c in test_clusters:
        test_numeric[c] = model.enumerate( test_clusters[c] )
      
      
      
      n_update =0
      total_loss = 0.0
      loss_normal = 0
      min_loss = 9999999999.9
      # Training loop
      for c in train_seq:
        #each epoch
        if n_update%( 50*len(train_numeric) )==0 or n_update==len(train_seq):
          total_loss = total_loss/max( loss_normal, 1 )
          print( "epoch : ",n_update/len(train_numeric) )
          print( "train_loss: ",total_loss)
          
          if total_loss<min_loss and total_loss>0:
            min_loss = total_loss
            save_model(saver, sess, config.save_dir+"/"+base_name, num=999)
          
          total_loss=0.0
          loss_normal=0
        sys.stdout.flush()
        
        #update
        train_numeric[c][model.dropout_keep_prob] = config.dropout_keep_prob
        _, loss = sess.run([train_op, model.get_loss()], train_numeric[c])
        total_loss += loss
        loss_normal+= 1
        
        n_update+=1
      
              
      test_material.test_cluster(test_numeric, test_clusters, model, sess)
      
