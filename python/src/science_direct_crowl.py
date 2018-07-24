#====================================
# script to download science direct
#====================================
#API access KEY
API_KEY_OBTAIN = {'Accept': 'text/xml', 'X-ELS-APIKey':None}
SEARCH_URL = 'http://api.elsevier.com/content/search/scidir'
RETRIEVE_URL = 'http://api.elsevier.com/content/article/doi/%s'

import requests
import sys
import os


def download(doi):
    '''
    download by doi
    
    doi : str
    return : html format or None if it's can not be retrieved;str/None
    '''
    try:
        r = requests.get(RETRIEVE_URL%doi, headers = API_KEY_OBTAIN)
        r.raise_for_status()
    except :
        sys.stderr.write( "non-public article("+str(r.status_code)+") : "+doi)
        return None
    return r.text

    

def make_dir(dir_path):
  if os.path.exists(dir_path): return
  
  if os.path.exists(os.path.dirname(dir_path)):
    os.mkdir(dir_path)
  else:
    make_dir(os.path.dirname(dir_path))
  

OUT_DIR ="--out_dir"
DOI_LST = "--lst"
API_KEY = "--api_key"
if __name__ == "__main__":
  
  out_dir=None
  lst_file=None
  api_key = None
  i=0
  while i<len(sys.argv):
    if sys.argv[i]==OUT_DIR:
      i+=1
      out_dir=sys.argv[i]
    elif sys.argv[i]==DOI_LST:
      i+=1
      lst_file = sys.argv[i]
    elif sys.argv[i]==API_KEY:
      i+=1
      api_key = sys.argv[i]
    i+=1
  
  #your api key
  API_KEY_OBTAIN['X-ELS-APIKey'] = api_key
  
  #laod doi list
  lines = []
  with open(lst_file, 'r') as f:
    lines = f.readlines()
  
  print("n_articles : ", len(lines))
  
  #crawl the doi list
  for i,l in enumerate(lines):
    l = l.strip()
    j = download(l)
    if j is None: continue
    
    n = os.path.basename(os.path.join(out_dir, l))
    d = os.path.dirname(os.path.join(out_dir, l))
    
    if d!=out_dir: make_dir(d)
    f = os.path.join(out_dir, l+".html")
    
    with open(f, 'w') as w:
      w.write(j)
    
    if(i%100==0): print("n-article done : ", i)
        
        
