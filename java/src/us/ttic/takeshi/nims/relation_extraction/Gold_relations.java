/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

//import weka.core.pmml.jaxbbindings.REGRESSIONNORMALIZATIONMETHOD;

/**
 * @author takeshi.onishi
 *
 */
public class Gold_relations {
	
	//Factors
	private List<Processing> processings = new ArrayList<Processing>();
	private List<Structure> structures = new ArrayList<Structure>();
	private List<Property> properties = new ArrayList<Property>();
	
	//source IDs
	private Set<String> sourceIds = new TreeSet<String>();
	
	
	//hyper parameters to read
	public static final String RELATION_TAG = ":";
	public static final String PRP_TAG = "Prp";
	public static final String PRC_TAG = "Prc";
	public static final String STR_TAG = "Str";
	public static final String SPLIT_TAG=":::";
	
	public Gold_relations(File cvs_processing, File cvs_property, File cvs_struct, File cvs_relation) throws IOException{
		processings = read_processings(cvs_processing, null);
		structures = read_structures(cvs_struct, null);
		properties = read_properties(cvs_property, null);
		set_relations(cvs_relation, null);
		
		//sort
		Collections.sort(processings);
		Collections.reverse(processings);
		Collections.sort(structures);
		Collections.reverse(structures);
		Collections.sort(properties);
		Collections.reverse(properties);
	}
	
	public Gold_relations(File cvs_processing, File cvs_property, File cvs_struct, File cvs_relation, String sourceID) throws IOException{
		processings = read_processings(cvs_processing, sourceID);
		structures = read_structures(cvs_struct, sourceID);
		properties = read_properties(cvs_property, sourceID);
		set_relations(cvs_relation, sourceID);
		
		//sort
		Collections.sort(processings);
		Collections.reverse(processings);
		Collections.sort(structures);
		Collections.reverse(structures);
		Collections.sort(properties);
		Collections.reverse(properties);
	}
	
	public static List<Processing> read_processings(File cvs, String sourceID) throws IOException{
		List<Processing> facts = new ArrayList<Processing>();
		
		//read all facts without parent
		List<String> lines = Files.readAllLines(cvs.toPath());
		for(String line : lines){
			line = line.trim();
			if(line!=null && !line.isEmpty() && !line.startsWith("#")){
				List<String> elms = Arrays.asList(line.split("\t"));
				
				//first two column is for source information
				String source_id = elms.get(0).trim();
				String source_type = elms.get(1).trim();
				if(sourceID!=null && !sourceID.equals(source_id)) continue; //skip non-target source
				
				//find the fact
				int splt_idx = elms.indexOf(SPLIT_TAG);
				if(splt_idx<0) throw new IOException("invalid file format for fact : No splitter(:::)");
				
				//variety to say
				String[] varieties = new String[elms.size()-(splt_idx+1)]; //this might be empty if the fact can't appear in txt
				for(int i=0;i<varieties.length;i++) varieties[i]=elms.get(splt_idx+1+i).trim();
				
				Processing f = new Processing(elms.get(splt_idx-1).trim(), source_id, source_type, varieties);
				Processing pp_f= null;
				Processing p_f = null;
				for(int i=2;i<splt_idx-1;i++){
					p_f = new Processing(elms.get(i).trim(), source_id, source_type, new String[]{"DUMMY"});
					p_f.set_super(pp_f);
					pp_f=p_f;
				}
				f.set_super(p_f);
				if(!facts.contains(f)){
					facts.add(f);
				}else if(f.get_source_type().equalsIgnoreCase("g")){
					facts.set(facts.indexOf(f), f);
				}
			}
		}
		
		//correct link of super facts
		for(int idx=0;idx<facts.size();idx++) {
			Processing p = facts.get(idx);
			if(p.get_super()!=null){		
				if(!facts.contains(p.get_super())) {
					System.out.println("super facts not appeared in source,... added : "+p.get_super());
					facts.add(p.get_super());
				}
				int i = facts.indexOf(p.get_super());
				p.set_super(facts.get(i));
			}
		}
				
		//add sub factors
		for(Processing f_p : facts)for(Processing f_c : facts)if(f_p.equals(f_c.get_super())){
			f_p.add_sub(f_c);
		}
		return facts;
	}
	public static List<Structure> read_structures(File cvs, String sourceID) throws IOException{
		List<Structure> facts = new ArrayList<Structure>();
		
		//read all facts without parent
		List<String> lines = Files.readAllLines(cvs.toPath());
		for(String line : lines){
			line = line.trim();
			if(line!=null && !line.isEmpty() && !line.startsWith("#")){
				List<String> elms = Arrays.asList(line.split("\t"));
				
				//first two column is for source information
				String source_id = elms.get(0).trim();
				String source_type = elms.get(1).trim();
				if(sourceID!=null && !sourceID.equals(source_id)) continue; //skip non-target source
				
				//find the fact
				int splt_idx = elms.indexOf(SPLIT_TAG);
				if(splt_idx<0) throw new IOException("invalid file format for fact : No splitter(:::) : "+line);
				
				//variety to say
				String[] varieties = new String[elms.size()-(splt_idx+1)]; //this might be empty if the fact can't appear in txt
				for(int i=0;i<varieties.length;i++) varieties[i]=elms.get(splt_idx+1+i).trim();
				
				Structure f = new Structure(elms.get(splt_idx-1).trim(), source_id, source_type, varieties);
				Structure pp_f= null;
				Structure p_f = null;
				for(int i=2;i<splt_idx-1;i++){
					p_f = new Structure(elms.get(i).trim(), source_id, source_type, new String[]{"DUMMY"});
					p_f.set_super(pp_f);
					pp_f=p_f;
				}
				f.set_super(p_f);
				if(!facts.contains(f)){
					facts.add(f);
				}else if(f.get_source_type().equalsIgnoreCase("g")){
					facts.set(facts.indexOf(f), f);
				}
			}
		}
		
		//correct link of super facts
		for(int idx=0;idx<facts.size();idx++) {
			Structure p = facts.get(idx);
			if(p.get_super()!=null){		
				if(!facts.contains(p.get_super())) {
					System.out.println("super facts not appeared in source,... added : "+p.get_super());
					facts.add(p.get_super());
				}
				int i = facts.indexOf(p.get_super());
				p.set_super(facts.get(i));
			}
		}
				
		//add sub factors
		for(Structure f_p : facts)for(Structure f_c : facts)if(f_p.equals(f_c.get_super())){
			f_p.add_sub(f_c);
		}
		return facts;
	}
	public static List<Property> read_properties(File cvs, String sourceID) throws IOException{
		List<Property> facts = new ArrayList<Property>();
		
		//read all facts without parent
		List<String> lines = Files.readAllLines(cvs.toPath());
		for(String line : lines){
			line = line.trim();
			if(line!=null && !line.isEmpty() && !line.startsWith("#")){
				List<String> elms = Arrays.asList(line.split("\t"));
				
				//first two column is for source information
				String source_id = elms.get(0).trim();
				String source_type = elms.get(1).trim();
				if(sourceID!=null && !sourceID.equals(source_id)) continue; //skip non-target source
				
				//find the fact
				int splt_idx = elms.indexOf(SPLIT_TAG);
				if(splt_idx<0) throw new IOException("invalid file format for fact : No splitter(:::)");
				
				//variety to say
				String[] varieties = new String[elms.size()-(splt_idx+1)]; //this might be empty if the fact can't appear in txt
				for(int i=0;i<varieties.length;i++) varieties[i]=elms.get(splt_idx+1+i).trim();
				
				Property f = new Property(elms.get(splt_idx-1).trim(), source_id, source_type, varieties);
				Property pp_f= null;
				Property p_f = null;
				for(int i=2;i<splt_idx-1;i++){
					p_f = new Property(elms.get(i).trim(), source_id, source_type, new String[]{"DUMMY"});
					p_f.set_super(pp_f);
					pp_f=p_f;
				}
				f.set_super(p_f);
				if(!facts.contains(f)){
					facts.add(f);
				}else if(f.get_source_type().equalsIgnoreCase("g")){
					facts.set(facts.indexOf(f), f);
				}
			}
		}
		
		//correct link of super facts
		for(int idx=0;idx<facts.size();idx++) {
			Property p = facts.get(idx);
			if(p.get_super()!=null){		
				if(!facts.contains(p.get_super())) {
					System.out.println("super facts not appeared in source,... added : "+p.get_super());
					facts.add(p.get_super());
				}
				int i = facts.indexOf(p.get_super());
				p.set_super(facts.get(i));
			}
		}
				
		//add sub factors
		for(Property f_p : facts)for(Property f_c : facts)if(f_p.equals(f_c.get_super())){
			f_p.add_sub(f_c);
		}
		return facts;
	}


	
	/**
	 * this set up relation of processing -> structure -> property
	 * PRELIMINALY : 
	 * 		processings, structures and properties must be FILLED 
	 * @param cvs_relation
	 * @throws IOException 
	 */
	private void set_relations(File cvs_relation, String sourceID) throws IOException{
		//Store positive golds
		BufferedReader reader = new BufferedReader(new FileReader(cvs_relation));
		String line;
		while((line=reader.readLine())!=null){
			List<String> elms = Arrays.asList(line.split("\t"));
			
			//first two column is for source information
			String type = elms.get(1).trim();
			if(!type.equalsIgnoreCase("t") && !type.equalsIgnoreCase("g")) System.err.println("unkown sorce tyep : "+type); 
			String source = elms.get(0).trim()+":"+type;
			if(sourceID!=null && !sourceID.equals(elms.get(0).trim())) continue; //skip non-target source
			sourceIds.add(source);
			
			//find delimiter
			int dlm_idx = elms.indexOf(RELATION_TAG);
			if(dlm_idx<0) throw new IOException("invalid file format : "+line);
			String l_type = elms.get(2);
			String r_type = elms.get(dlm_idx+1);
			
			List<General_factor> family = new ArrayList<General_factor>();
			//left
			for(int i=3;i<dlm_idx;i++) family.add(new General_factor(elms.get(i), null, null));
			for(int i=1;i<family.size();i++) family.get(i).set_super(family.get(i-1));
			General_factor l_fact = family.get(family.size()-1);
			
			//right
			family = new ArrayList<General_factor>();
			for(int i=dlm_idx+2;i<elms.size();i++) family.add(new General_factor(elms.get(i),null,null));
			for(int i=1;i<family.size();i++) family.get(i).set_super(family.get(i-1));
			General_factor r_fact = family.get(family.size()-1);
			
			//store relation for each relation type
			if( (l_type.equalsIgnoreCase(PRC_TAG) && r_type.equalsIgnoreCase(STR_TAG))
					|| (l_type.equals(STR_TAG) && r_type.equals(PRC_TAG))){
				General_factor prc = (l_type.equalsIgnoreCase(PRC_TAG))? l_fact : r_fact;
				General_factor str = (l_type.equalsIgnoreCase(STR_TAG))? l_fact : r_fact;
				
				//check
				if(!processings.contains(prc)) throw new IOException("Invalid processing not on dict in relation : "+prc.toString());
				if(!structures.contains(str)) throw new IOException("Invalid structure not on dict in relation : "+str.toString());
				
				Processing p = processings.get(processings.indexOf(prc));
				Structure s = structures.get(structures.indexOf(str));
				p.set_relation(s, new Relation(Relation.GOLD, 1.0));
				
			}else if( (l_type.equalsIgnoreCase(STR_TAG) && r_type.equalsIgnoreCase(PRP_TAG))
						|| (l_type.equalsIgnoreCase(PRP_TAG) && r_type.equalsIgnoreCase(STR_TAG)) ){
				General_factor str = (l_type.equals(STR_TAG))? l_fact : r_fact;
				General_factor prp = (l_type.equals(PRP_TAG))? l_fact : r_fact;
				
				//check
				if(!structures.contains(str)) throw new IOException("Invalid structure not on dict in relation : "+ str);
				if(!properties.contains(prp)) throw new IOException("Invalid property not on dict in realtion : "+prp);
				
				Structure s = structures.get(structures.indexOf(str));
				Property p = properties.get(properties.indexOf(prp));
				s.set_relation(p, new Relation(Relation.GOLD, 1.0));
				
			}else{
				System.err.println("IGNORED RELATION : "+l_type+" -> "+r_type);
			}
		}
		reader.close();
		
		//store negative golds
		for(Processing p : processings)for(Structure s : structures){
			//target?
			if(sourceID!=null && (!p.get_source_id().startsWith(sourceID)||!s.get_source_id().startsWith(sourceID))) continue;
			
			//at least one side factor must be in graph ( having some relation )
			//AND ... negative relation?
			if(s.get_source_type().equalsIgnoreCase("g") && !p.get_relation(s).get_relation().equals(Relation.GOLD)){
				p.set_relation(s, new Relation(Relation.GOLD, 0.0));
			}
		}
		for(Structure s : structures)for(Property p : properties){
			//target?
			if(sourceID!=null && (!p.get_source_id().startsWith(sourceID)||!s.get_source_id().startsWith(sourceID))) continue;
			
			//at least one side factor must be in graph ( having some relation )
			//AND ... negative relation?
			if( p.get_source_type().equalsIgnoreCase("g") && !s.get_relation(p).get_relation().equals(Relation.GOLD)){
				s.set_relation(p, new Relation(Relation.GOLD, 0.0));
			}
		}
	}
	
	public final List<Processing> get_processings(){
		return processings;
	}
	public final List<Structure> get_structures(){
		return structures;
	}
	public final List<Property> get_propeties(){
		return properties;
	}
	
	public final Set<String> get_soruceIDs(){
		return sourceIds;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();

		sb.append("PROCESSINGS : \n");
		for(Processing p : processings){
			sb.append(p.toString()+"\n");
			List<String> pos = new ArrayList<String>();
			List<String> neg = new ArrayList<String>();
			for(Structure s : structures){
				Relation r = p.get_relation(s); 
				if(r.get_relation().equals(Relation.GOLD)){
					if(r.get_score()>0.5){
						pos.add(s.get_name());
					}else{
						neg.add(s.get_name());
					}
				}
			}
			sb.append("\tGOLD_POS("+pos.size()+") :{");
			for(String n : pos) sb.append(n+',');
			sb.append("}\n");
			sb.append("\tGOLD_NEG("+neg.size()+") :{");
			for(String n : neg) sb.append(n+",");
			sb.append("}\n");
		}

		sb.append("STRUCTURES : \n");
		for(Structure s : structures){
			sb.append(s.toString()+"\n");
			List<String> pos = new ArrayList<String>();
			List<String> neg = new ArrayList<String>();
			for(Property p : properties){
				Relation r = s.get_relation(p); 
				if(r.get_relation().equals(Relation.GOLD)){
					if(r.get_score()>0.5){
						pos.add(p.get_name());
					}else{
						neg.add(p.get_name());
					}
				}
			}
			sb.append("\tGOLD_POS ("+pos.size()+"):{");
			for(String n : pos) sb.append(n+',');
			sb.append("}\n");
			sb.append("\tGOLD_NEG("+neg.size()+") :{");
			for(String n : neg) sb.append(n+",");
			sb.append("}\n");
		}
		
		sb.append("PROPERTIES : \n");
		for(Property p : properties) sb.append(p.toString()+"\n");
		
		return sb.toString();
	}
	
	
	public static final String PROPERTY = "--property";
	public static final String PROCESSING = "--processing";
	public static final String STRUCTURE = "--structure";
	public static final String RELATION = "--relation";
	public static final String SOURCE = "--source";
	public static void main(String[] args) throws IOException {
//		String processing = "/home/takeshi.onishi/workspace/material_text/annotations/processing.dct";
//		String property = "/home/takeshi.onishi/workspace/material_text/annotations/property.dct";
//		String structure ="/home/takeshi.onishi/workspace/material_text/annotations/structure.dct";
//		String relation = "/home/takeshi.onishi/workspace/material_text/annotations/relation.csv";
		String processing = "/home/ikumu/workspace/material_text/annotations/processing.dct";
		String property = "/home/ikumu/workspace/material_text/annotations/property.dct";
		String structure ="/home/ikumu/workspace/material_text/annotations/structure.dct";
		String relation = "/home/ikumu/workspace/material_text/annotations/relation.csv";
		String source = null;
		
		for(int i=0;i<args.length;i++) {
			if(args[i].equals(PROPERTY)) {
				property = args[++i];
			}else if(args[i].equals(PROCESSING)) {
				processing = args[++i];
			}else if(args[i].equals(STRUCTURE)) {
				structure = args[++i];
			}else if(args[i].equals(RELATION)) {
				relation = args[++i];
			}else if(args[i].equals(SOURCE)) {
				source = args[++i];
			}
		}
		
		Gold_relations gold = new Gold_relations(new File(processing), new File(property), new File(structure), new File(relation), source);
		//System.out.println(gold.toString());
	}

}
