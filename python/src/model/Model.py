'''
Created on Feb 15, 2018

@author: takeshi.onishi
'''
import tensorflow as tf
tf.set_random_seed(3)
import numpy as np




def positions( d, MAX_WINDOW=30):
  '''
    d : Sentence
    
    return ; (p1,p2) positions idx for each entity1, entity2 
            : (list<int>, list<int>)
  '''
  p1 = []
  p2 = []
  e1 = d.entity1
  e2 = d.entity2
  words = d.words
  l1 = 0
  l2 = 0
  for i, w in enumerate(words):
      if w == e1:
          l1 = i
      if w == e2:
          l2 = i
  for i, w in enumerate(words):
      a = i-l1
      b = i-l2
      if a > MAX_WINDOW:
          a = MAX_WINDOW
      if b > MAX_WINDOW:
          b = MAX_WINDOW
      if a < -MAX_WINDOW:
          a = -MAX_WINDOW
      if b < -MAX_WINDOW:
          b = -MAX_WINDOW
      p1.append(a+MAX_WINDOW+1)
      p2.append(b+MAX_WINDOW+1)
      
  return p1, p2

def fix_length(seq, L, pad=0):
  '''
    seq : list<?>
    L : int
    
    return ; seq which is padded on both side : list<int>
  '''
  a = L-len(seq)
  if a > 0:
      front = int( a/2 )
      back = a-front
      front_vec = [pad for _ in range(front)]
      back_vec = [pad for _ in range(back)]
      padded = front_vec + seq + back_vec
  else:
      padded = seq[:L]
      
  return padded


def word2vec(txt_file, vocb):
  '''
    txt_file : str
      FORMAT:
        str double double ...
        [word] [vec]
    vocb : Vocb.Word_dict
    
    return : R^{V x emb}
  '''
  #load word2vec from file
  #Two data structure: word2index, index2vector
  lines = list(open(txt_file, "r").readlines())
  vecs = [s.split() for s in lines]
  
  #check dim
  vec_dim = len(vecs[0])-1
  print("word2vec (dim=", vec_dim, "): ", txt_file)
  
  #zero init
  W = np.zeros((vocb.get_size(), vec_dim))
  
  for v in vecs:
    vec = [ float(v[i+1]) for i in range(vec_dim) ]
    widx = vocb.get_idx(v[0])
    W[widx] = vec
  
  return W


def Cnnblock(num_filters, h, i):
  l2_loss = tf.constant(0.0)
  
  #W1 = tf.Variable(tf.truncated_normal([3, 1, num_filters, num_filters], stddev=0.1), name="W1")
  W1 = tf.get_variable(
      "W1_"+str(i),
      shape=[3, 1, num_filters, num_filters],
      initializer=tf.contrib.layers.xavier_initializer_conv2d())
  b1 = tf.Variable(tf.constant(0.1, shape=[num_filters]), name="b1_"+str(i))
  conv1 = tf.nn.conv2d(
      h,
      W1,
      strides=[1,1,1,1],
      padding="SAME")
  h1 = tf.nn.relu(tf.nn.bias_add(conv1, b1), name="relu1")
  l2_loss += tf.nn.l2_loss(W1)
  l2_loss += tf.nn.l2_loss(b1)
  #W2 = tf.Variable(tf.truncated_normal([3, 1, num_filters, num_filters], stddev=0.1), name="W2")
  W2 = tf.get_variable(
      "W2_"+str(i),
      shape=[3, 1, num_filters, num_filters],
      initializer=tf.contrib.layers.xavier_initializer_conv2d())
  b2 = tf.Variable(tf.constant(0.1, shape=[num_filters]), name="b2_"+str(i))
  conv2 = tf.nn.conv2d(
      h1,
      W2,
      strides=[1,1,1,1],
      padding="SAME")
  h2 = tf.nn.relu(tf.nn.bias_add(conv2, b2), name="relu2")
  l2_loss += tf.nn.l2_loss(W2)
  l2_loss += tf.nn.l2_loss(b2)
  return h2, l2_loss


