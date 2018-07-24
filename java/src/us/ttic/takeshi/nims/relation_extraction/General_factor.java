/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction;

/**
 * @author takeshi.onishi
 *
 */
public class General_factor extends Factor<General_factor> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4203617342354321266L;

	public General_factor(String NAME, String SOURCE, String TYPE){
		super(NAME, SOURCE, TYPE);
	}
	public General_factor(String NAME, String SOURCE, String TYPE, String[] VARIETY_TO_SAY) {
		super(NAME, SOURCE, TYPE, VARIETY_TO_SAY);
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null) return false;
		
		if(Factor.class.isAssignableFrom(obj.getClass()) && Factor.class.cast(obj).get_id().equals(this.get_id())) return true;
		
		if(!obj.getClass().equals(this.getClass())) return false;
		if(this.get_id().equals( this.getClass().cast(obj).get_id() )){
			return true;
		}
		return false;
	}
}
