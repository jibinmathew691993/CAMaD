'''
Created on Feb 15, 2018

@author: tonishi
'''
import tensorflow as tf
tf.set_random_seed(3)
import math
from data.Vocb import Word_Dict
from model.Model import *






class REcnn(object):
  '''
    CP graph takes
      - set of sentences where each length is L: Z^{batch_size x L}
      - 
  '''
  
  PAD_TAG = "<pad>"
  
  def __init__(self, config, word_vocb):
    '''
      config : config module
      
      build CP graph
    '''
    #network parameters
    self.L = config.snt_length
    self.POS_WINDOW= config.position_window
    self.POS_EMB = config.position_emb_dim
    self.WORD_EMB= config.word_emb_dim
    self.WORD_EMB_FILE=config.word_emb_init
    self.FILTER_SIZES = config.filter_sizes
    self.CONV_CHANNEL = config.conv_channel
    self.L2_LAMBDA = config.l2_lambda
    
    #Ling data
    self.word_vocb = word_vocb
    
    #Place holders
    #  sentence ; Z^{ |cluster_size| x L } 
    self.snts = tf.placeholder(tf.int32, [None, self.L], name="snts")
    self.pos1 = tf.placeholder(tf.int32, [None, self.L], name="pos1")
    self.pos2 = tf.placeholder(tf.int32, [None, self.L], name="pos2")
    self.label= tf.placeholder(tf.int32, [1], name="label")
    self.dropout_keep_prob = tf.placeholder(tf.float32, name="dropout_keep_prob")
    
    #  regulalizer
    l2_loss = tf.constant(0.0)
    
    
    #word embedding
    with tf.name_scope("word-embedding"):
      W = None
      if self.WORD_EMB_FILE is None:
        W = tf.Variable(tf.random_normal([self.word_vocb.get_size(),self.WORD_EMB] ,name='W'))
      else:
        self.word_vocb.add_word(REcnn.PAD_TAG)
        if self.word_vocb.get_idx(REcnn.PAD_TAG)==self.word_vocb.get_idx(Word_Dict.UNK): raise Exception("invalid word dict")
        W = word2vec(self.WORD_EMB_FILE, self.word_vocb)
        init = tf.constant_initializer(W)
        W = tf.get_variable(initializer=init, shape=W.shape, name='W')
    
      token_embs = tf.nn.embedding_lookup(W, self.snts)
    
    
    #position embedding
    with tf.name_scope("position-embedding"):
      W = tf.Variable(tf.random_uniform([self.POS_WINDOW+2, self.POS_EMB],
                                   minval=-math.sqrt(2/(self.POS_EMB+self.WORD_EMB)),
                                   maxval=+math.sqrt(2/(self.POS_EMB+self.WORD_EMB))),
                                   name="W")
      pos_emb1 = tf.nn.embedding_lookup(W, self.pos1)
      pos_emb2 = tf.nn.embedding_lookup(W, self.pos2)
      emb = tf.concat([token_embs, pos_emb1, pos_emb2],2)
      emb = tf.expand_dims(emb, -1) #R^{batch_size x L x emb x 1}
    
    
    # Create a convolution + maxpool layer for each filter size
    pooled_outputs = []
    for i, filter_size in enumerate(self.FILTER_SIZES):
      
      #build filter
      with tf.name_scope("conv-maxpool-%s" % filter_size):
        # Convolution Layer
        filter_shape = [filter_size, self.WORD_EMB+2*self.POS_EMB, 1, self.CONV_CHANNEL]
        W = tf.Variable(tf.truncated_normal(filter_shape, stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.1, shape=[self.CONV_CHANNEL]), name="b")
        conv = tf.nn.conv2d(emb,
                            W,
                            strides=[1,1,1,1],
                            padding="VALID",
                            name="conv")
        
        # Apply nonlinearity
        h = tf.nn.relu(tf.nn.bias_add(conv, b), name="relu")
        l2_loss += tf.nn.l2_loss(W)
        l2_loss += tf.nn.l2_loss(b)
        for i in range(4):
            h2, l2 = Cnnblock(self.CONV_CHANNEL, h, i)
            h = h2+h
            l2_loss += l2
        # Maxpooling over the outputs
        pooled = tf.nn.max_pool(
                                h,
                                ksize=[1, self.L - filter_size + 1, 1, 1],
                                strides=[1, 1, 1, 1],
                                padding='VALID',
                                name="pool")
        pooled_outputs.append(pooled)
  
    
    # Combine all the pooled features
    num_filters_total = self.CONV_CHANNEL * len(self.FILTER_SIZES)
    h_pool = tf.concat(pooled_outputs, 3)
    h_pool_flat = tf.reshape(h_pool, [-1, num_filters_total], name="hidden_feature")

    # Add dropout
    with tf.name_scope("dropout"):
      h_drop = tf.nn.dropout(h_pool_flat, self.dropout_keep_prob)
    
    
    
    with tf.name_scope("MLP"):
      W0 = tf.Variable(tf.truncated_normal([num_filters_total, num_filters_total], stddev=0.1), name="W0")
      b0 = tf.Variable(tf.constant(0.1, shape=[num_filters_total]), name="b0")
      h0 = tf.nn.relu(tf.nn.xw_plus_b(h_drop, W0, b0))
      l2_loss += tf.nn.l2_loss(W0)
      l2_loss += tf.nn.l2_loss(b0)
      W1 = tf.Variable(tf.truncated_normal([num_filters_total, num_filters_total], stddev=0.1), name="W1")
      b1 = tf.Variable(tf.constant(0.1, shape=[num_filters_total]), name="b1")
      h1 = tf.nn.relu(tf.nn.xw_plus_b(h0, W1, b1)) # R^{batch x channel}
      l2_loss += tf.nn.l2_loss(W1)
      l2_loss += tf.nn.l2_loss(b1)
    
    
    # Final (unnormalized) scores and predictions
    with tf.name_scope("output"):
        W = tf.get_variable(
            "relation_v",
            shape=[num_filters_total, 1],
            initializer=tf.contrib.layers.xavier_initializer())
        l2_loss += tf.nn.l2_loss(W)
        self.scores = tf.matmul(h1, W, name="scores")
        self.scores = tf.squeeze(self.scores, axis=1)#R^{batch}

    # CalculateMean cross-entropy loss
    with tf.name_scope("loss"):
        losses = tf.nn.sigmoid_cross_entropy_with_logits(logits=self.scores
                                                         ,labels=tf.ones_like(self.scores)*tf.cast(self.label, dtype=tf.float32))
        self.loss = tf.reduce_mean(losses) + self.L2_LAMBDA * l2_loss

    self.debug = [self.label]

  
  def get_loss(self):
    '''
      return : R
    '''
    return self.loss
  
  def get_score(self):
    return tf.squeeze( tf.reduce_max( self.scores ) )
  
  
  def enumerate(self, cluster):
    '''
      cluster : list<Sentence>
      
      return ; input for the CP graph : map<Place_holder, np.array>
    '''
    snts = []
    pos1 = []
    pos2 = []
    label= None
    for s in cluster:
      if label is None:
        label = s.relation.id
      else:
        if label!=s.relation.id: raise Exception("invalid cluster")
      
      fixed_snt = fix_length(s.words, L=self.L, pad=REcnn.PAD_TAG)
      p1, p2    = positions(s, MAX_WINDOW=self.POS_WINDOW)
      p1 = fix_length(p1, L=self.L, pad=0)
      p2 = fix_length(p2, L=self.L, pad=0)
      
      snts += [ [self.word_vocb.get_idx(w) for w in fixed_snt] ]
      pos1 += [ p1 ]
      pos2 += [ p2 ]

    return { self.snts : np.array(snts)
            ,self.pos1 : np.array(pos1)
            ,self.pos2 : np.array(pos2)
            ,self.label: np.array([label])}


   
  
