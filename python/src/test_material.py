import numpy as np





def test_cluster(test_numeric, test_cluster, model, sess):
    relation_instances = [] #list of [ent_pair, pred_relation_type, score, gold_relation_type]

    for c in test_numeric:
      gold_id = test_cluster[c][0].relation.id
      #check
      for snt in test_cluster[c]:
        if snt.relation.id!=gold_id:
          print("invalid bag : ", gold_id, ", ", snt.relation.id)
          gold_id = -1
          break
      if gold_id<0: continue 
      
      test_numeric[c][model.dropout_keep_prob] = 1.0
      score = sess.run([model.get_score()], test_numeric[c])[0]
      
      likelihood = 1.0/(1.0+np.exp(-score))
      relation_instances+=[[c, 1, likelihood, gold_id]]
    
    print_dist(relation_instances)
    print_precision_and_recall(relation_instances)
    print_accuracies(relation_instances)
    
    return relation_instances



def print_precision_and_recall(relation_instances):
  relation_instances = sorted(relation_instances, key=lambda x: x[2])
  relation_instances.reverse()
  
  #total gold positive
  n_pos = 0
  n_neg = 0
  for ins in relation_instances:
    if ins[3]>0:
      n_pos+=1
    else:
      n_neg+=1
  print("gold positives : ", n_pos)
  print("gold negatives : ", n_neg)
  
  #precision and recall
  print("n\tprecison\trecall")
  c=0
  N_POINTS = 200
  for n,ins in enumerate(relation_instances):
    if ins[3]>0:
      c+=1
    if n%(max(len(relation_instances)/N_POINTS, 1))==0 or n==len(relation_instances)-1:
      print("%d\t%f\t%f"%(n+1, c/float(n+1) ,c/float(n_pos)))

def print_dist(relation_instances):
  MAX_BIN = 20
  neg_bin = [0]*MAX_BIN
  pos_bin = [0]*MAX_BIN
  for r in relation_instances:
    s = r[2]
    bin_idx = int(min(np.floor(s*MAX_BIN), MAX_BIN-1))
    if r[3]>0:
      pos_bin[bin_idx]+=1
    else:
      neg_bin[bin_idx]+=1
      
  neg_sum = sum(neg_bin)
  if neg_sum==0: neg_sum=-1
  pos_sum = sum(pos_bin)
  if pos_sum==0: pos_sum=-1
  
  print("pos/rate\tneg/rate")
  for i in range(MAX_BIN):
    print("%d/%f\t%d/%f"%(pos_bin[i],pos_bin[i]/float(pos_sum), neg_bin[i],neg_bin[i]/float(neg_sum)))
    

def print_accuracies(relation_isntances, th=0.5):
  '''
    relation_instances :
    th ;(0,1) : double
  '''
  pos_crr = 0
  pos_size= 0
  neg_crr = 0
  neg_size= 0
  for r in relation_isntances:
    if r[3]>0:
      pos_size+=1
      if r[2]>th: pos_crr+=1
    else:
      neg_size+=1
      if r[2]<th: neg_crr+=1
  
  print("accuracies: th=",th)
  print("pos_acc :",pos_crr/float(pos_size) if pos_size!=0 else -1)
  print("neg_acc :",neg_crr/float(neg_size) if neg_size!=0 else -1)
   



