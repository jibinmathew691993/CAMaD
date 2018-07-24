'''
Created on Feb 13, 2018

@author: tonishi
'''
import sys

class Dynamic_Word_Dict(object):
  
  UNK = "<unk>"
  
  def __init__(self):
    '''
      Dictionary word -> int
      (words appended more than max_size will be assign  
    
      FIELD:
        self.indecs : map<str, int>
        self.ids : list<str>
        self.size : int 
    '''
    self.indeces = {}
    self.ids=[]
    self.lock = False
    self.get_idx(Dynamic_Word_Dict.UNK)
    
  def is_locked(self):
    return self.lock

  def lock_dict(self):
    self.lock = True
  
  def get_idx(self, idneity):
    std = Dynamic_Word_Dict.std_form(idneity)
    if not std in self.indeces:
      if self.is_locked():
        std = Dynamic_Word_Dict.UNK
      else:
        idx = len(self.ids)
        self.ids +=[std]
        self.indeces[std]=idx
    
    return self.indeces[std]

  @staticmethod
  def std_form(word):
    return word.strip().lower()
  
  def get_id(self, index):
    '''
    index : int
    '''
    return self.ids[index]


  def get_size(self):
    return len(self.ids)



class Freq_Dictionary(object):
  '''
  this is a dictionary
    id -> idx
     and
    idx -> freq
  '''
  
  def __init__(self, freq):
    '''
      freq ; id -> freq : map<str, int>
      
      FIELD:
        self.indeces ; id -> idx : map<str, int>
        self.ids ; idx -> id  : list<int>
        self.freq ; idx-> freq : list<int>
        self.total_freq ; sum(self.freq) : int 
    '''
    self.ids = [k for k in freq.keys()]
    self.indeces = {k:i for i,k in enumerate(self.ids)}
    self.freq = [freq[k] for k in self.ids]
    self.total_freq = sum(self.freq)
  
  def set_identity_freq(self, identity, freq):
    '''
    identity : str
    freq : int
    '''
    idx = len(self.ids)
    self.ids+=[identity]
    self.indeces[identity]=idx
    self.freq+=[freq]
    self.total_freq = sum(self.freq)
  
  @staticmethod
  def load_from_file(lst_file, Dict_Type):
    '''
      file : str
      Dic_Type : Type of dict : <? extends Freq_Dictionary> 
      
      RETURN : Dict_Type
    '''
    print( "Dict file is loaded form : ", lst_file )
    with open(lst_file, 'r') as f:
      lines = f.readlines()
    
    d = {}
    for l in lines:
      e = l.split("\t")
      d[e[0].strip()]=int(e[1])
      
    return Dict_Type(d)
  
    
  def save(self, lst_file):
    '''
      file : str
    '''
    freq = {self.ids[i]:self.freq[i] for i in range(len(self.ids))}
    ranked = sorted(freq.keys(), key=lambda x: freq[x])
    ranked = reversed(ranked)
    
    with open(lst_file, 'w') as f:
      for identity in ranked:
        f.write(identity)
        f.write("\t")
        f.write(str(freq[identity]))
        f.write("\n")
    
  def get_idx(self, idneity):
    if not idneity in self.indeces: return -1
    return self.indeces[idneity]

  def is_known_id(self, identity):
    return identity in self.indeces

  def get_id(self, idx):
    if not idx<len(self.ids): return None
    return self.ids[idx]
  
  def get_idx_freq(self, idx):
    if not idx < len(self.freq): return -1
    return self.freq[idx]
  
  def get_total_freq(self):
    return self.total_freq

  def get_size(self):
    return len(self.ids)

  def get_ids(self):
    return self.ids
    
    
    
    

class Word_Dict(Freq_Dictionary):
  
  UNK = "<unk>"
  
  def __init__(self, freq, freq_of_unk = 0):
    super(Word_Dict, self).__init__(freq)
    
    self.indeces[Word_Dict.UNK] = len(self.ids)
    self.freq+= [freq_of_unk] 
    self.ids += [Word_Dict.UNK]
    self.total_freq+=freq_of_unk
    
  def add_word(self, word):
    std = Word_Dict.std_form(word)
    if self.is_known_id(std): return
    
    self.indeces[std] = len(self.ids)
    self.freq+=[0]
    self.ids +=[std]
  
  #OVERRIDE  
  def set_identity_freq(self, identity, freq):
    '''
    identity : str
    freq : int
    '''
    k = Word_Dict.std_form(identity)
    idx = len(self.ids)
    self.ids+=[k]
    self.indeces[k]=idx
    self.freq+=[freq]
    self.total_freq = sum(self.freq)
  
  #OVERRIDE
  def get_idx(self, idenity):
    std = Word_Dict.std_form(idenity)
    if not std in self.indeces: return self.indeces[Word_Dict.UNK]
    return self.indeces[std]
  
  #OVERRIDE
  def is_known_id(self, idenity):
    std = Word_Dict.std_form(idenity)
    return std in self.indeces
  
  def is_known_idx(self, index):
    return (index<self.get_size()) and (index != self.get_idx(Word_Dict.UNK))
  
  @staticmethod
  def std_form(word):
    '''
      word : str
      RETURN : str
    '''
    return word.strip().lower()



  