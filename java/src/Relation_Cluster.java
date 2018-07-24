import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import us.ttic.takeshi.nims.relation_extraction.relation_model.Target_snt;
import us.ttic.takeshi.tools.Ling;

/**
 * 
 */

/**
 * This is a cluster for a specific relation Like "heat"->"grain", "grain"->"hardness"
 * 
 * 
 * 
 * @author takeshi.onishi
 *
 */
public class Relation_Cluster {
	
	//sentences in cluster
	private final Collection<Target_snt> snts = new ArrayList<Target_snt>();
	
	//cluster name format
	public static final String NAME_FORMAT = "%s_\"%s\"_\"%s\"";
	
	//Target types
	private final String org_type;
	private final String dst_type;
	
	//Target
	private final String org;
	private final String dst;
	
	
	public Relation_Cluster(Collection<Target_snt> sentences){
		//type and org/dst
		String o=null;
		String d=null;
		String ot=null;
		String dt=null;
		for(Target_snt s : sentences){
			o=s.get_orig_label().toLowerCase();
			d=s.get_dest_label().toLowerCase();
			ot=s.getType().first;
			dt=s.getType().second;
			break;
		}
		
		//check if all the same?
		org = o;
		org_type = ot;
		dst = d;
		dst_type = dt;
		for(Target_snt s : sentences){
			snts.add(s);
			o=s.get_orig_label().toLowerCase();
			d=s.get_dest_label().toLowerCase();
			ot=s.getType().first;
			dt=s.getType().second;
			if(!org.equals(o) 
					|| !dst.equals(d)
					|| !org_type.equals(ot)
					|| !dst_type.equals(dt)){
				System.err.println("INVLAID CLUSTER : ");
				return;
			}
		}
		
	}
	
	public final Collection<Target_snt> get_snts() {
		return snts;
	}
	
	public final String get_org(){
		return org;
	}
	public final String get_dst(){
		return dst;
	}
	public final String get_org_type(){
		return org_type;
	}
	public final String get_dst_type(){
		return dst_type;
	}
	
	public void toFile(File out) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		for(Target_snt s : snts){
			writer.write(s.toString());
			writer.newLine();
		}
		writer.close();
	}
	
	
	public void toFile(File out, MaxentTagger tagger) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		for(Target_snt s : snts){
			writer.write(s.toString());
			writer.write("\t"+Ling.tagged2string(tagger.apply(s.getSnt())));
			writer.newLine();
		}
		writer.close();
	}
	
	public String get_Cluster_name(){
		return String.format(NAME_FORMAT, 
							String.format(Target_snt.TYPE_FORMAT, get_org_type(), get_dst_type())
							, get_org()
							, get_dst());
	}
	
	public static String get_cluster_name(Target_snt snt){
		String o=snt.get_orig_label().toLowerCase();
		String d=snt.get_dest_label().toLowerCase();
		return String.format(NAME_FORMAT, 
								String.format(Target_snt.TYPE_FORMAT, snt.getType().first, snt.getType().second)
								, o, d);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
