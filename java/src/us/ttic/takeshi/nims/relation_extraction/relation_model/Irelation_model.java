package us.ttic.takeshi.nims.relation_extraction.relation_model;

import java.util.List;

import us.ttic.takeshi.nims.relation_extraction.Ireader;
//import us.ttic.takeshi.nims.relation_extraction.Olson_chart;
import us.ttic.takeshi.nims.relation_extraction.Processing;
import us.ttic.takeshi.nims.relation_extraction.Property;
import us.ttic.takeshi.nims.relation_extraction.Structure;

/**
 * 
 * @author takeshi.onishi
 */
public interface Irelation_model {
	
	//test
	//Specify target
	//public Olson_chart get_chart(List<Property> props);
	//public Olson_chart get_chart(List<Processing> process, List<Property> props);
	//public Olson_chart get_chart(List<Processing> process, List<Structure> structures, List<Property> props);
	
	//train
	//un-supervised
	public void train(Ireader corpus);
	//supervised
	public void train(Ireader corpus, List<Processing> props, List<Structure> structures) throws Exception;
	
	//track proof
	public String get_proof(Processing p, Structure s);
	public String get_proof(Structure s, Property p);
}
