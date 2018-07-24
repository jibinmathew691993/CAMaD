package us.ttic.takeshi.nims.relation_extraction;

public class Property extends Factor<Property> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2175272592116065935L;

	public Property(String NAME, String SOURCE, String TYPE){
		super(NAME, SOURCE, TYPE);
	}
	public Property(String NAME, String SOURCE, String TYPE, String[] VARIETY_TO_SAY) {
		super(NAME, SOURCE, TYPE, VARIETY_TO_SAY);
	}
//	public Property(Factor<?> f){
//		super(f.get_name(), f.get_source());
//		
//		
//		this.super_factor=f.get_super();
//		this.subs = f.get_subs();
//	}
	
	public String toString(){
		return this.get_id();
	}

}
