/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

import java.util.List;

import edu.stanford.nlp.ling.HasWord;

/**
 * @author ikumu
 *
 */
public class Sentence_itr_wrapper implements Iiterator<List<HasWord>> {

	//store reader
	private final List<? extends Ireader> readers;
	
	//counters
	private int current_reader=-1;
	private Iiterator<List<HasWord>> snt_itr = null;
	
	public Sentence_itr_wrapper(List<? extends Ireader> readers_to_go_throgh) {
		readers = readers_to_go_throgh;
		snt_itr = readers.get(++current_reader).itr_snt();
	}
	
	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Iiterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if(current_reader>=readers.size()-1 && snt_itr!=null && !snt_itr.hasNext()) return false;
		//if(!snt_itr.hasNext() && !readers.get(current_reader+1).itr_snt().hasNext()) return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.Iiterator#next()
	 */
	@Override
	public List<HasWord> next() {
		if(snt_itr.hasNext()) return snt_itr.next();
		//check
		current_reader++;
		//if(!(current_reader<readers.size())) return null;
		snt_itr = readers.get(current_reader).itr_snt();
		return this.next();
	}

}
