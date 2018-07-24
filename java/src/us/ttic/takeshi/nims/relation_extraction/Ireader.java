package us.ttic.takeshi.nims.relation_extraction;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;

public interface Ireader {
	
	public String get_id();

	/**
	 * this is method to return list of paragraph in the text
	 * @return
	 */
	public List<String> paragraph();
	
	/**
	 * this returns iterator of all sentence in the text
	 * @return
	 */
	public Iiterator<List<HasWord>> itr_snt();
	
	/**
	 * this returns iterator of all sentence in the chapter
	 * @param chapter_idx
	 * @return
	 */
	public Iiterator<List<HasWord>> itr_snt(int chapter_idx);
	
	/**
	 * this returns chapter size of the text
	 * @return
	 */
	public int chapter_size();
	
	public Map<String, Integer> word_freq(final int n_gram);
	public Set<String> vocab(final int n_gram);
	public Map<Phrase, Double> max_tf_idf(int n_gram);

	//public Map<Phrase, Double> max_tf_idf_noun();
}
