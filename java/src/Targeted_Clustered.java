
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import us.ttic.takeshi.nims.relation_extraction.ArticleSet;
import us.ttic.takeshi.nims.relation_extraction.Gold_relations;

import us.ttic.takeshi.nims.relation_extraction.Ireader;
//import us.ttic.takeshi.nims.relation_extraction.Main_root;
import us.ttic.takeshi.nims.relation_extraction.Processing;
import us.ttic.takeshi.nims.relation_extraction.Property;
import us.ttic.takeshi.nims.relation_extraction.Relation;
import us.ttic.takeshi.nims.relation_extraction.Structure;
import us.ttic.takeshi.nims.relation_extraction.relation_model.Lazy;
import us.ttic.takeshi.nims.relation_extraction.relation_model.Target_snt;

/**
 * 
 */

/**
 * This is a script to save tagged sentence files for each relations, directory structure for a given dir D is
 * 
 *  D
 *  |- relation_1
 *  |- relation_2
 *  |- ...
 *  |- relation_i
 *  
 *  See "Relation Cluster" for format in the files
 *  
 * @author takeshi.onishi
 *
 */
public class Targeted_Clustered{
	
	//file
	static final String FILE_BASE = "relation_%d";

	static final String SC_DIR_OPT = "-sc_dir";
	static final String PRC_OPT = "-prc";
	static final String STR_OPT = "-str";
	static final String PRP_OPT = "-prp";
	static final String RELATION_OPT = "-relation";
	static final String OUT_TAG = "-out";
	static final String SOURCE_TAG = "-source";
	static final String POS_TAGGER = "-pos_tagger";
	public static void main(String[] args) throws Exception {
		String sc_dir  = null;
		String prc = null;
		String str = null;
		String prp = null;
		String relation = null;
		String out = null;
		String source = null;
		String pos_tagger = null;
		
		for(int i=0;i<args.length;i++){
			if(args[i].equals(SC_DIR_OPT)){
				sc_dir = args[++i];
				System.out.println(SC_DIR_OPT+" : "+sc_dir);
			}else if(args[i].equals(PRC_OPT)){
				prc=args[++i];
				System.out.println(PRC_OPT+" : "+prc);
			}else if(args[i].equals(STR_OPT)){
				str=args[++i];
				System.out.println(STR_OPT+" : "+str);
			}else if(args[i].equals(PRP_OPT)){
				prp=args[++i];
				System.out.println(PRP_OPT+" : "+prp);
			}else if(args[i].equals(RELATION_OPT)){
				relation = args[++i];
				System.out.println(RELATION_OPT+" : "+relation);
			}else if(args[i].equals(OUT_TAG)){
				out=args[++i];
				System.out.println(OUT_TAG+" : "+out);
			}else if(args[i].equals(SOURCE_TAG)){
				source = args[++i];
				System.out.println(SOURCE_TAG+" : "+source);
			}else if(args[i].equals(POS_TAGGER)){
				pos_tagger = args[++i];
				System.out.println(POS_TAGGER+" : "+pos_tagger);
			}
		}
		
		//tagger
		MaxentTagger tagger = new MaxentTagger(pos_tagger);
		
		//corpus
		Ireader reader = null;
		if(sc_dir!=null){
			reader = new ArticleSet(new File(sc_dir));
		}
		
		//factors
		if(prc==null || str==null ||prp==null){
			System.err.println("invalid option : all of -prp, -str, -prc are required");
		}
		List<Processing> prcessing = Gold_relations.read_processings(new File(prc), source);
		List<Structure> structure = Gold_relations.read_structures(new File(str), source);
		List<Property>  property = Gold_relations.read_properties(new File(prp), source);

		Lazy l = new Lazy(prcessing, structure, property);
		
		//relations
		if(relation==null){ //for unknown ralations
			System.err.println("INVALID_ARG : relation="+relation);
			return;
		}
		//train
		Gold_relations terms = new Gold_relations(new File(prc),new File(prp)
				, new File(str), new File(relation), source);
		
		//print dist of gold relations
		System.out.println("gold knowledge in : "+ relation);
		System.out.println("PRC : "+terms.get_processings().size());
		System.out.println("STR : "+terms.get_structures().size());
		System.out.println("PRP : "+terms.get_propeties().size());
		
		int pos_p2s = 0;
		int neg_p2s = 0;
		for(Processing p : terms.get_processings())for(Structure s : terms.get_structures()) if(p.get_relation(s).get_relation().equals(Relation.GOLD)){
			if(p.get_relation(s).get_score()>0.0) {
				pos_p2s++;
			}else {
				neg_p2s++;
			}
		}
		System.out.println("n_positive PRC2STR : "+pos_p2s);
		System.out.println("n_negative PRC2STR : "+neg_p2s);
		
		int pos_s2p = 0;
		int neg_s2p = 0;
		for(Structure s : terms.get_structures())for(Property p : terms.get_propeties()) if(s.get_relation(p).get_relation().equals(Relation.GOLD) ){
			if( s.get_relation(p).get_score()>0.0) {
				pos_s2p++;
			}else {
				neg_s2p++;
			}
		}
		System.out.println("n_positive STR2PRP : "+pos_s2p);
		System.out.println("n_negative STR2PRP : "+neg_s2p);
		
		
		l.train(reader, terms.get_processings(), terms.get_structures());
		System.out.println("Corpus loaded");
		
		//clustering by target
		Map<String, Collection<Target_snt>> cluster = new TreeMap<String, Collection<Target_snt>>();
		//prc->str
		for(Target_snt s : l.get_prc2str()){
			String n = Relation_Cluster.get_cluster_name(s);
			n = n.replaceAll("/", " ");
			if(!cluster.containsKey(n)) cluster.put(n, new ArrayList<Target_snt>());
			cluster.get(n).add(s);
			if(cluster.size()%100==0) System.out.println("clusters : "+cluster.size());
		}
		//str->prp
		for(Target_snt s : l.get_str2prp()){
			String n = Relation_Cluster.get_cluster_name(s);
			n = n.replaceAll("/", " ");
			if(!cluster.containsKey(n)) cluster.put(n, new ArrayList<Target_snt>());
			cluster.get(n).add(s);
			if(cluster.size()%100==0) System.out.println("clusters : "+cluster.size());

		}
		
		for(Map.Entry<String, Collection<Target_snt>> e : cluster.entrySet()){
			try{
				Relation_Cluster c =new Relation_Cluster(e.getValue());
				c.toFile(new File(out, c.get_Cluster_name()+".lst"), tagger);
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}

		System.out.println("DONE");
	}

}
