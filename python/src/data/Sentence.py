import glob
import os

class Sentence:
  '''
  this is a sentence with entities and relation type
  '''
  SUFFIX = ".lst"
  TAIL_TAG = "###END###"
  
  POS_RLT = "POS"
  NA_RLT = "NA"
  
  PAIR_TYPES = {"STR2PRP":["STR", "PRP"], "PRC2STR":["PRC", "STR"] }
  
  def __init__(self, line, relation_dict):
    '''
    line : str
      FROMAT:
        [double]\t[str]\t[str] [str] ... [str]\t[
        e.g.
        0.0     STR2PRP it is proposed that the grain size , shape and distribution of Y phase , the interface @PRP strength PRP@ between martensite phase and Y phase , as well as the own ductility of @STR Y phase STR@ are responsible for the whole ductility of the counterpart two-phase alloys .  Y phase Strength        PRP VBZ VBN IN DT NN NN , NN CC NN IN NN NN , DT NN NN IN JJ NN CC NN NN , RB RB IN DT JJ NN IN NN NN VBP JJ IN DT JJ NN IN DT NN JJ NNS .
    
    FILED:
      self.entity1 : str
      self.entity2 : str
      self.relation: Relation
      self.words : list<str>
      
      self.relation_score = double
      self.pair_type : str
      self.org_label : str
      self.dst_label : str
      self.art_id : str
    '''
    e = line.split("\t")
    
    self.relation_score = float(e[0].strip())
    self.relation = relation_dict[ Sentence.POS_RLT if self.relation_score>0.5 else Sentence.NA_RLT ]
    self.pair_type = e[1].strip()
    self.org_label = e[3].strip()
    self.dst_label = e[4].strip()
    
    if len(e)>5: self.art_id = e[5].strip()
    
    #check
    if self.pair_type not in Sentence.PAIR_TYPES:
      print( "invalid pair type : ", self.pair_type)
    
    self.tagged = e[2].strip().split(" ")
    self.words = []
    self.entity1 = None
    self.entity2 = None
    self.set_snt(self.tagged)
    

  def set_snt(self, tagged_snt):
    '''
    tagged_snt ; sentence with tags, @XXX, XXX@  and @YYY, YYY@. : list<str>
    return ; sequence of words : list<str>
    '''
    self.words = []
    self.entity1 = None
    self.entity2 = None
    
    entities = {}
    i = 0
    while i<len(tagged_snt):
      w = tagged_snt[i]
      
      if w.startswith("@") and (w.endswith(Sentence.PAIR_TYPES[self.pair_type][0]) or w.endswith(Sentence.PAIR_TYPES[self.pair_type][1])):
        tag = w[1:]
        ph = []
        i+=1
        while tagged_snt[i]!=tag+"@":
          ph += [tagged_snt[i].lower()]
          i+=1
        
        tagged = ph[0]
        for j in range(1, len(ph)): tagged +="-"+ph[j]
        entities[tag]=tagged
        
        self.words += [tagged]
      else:
        self.words += [w.lower()]
      i+=1
    
    #check
    #if len(self.words)!=len(tagged_snt)-4: print "invalid format : ", tagged_snt
    
    #entity
    if len(entities)==2:
      self.entity1 = entities[Sentence.PAIR_TYPES[self.pair_type][0]]
      self.entity2 = entities[Sentence.PAIR_TYPES[self.pair_type][1]]
    else:
      self.entity1=None
      self.entity2=None
    
  def to_line(self):
    r = str(self.relation_score)+"\t"
    r+= self.pair_type+"\t"
    for i, w in enumerate(self.tagged):
      r+=w+ " " if i<len(self.tagged)-1 else "\t"
    r+=self.org_label+"\t"
    r+=self.dst_label
    return r
    
  def __str__(self):
    r = "%s\t%s\t%s\t%s\t%s\t"%(self.entity1, self.entity2
                                , self.entity1, self.entity2
                                , self.relation)
    for w in self.words:
      r+=w+" "
    r+=Sentence.TAIL_TAG
    
    return r

  @staticmethod
  def load_clusters(dirname, relations):
    '''
      dir_path : str
      relations : map<str, list<Relation>>
      
      return : map<str, list<Sentence>>
    '''
    bags = {}
      
    for filename in glob.glob(dirname+"/*"+Sentence.SUFFIX):
      with open(filename, 'r') as f:
        lines = f.readlines()
        
      for line in lines:
        s = Sentence(line, relations)
        if (s.entity1 is None) or (s.entity2 is None):
          #print "Entities are overlapped : ", s.__str__()
          pass
        else:
          if os.path.basename(filename) not in bags:
            bags[os.path.basename(filename)] = []
          bags[os.path.basename(filename)] += [s]

    return bags

  
  
  