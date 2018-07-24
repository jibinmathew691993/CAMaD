/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

/**
 * @author takeshi.onishi
 *
 */
public class Sentence_itr implements Iiterator<List<HasWord>> {

	private int crrPara=-1;
	private List<String> paras;
	private Iterator<List<HasWord>> doc_itr = null;
	
	public Sentence_itr(List<String> para){
		crrPara=0;
		this.paras = new ArrayList<String>(para);
	}
	
	public Sentence_itr(String para){
		crrPara=0;
		this.paras=new ArrayList<String>();
		paras.add(para);
	}
	
	@Override
	public boolean hasNext() {
		if( crrPara!=paras.size() || (doc_itr!=null && doc_itr.hasNext()) ){
			return true;
		}
		return false;
	}

	@Override
	public List<HasWord> next() {
		//in para
		if(doc_itr!=null && doc_itr.hasNext()){
			return doc_itr.next();
		}else{//nex para
			DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(paras.get(crrPara++)));
			doc_itr=dp.iterator();
			return this.next();
		}
	}
	
	

}
