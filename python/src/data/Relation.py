import numpy as np
from data.Sentence import Sentence

class Relation:
    def __init__(self, name, id_):
        self.id = id_
        self.name = name
        self.number = 0

    def generate_vector(self, relationTotal):
        v = np.zeros(relationTotal)
        v[self.id] = 1
        self.vector = v

    def add_one(self):
        self.number += 1


    @staticmethod
    def load_binary():
      relations = {}
      r = Relation(Sentence.NA_RLT, 0)
      relations[Sentence.NA_RLT] = r
        
      r = Relation(Sentence.POS_RLT, 1)
      relations[Sentence.POS_RLT] = r
      
      return relations