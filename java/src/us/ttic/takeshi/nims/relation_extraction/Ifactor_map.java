package us.ttic.takeshi.nims.relation_extraction;

import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;

public interface Ifactor_map {

	public List<Processing> assign_processing(final List<? extends HasWord> snt);
	public List<Structure> assign_structure(final List<? extends HasWord> snt);
	public List<Property> assign_property(final List<? extends HasWord> snt);
	
	public List<Processing> get_prc(Ireader corpus);
	public List<Structure> get_str(Ireader corpus);
	public List<Property> get_prp(Ireader corpus);
}
