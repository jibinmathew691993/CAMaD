/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction.relation_model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.common.Tagger;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import us.ttic.takeshi.nims.relation_extraction.ArticleSet;
import us.ttic.takeshi.nims.relation_extraction.Gold_relations;
import us.ttic.takeshi.nims.relation_extraction.Iiterator;
import us.ttic.takeshi.nims.relation_extraction.Ireader;
import us.ttic.takeshi.nims.relation_extraction.Processing;
import us.ttic.takeshi.nims.relation_extraction.Property;
import us.ttic.takeshi.nims.relation_extraction.Relation;
//import us.ttic.takeshi.nims.relation_extraction.Ris_reader;
import us.ttic.takeshi.nims.relation_extraction.Structure;
//import us.ttic.takeshi.nims.relation_extraction.factor_recgnizer.Ifactor_recognizer;
//import us.ttic.takeshi.nims.relation_extraction.factor_recgnizer.S_NER_based;
//import us.ttic.takeshi.nims.relation_extraction.factor_recgnizer.Tf_idf;
import us.ttic.takeshi.tools.Pair;
//import us.ttic.takeshi.tools.RunConfiguation;

/**
 * @author ikumu
 *
 */
public class Lazy extends Relation_model {
	/**
	 * layzy model saves rIs format file as lazy training
	 * FORMAT
	 * 	labeled sentence per line
	 * 
	 * + [source_name]\t[0/1 : related or not]\t[tagged sentence]
	 * + [tagged sentence] : seq of words splited by white space tagged by the followings.
	 * + tags : { @STR, STR@, @PRP, PRP@, @PRC, PRC@}  
	 */
	
	//sentence, index of target
	private Collection<Target_snt> prc2str_snts = new ArrayList<Target_snt>();
	private Collection<Target_snt> str2prp_snts = new ArrayList<Target_snt>();
	
	public static final String SUFFIX="lrs";
	
	/**
	 * @param process_dict
	 * @param structure_dict
	 * @param property_dict
	 */
	public Lazy(List<Processing> process_dict, List<Structure> structure_dict, List<Property> property_dict) {
		super(process_dict, structure_dict, property_dict);
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.relation_model.Irelation_model#train(us.ttic.takeshi.nims.relation_extraction.Ireader)
	 */
	@Override
	public void train(Ireader corpus) {
		System.err.println("LAZY MODEL DOES NOT SUPPORT UN-SUPERVISED");
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.relation_model.Irelation_model#train(us.ttic.takeshi.nims.relation_extraction.Ireader, java.util.List, java.util.List)
	 */
	@Override
	public void train(Ireader corpus, List<Processing> process, List<Structure> structures) throws Exception {
		train_corpus = corpus;
		System.out.println("Layzy : train(Ireader corpus, List<Processing> props, List<Structure> structures) ; collecting sentence with relations"); 
		
		Iiterator<List<HasWord>> snt_itr = corpus.itr_snt();
		while(snt_itr.hasNext()){
			List<HasWord> snt = snt_itr.next();
			List<Processing> prc=dict.assign_processing(snt);
			List<Structure> str = dict.assign_structure(snt);
			List<Property> prp = dict.assign_property(snt);
			
			//prc -> str
			Collection<Pair<Pair<Integer>>> prc2str = prc2str_pairs(prc, str);
			for(Pair<Pair<Integer>> f_pair : prc2str){
				Pair<Integer> p_idces = f_pair.first;
				Pair<Integer> s_idces = f_pair.second;
				
				//ignore UNKNOWN factors <= in dictionary but not on training data
				if(!process.contains(prc.get(p_idces.first)) || !structures.contains(str.get(s_idces.first))) continue;
				Processing p = process.get(process.indexOf(prc.get(p_idces.first)));
				Structure s = structures.get(structures.indexOf(str.get(s_idces.first)));
				if(!p.get_relation(s).get_relation().equals(Relation.GOLD)) continue;
				
				Relation r = p.get_relation(s);
				
				prc2str_snts.add(new Target_snt(snt, f_pair
												, r.get_score(), new Pair<String>(Target_snt.PRC_TAG, Target_snt.STR_TAG), p.get_name(), s.get_name()));
			}
			

			//str->prp
			Collection<Pair<Pair<Integer>>> str2prp = str2prp_pairs(str, prp);
			for(Pair<Pair<Integer>> f_pair : str2prp){
				Pair<Integer> s_idces = f_pair.first;
				Pair<Integer> p_idces = f_pair.second;
				
				//ignore UNKNOWN factors
				if(!structures.contains(str.get(s_idces.first))) continue;
				Structure s = structures.get(structures.indexOf(str.get(s_idces.first)));
				Property p = prp.get(p_idces.first);
				if(!s.get_relation(p).get_relation().equals(Relation.GOLD)) continue;
				
				Relation r = s.get_relation(p);
				
				str2prp_snts.add(new Target_snt(snt, f_pair
												,r.get_score(), new Pair<String>(Target_snt.STR_TAG, Target_snt.PRP_TAG), s.get_name(), p.get_name()));
			}
			
		}

	}
	
	public Collection<Target_snt> targeting(List<HasWord> snt ){
		List<Processing> prc=dict.assign_processing(snt);
		List<Structure> str = dict.assign_structure(snt);
		List<Property> prp = dict.assign_property(snt);
		
		List<Target_snt> targeted = new ArrayList<Target_snt>();
		//prc -> str
		Collection<Pair<Pair<Integer>>> prc2str = prc2str_pairs(prc, str);
		for(Pair<Pair<Integer>> f_pair : prc2str){
			Processing p = prc.get(f_pair.first.first);
			Structure  s = str.get(f_pair.second.first);
			
			targeted.add( new Target_snt(snt, f_pair, -1.0 , new Pair<String>(Target_snt.PRC_TAG, Target_snt.STR_TAG), p.get_name(), s.get_name()) );
		}
		

		//str->prp
		Collection<Pair<Pair<Integer>>> str2prp = str2prp_pairs(str, prp);
		for(Pair<Pair<Integer>> f_pair : str2prp){
			Structure s = str.get(f_pair.first.first);
			Property p  = prp.get(f_pair.second.first);
			
			targeted.add(new Target_snt(snt, f_pair, -1.0, new Pair<String>(Target_snt.STR_TAG, Target_snt.PRP_TAG), s.get_name(), p.get_name()));
		}
		
		return targeted;
	}
	
	public Collection<Target_snt> get_prc2str(){
		return prc2str_snts;
	}
	public Collection<Target_snt> get_str2prp(){
		return str2prp_snts;
	}
	
	public void toFile(File out) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		for(Target_snt t : prc2str_snts){
			writer.write(t.toString());
			writer.newLine();
		}
		for(Target_snt t : str2prp_snts){
			writer.write(t.toString());
			writer.newLine();
		}
		writer.close();
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.relation_model.Irelation_model#get_proof(us.ttic.takeshi.nims.relation_extraction.Processing, us.ttic.takeshi.nims.relation_extraction.Structure)
	 */
	@Override
	public String get_proof(Processing p, Structure s) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.relation_model.Irelation_model#get_proof(us.ttic.takeshi.nims.relation_extraction.Structure, us.ttic.takeshi.nims.relation_extraction.Property)
	 */
	@Deprecated
	@Override
	public String get_proof(Structure s, Property p) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see us.ttic.takeshi.nims.relation_extraction.relation_model.Relation_model#push_relation(java.util.List, java.util.List, java.util.List)
	 */
	@Deprecated
	@Override
	public void push_relation(List<Processing> process, List<Structure> structures, List<Property> props) {
		System.err.println("LAZY DOES NOT SUPPORT : push_relation(List<Processing> process, List<Structure> structures, List<Property> props" );
	}

	/**
	 * -ris_dir : dir path to ris files
	 * -sc_dir : dir path to .html files
	 * -prc : .dct file
	 * -str : .dct file
	 * -prp : .dct file
	 * -relation : .csv file
	 * @param args
	 * @throws Exception 
	 */
//	static final String RIS_DIR_OPT = "-ris_dir";
//	static final String SC_DIR_OPT = "-sc_dir";
//	static final String PRC_OPT = "-prc";
//	static final String STR_OPT = "-str";
//	static final String PRP_OPT = "-prp";
//	static final String RELATION_OPT = "-relation";
//	static final String OUT_TAG = "-out";
//	static final String SOURCE_TAG = "-source";
//	public static void main(String[] args) throws Exception {
//		String ris_dir = null;
//		String sc_dir  = null;
//		String prc = null;
//		String str = null;
//		String prp = null;
//		String relation = null;
//		String out = null;
//		String source = null;
//		
//		for(int i=0;i<args.length;i++){
//			if(args[i].equals(RIS_DIR_OPT)){
//				ris_dir = args[++i];
//			}else if(args[i].equals(SC_DIR_OPT)){
//				sc_dir = args[++i];
//			}else if(args[i].equals(PRC_OPT)){
//				prc=args[++i];
//			}else if(args[i].equals(STR_OPT)){
//				str=args[++i];
//			}else if(args[i].equals(PRP_OPT)){
//				prp=args[++i];
//			}else if(args[i].equals(RELATION_OPT)){
//				relation = args[++i];
//			}else if(args[i].equals(OUT_TAG)){
//				out=args[++i];
//			}else if(args[i].equals(SOURCE_TAG)){
//				source = args[++i];
//			}
//		}
//		
//		//corpus
//		Ireader reader = null;
//		if(ris_dir!=null && sc_dir!=null){
//			System.err.println("invalid option : "+ris_dir+" and "+sc_dir);
//			return;
//		}else if(ris_dir!=null){
//			reader = new Ris_reader(new File(ris_dir));
//		}else if(sc_dir!=null){
//			reader = new ArticleSet(new File(sc_dir));
//		}
//		
//		//factors
//		if(prc==null || str==null ||prp==null){
//			System.err.println("invalid option : all of -prp, -str, -prc are required");
//		}
//		List<Processing> prcessing = Gold_relations.read_processings(new File(prc), source);
//		List<Structure> structure = Gold_relations.read_structures(new File(str), source);
//		List<Property>  property = Gold_relations.read_properties(new File(prp), source);
//
//		Lazy l = new Lazy(prcessing, structure, property);
//		
//		//relations
//		if(relation==null){ //for unknown ralations
//			BufferedWriter writer = new BufferedWriter(new FileWriter(out));
//			Iiterator<List<HasWord>> snt_itr = reader.itr_snt();
//			while(snt_itr.hasNext())for(Target_snt targeted : l.targeting(snt_itr.next())){
//				writer.write(targeted.toString());
//				writer.newLine();
//			}
//			writer.close();
//		}else{ //only golds
//			//train
//			Gold_relations terms = new Gold_relations(new File(prc),new File(prp)
//					, new File(str), new File(relation), source);
//			l.train(reader, terms.get_processings(), terms.get_structures());
//			
//			//save
//			l.toFile(new File(out));
//		}
//
//		System.out.println("DONE");
//	}

}
