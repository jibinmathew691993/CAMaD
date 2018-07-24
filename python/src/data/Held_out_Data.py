'''
Created on Feb 11, 2018

This is script to split data
-> cross validations of training and Test

@author: tonishi
'''
import sys
import glob
import os.path
import shutil

CLOSS_SET_TEMP = "cross_%d"
TEST_SET = "test"
TRAIN_SET = "train"

CLUSTER_DIRS = "--dirs"
OUT_DIR = "--out"
TARGET_SUFFIX = "--suffix"
if __name__=="__main__":
  
  cluster_dirs=None
  out_dir = None
  target_suffix = ".lst"
  i=0
  while i<len(sys.argv):
    if CLUSTER_DIRS==sys.argv[i]:
      i+=1
      cluster_dirs = sys.argv[i].split(",")
    elif OUT_DIR==sys.argv[i]:
      i+=1
      out_dir = sys.argv[i]
    elif TARGET_SUFFIX==sys.argv[i]:
      i+=1
      target_suffix=sys.argv[i]
    i+=1
  
  if os.path.isdir(out_dir) or os.path.isfile(out_dir):
    print("dir already exists : ", out_dir)
    exit()
  else:
    os.mkdir(out_dir)
    for i in range(len(cluster_dirs)):
      if not os.path.isdir(cluster_dirs[i]):
        print("not dir : ", cluster_dirs[i])
        exit()
      os.mkdir(out_dir+"/"+CLOSS_SET_TEMP%i)
      os.mkdir(out_dir+"/"+CLOSS_SET_TEMP%i+"/"+TEST_SET)
      os.mkdir(out_dir+"/"+CLOSS_SET_TEMP%i+"/"+TRAIN_SET)
  
  #load files
  for test_idx in range(len(cluster_dirs)):
    print( "cross_set : ", test_idx ) 
    
    test_dir = cluster_dirs[test_idx]  
    test_files = glob.glob(test_dir+"/*"+target_suffix)
    tests = [os.path.basename(f) for f in test_files]
    print( "n_test : ", len(test_files))
    
    train_files = []
    for train_idx in range(len(cluster_dirs)):
      if test_idx==train_idx: continue
      
      for train_file in glob.glob(cluster_dirs[train_idx]+"/*"+target_suffix):
        if os.path.basename(train_file) in tests:
          print( "cluster removed from training: ", train_file ) 
        else:
          train_files+=[train_file]
    print( "n_train : ", len(train_files) ) 
    
    #test
    for f in test_files:
      shutil.copy(f, out_dir+"/"+CLOSS_SET_TEMP%test_idx+"/"+TEST_SET)
    #train
    for f in train_files:
      shutil.copy(f, out_dir+"/"+CLOSS_SET_TEMP%test_idx+"/"+TRAIN_SET)
  
  print( "DONE" ) 
  
    

    