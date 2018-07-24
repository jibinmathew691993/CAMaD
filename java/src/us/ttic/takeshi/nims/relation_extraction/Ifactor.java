package us.ttic.takeshi.nims.relation_extraction;

import java.util.Collection;

/**
 * Interface for factor. Ifactor does not care about type of fact.
 * 	This is just a object with tree structure
 * @author takeshi.onishi
 *
 * @param <SELF>
 */
public interface Ifactor<SELF>{

	public String get_id();
	public String get_name();
	public String[] get_varieties();
	public String get_source_id();
	public String get_source_type();
	public int get_rank();
	public SELF get_origin();
	public Collection<SELF> get_descendant();
	
	/**
	 * 
	 * @return A super factor, null if there is no super factor
	 */
	public SELF get_super();
	public void set_super(SELF Super_Facter);
	
	/**
	 * 
	 * @return set of sub factors
	 */
	public Collection<SELF> get_subs();
	public void add_sub(SELF sub);
	
}
