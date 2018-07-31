/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;

/**
 * @author takeshi.onishi
 *
 */
public abstract class Reader implements Ireader {
	
	//hyper for language resource
	public static final String NN_LABEL = "NN";
	public static final String NNP_LABEL = "NNP";
	public static final String NNS_LABEL = "NNS";
	public static final String NP_LABEL = "NP";
	
	//parameters for parser
	//protected  LexicalizedParser l_parser=null;
//	public static int PARSER_MAX_SNT=20; 
	
	public Reader(){
	}
//	public Reader(LexicalizedParser lexical_parser){
//		l_parser = lexical_parser;
//	}
	
	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Ireader#paragraph()
	 */
	@Override
	public abstract List<String> paragraph();

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Ireader#itr_snt()
	 */
	@Override
	public abstract Iiterator<List<HasWord>> itr_snt();

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Ireader#itr_snt(int)
	 */
	@Override
	public abstract Iiterator<List<HasWord>> itr_snt(int chapter_idx);

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Ireader#chapter_size()
	 */
	@Override
	public abstract int chapter_size();

//	/**
//	 *
//	 * @return
//	 * 	Map<word, tf-idf>
//	 */
//	public final Map<String, Double> max_tf_idf(){
//		//tf-idf : documented by chapter
//		
//		//idf counter
//		Map<String, Double> idf_mono = new HashMap<String, Double>(); //number of doc where the word appears normalized over total documents
//		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
//			Set<String> bag_of_words = new HashSet<String>();
//			Iiterator<List<HasWord>> itr = this.itr_snt(ch_idx);
//			while(itr.hasNext())for(HasWord w : itr.next())bag_of_words.add(w.word().toLowerCase());
//			
//			for(String w : bag_of_words){
//				if(!idf_mono.containsKey(w)) idf_mono.put(w, 0.0);
//				idf_mono.put(w, idf_mono.get(w)+1.0);
//			}
//		}
//		for(String w : idf_mono.keySet()){
//			idf_mono.put(w, -Math.log(idf_mono.get(w)/this.chapter_size()));
//		}
//		
//		
//		//tf counter
//		List<Map<String, Double>> tf_mono = new ArrayList<Map<String, Double>>(); //tf.get(ch_idcx).get(word)
//		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
//			Map<String, Double> tf_d = new HashMap<String, Double>();
//			int ch_size=0;
//			Iiterator<List<HasWord>> itr = this.itr_snt(ch_idx);
//			while(itr.hasNext())for(HasWord w : itr.next()){
//				String lower = w.word().toLowerCase();
//				if(!tf_d.containsKey(lower)) tf_d.put(lower, 0.0);
//				
//				tf_d.put(lower, tf_d.get(lower)+1.0);
//				ch_size++;
//			}
//			
//			//normalize over chapter
//			for(String k : tf_d.keySet()) tf_d.put(k, tf_d.get(k)/ch_size);
//			tf_mono.add(tf_d);
//		}
//		
//		//tf-idf sort
//		Map<String, Double> max_tf_idf = new HashMap<String, Double>();
//		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
//			//words in chapter
//			for(String w : tf_mono.get(ch_idx).keySet()){
//				double tfidf = tf_mono.get(ch_idx).get(w)*idf_mono.get(w);
//				if(!max_tf_idf.containsKey(w) || tfidf>max_tf_idf.get(w)){
//					max_tf_idf.put(w, tfidf);
//				}
//			}
//		}
//		
//		return max_tf_idf;
//	}
	
	@Override
	public final Map<Phrase, Double> max_tf_idf(int n_gram){
		//tf-idf : documented by chapter
		
		//idf counter
		Map<Phrase, Double> idf = new HashMap<Phrase, Double>(); //number of doc where the word appears normalized over total documents
		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
			
			//collect phrase for each chapter
			Set<Phrase> bag_of_words = new HashSet<Phrase>();
			Iiterator<List<HasWord>> itr = this.itr_snt(ch_idx);
			while(itr.hasNext()){
				List<HasWord> snt = itr.next();
				for(int i=0;i<=snt.size()-n_gram;i++){
					bag_of_words.add(new Phrase(snt.subList(i, i+n_gram)));
				}
			}
			
			for(Phrase w : bag_of_words){
				if(!idf.containsKey(w)) idf.put(w, 0.0);
				idf.put(w, idf.get(w)+1.0);
			}
		}
		for(Phrase w : idf.keySet()){
			idf.put(w, -Math.log(idf.get(w)/this.chapter_size()));
		}
		
		
		//tf counter
		List<Map<Phrase, Double>> tf = new ArrayList<Map<Phrase, Double>>(); //tf.get(ch_idcx).get(phrase)
		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
			Map<Phrase, Double> tf_d = new HashMap<Phrase, Double>();
			int ch_size=0;
			Iiterator<List<HasWord>> itr = this.itr_snt(ch_idx);
			while(itr.hasNext()){
				List<HasWord> snt = itr.next();
				for(int i=0;i<=snt.size()-n_gram;i++){
					Phrase ph = new Phrase(snt.subList(i, i+n_gram));
					if(!tf_d.containsKey(ph)) tf_d.put(ph, 0.0);
					tf_d.put(ph, tf_d.get(ph)+1.0);
					
					ch_size++;
				}
			}
			
			//normalize over chapter
			for(Phrase k : tf_d.keySet()) tf_d.put(k, tf_d.get(k)/ch_size);
			tf.add(tf_d);
		}
		
		//tf-idf sort
		Map<Phrase, Double> max_tf_idf = new HashMap<Phrase, Double>();
		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
			//words in chapter
			for(Phrase w : tf.get(ch_idx).keySet()){
				double tfidf = tf.get(ch_idx).get(w)*idf.get(w);
				if(!max_tf_idf.containsKey(w) || tfidf>max_tf_idf.get(w)){
					max_tf_idf.put(w, tfidf);
				}
			}
		}
		
		return max_tf_idf;
	}
	
	/**
	 * frequency of each words in the corpus
	 * @param n_gram
	 * @return
	 */
	public final Map<String, Integer> word_freq(final int n_gram){
		Map<String, Integer> freq = new HashMap<String, Integer>();
		
		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
			Iiterator<List<HasWord>> itr = this.itr_snt(ch_idx);
			while(itr.hasNext()){
				List<HasWord> snt = itr.next();
				//for each position
				for(int i=0;i<snt.size()-n_gram+1;i++){
					StringBuffer phrase = new StringBuffer();
					for(int j=i;j<i+n_gram;j++) phrase.append(snt.get(j).word().toLowerCase()+" ");
					
					String lower = phrase.toString().trim();
					if(!freq.containsKey(lower)) freq.put(lower, 0);
					freq.put(lower, freq.get(lower)+1);
				}
			}
		}
		return freq;
	}
	
//	public final Map<Phrase, Double> max_tf_idf_noun(){
//		//tf-idf : documented by chapter
//		
//		List<Map<Phrase, Double>> tf = new ArrayList<Map<Phrase, Double>>(); //tf.get(ch_idcx).get(phrase)
//		Map<Phrase, Double> idf = new HashMap<Phrase, Double>(); //number of doc where the word appears normalized over total documents
//		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
//			//for tf
//			Map<Phrase, Double> tf_d = new HashMap<Phrase, Double>();
//			int ch_size=0;
//			
//			//for idx
//			Set<Phrase> bag_of_words = new HashSet<Phrase>();
//			Iiterator<List<HasWord>> itr = this.itr_snt(ch_idx);
//			while(itr.hasNext()){
//				List<HasWord> snt = itr.next();
//				//avoid too long
//				if(snt.size()>PARSER_MAX_SNT) continue;
//				Tree root = l_parser.parse(snt);
//				
//				Set<Tree> subs = root.subTrees();
//				for(Tree t : subs){
//					if(t.label()!=null && (t.label().value().equals(NN_LABEL) || t.label().value().equals(NP_LABEL))){
//						Phrase ph = new Phrase(t.yieldWords());
//						bag_of_words.add(ph);
//						
//						if(!tf_d.containsKey(ph)) tf_d.put(ph, 0.0);
//						tf_d.put(ph, tf_d.get(ph)+1.0);
//						
//						ch_size++;
//					}
//				}
//			}
//			
//			//count idf
//			for(Phrase w : bag_of_words){
//				if(!idf.containsKey(w)) idf.put(w, 0.0);
//				idf.put(w, idf.get(w)+1.0);
//			}
//			
//			//normalize tf over chapter size
//			for(Phrase k : tf_d.keySet()) tf_d.put(k, tf_d.get(k)/(double)ch_size);
//			tf.add(tf_d);
//		}
//		//normalize idf over chapter
//		for(Phrase w : idf.keySet()){
//			idf.put(w, -Math.log(idf.get(w)/(double)this.chapter_size()));
//		}
//		
//		
//		//tf-idf sort
//		Map<Phrase, Double> max_tf_idf = new HashMap<Phrase, Double>();
//		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
//			//words in chapter
//			for(Phrase w : tf.get(ch_idx).keySet()){
//				double tfidf = tf.get(ch_idx).get(w)*idf.get(w);
//				if(!max_tf_idf.containsKey(w) || tfidf>max_tf_idf.get(w)){
//					max_tf_idf.put(w, tfidf);
//				}
//			}
//		}
//		
//		return max_tf_idf;	
//	}
	
	public final Set<String> vocab(final int n_gram){
		Set<String> vocab = new HashSet<String>();
		
		for(int ch_idx=0;ch_idx<this.chapter_size();ch_idx++){
			Iiterator<List<HasWord>> itr = this.itr_snt(ch_idx);
			while(itr.hasNext()){
				List<HasWord> snt = itr.next();
				//for each position
				for(int i=0;i<snt.size()-n_gram+1;i++){
					StringBuffer phrase = new StringBuffer();
					for(int j=i;j<i+n_gram;j++) phrase.append(snt.get(j).word().toLowerCase()+" ");
					vocab.add(phrase.toString().trim());
				}
			}
		}
		
		return vocab;
	}
	
}
