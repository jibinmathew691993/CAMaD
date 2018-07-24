'''
Created on Feb 15, 2018

@author: takeshi.onishi
'''
import tensorflow as tf

#corpus
train_dir = None
test_dir = None

#language data
word_emb_init = "../data/glove.6B.50d.txt"
word_vocb = "../data/word.vocb"


#model
from model.RECnn import REcnn as model
# model parameters
snt_length = 100     
position_window = 30
position_emb_dim = 5
word_emb_dim = 50
filter_sizes = [2]
conv_channel = 50
l2_lambda = 0.0001


# Misc Parameters
allow_soft_placement = True
log_device_placement = False


#Train alg
optimizer = tf.train.AdamOptimizer(learning_rate=0.00005)
# Training parameters
dropout_keep_prob = 0.8
num_epochs = 6000


#Log
evaluate_every = 1000
checkpoint_every = 10000

#store model
save_dir = "../data/model" #"dir to save log"
