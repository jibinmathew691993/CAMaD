package us.ttic.takeshi.nims.relation_extraction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


/**
 * this is super class for processing, structure, property. Trees can not be connected in each type 
 * 	Factors are
 * 		- Tree in each sub class ( might be disjoint )
 * 		- Has specific directional labeled relation in each class by Relation.class
 * 			processing -> structure
 * 			structure -> property
 * 			property -> ---
 * 		- Identified by it's sub-tree so far 
 * 			( this might be changed )
 * @author takeshi.onishi
 *
 */
public class Factor<T extends Ifactor<T>> implements Ifactor<T>, Comparable<T>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3822301999036785137L;
	
	
	protected String name;
	protected String source_id;
	protected String source_type;
	protected T super_factor = null;
	protected Collection<T> subs = new ArrayList<T>();

	//variety to say this factor
	protected String[] varieties = null;
	
	public Factor(){
		//do nothing so far
	}
	public Factor(String NAME, String SOURCE, String TYPE){
		name = NAME;
		source_id=SOURCE;
		source_type = TYPE;
		
		varieties = new String[]{name};
	}
	public Factor(String NAME, String SOURCE, String TYPE, String[] VARIETY_TO_SAY){
		name = NAME;
		source_id=SOURCE;
		source_type=TYPE;
		
		varieties = VARIETY_TO_SAY;
	}
	
	@Override
	public final Collection<T> get_subs(){
		return subs;
	}
	@Override
	public final Collection<T> get_descendant(){
		
		Collection<T> descendants = new ArrayList<T>();
		for(T sub : get_subs()){
			descendants.addAll(sub.get_descendant());
		}
		return descendants;
	}
	
	@Override
	public void add_sub(T sub){
		if(!this.getClass().isInstance(sub)) throw new ClassCastException("invalid sub class : "+sub.getClass()+" for "+this.getClass());
		subs.add(sub);
	}
	
	@Override
	public String get_id(){
		T f = this.get_super();
		return (f==null)? get_name().toLowerCase() : f.get_id()+" > "+get_name().toLowerCase();
	}
	
	@Override
	public void set_super(T Super_Factor){
		if(Super_Factor==null){
			super_factor=null;
			return;
		}
		if(!this.getClass().isInstance(Super_Factor)) throw new ClassCastException("invalid super class : "+Super_Factor.getClass()+" for "+this.getClass());
		super_factor = Super_Factor;
	}

	@Override
	public T get_super(){
		return super_factor;
	}
	
	@Override
	public final String get_source_id(){
		return source_id;
	}
	@Override
	public final String get_source_type(){
		return source_type;
	}
	@Override
	public String get_name() {
		return name;
	}
	@Override
	public final String[] get_varieties(){
		return varieties;
	}
	@Override
	public int get_rank() {
		if(this.get_super()==null) return 0;
		return this.get_super().get_rank()+1;
	}
	@Override
	public T get_origin(){
		if(this.get_super()==null) return (T) this;
		return this.get_super().get_origin();
	}
	
	@Override
	public String toString(){
		StringBuffer writer = new StringBuffer();
		for(String v : varieties) writer.append(v+",");
		return String.format("%s:::[%s]", get_id(), writer.toString());
	}
	
	
	@Override
	public int hashCode() {
		return this.get_id().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		
		if(Factor.class.isAssignableFrom(obj.getClass()) && Factor.class.cast(obj).get_id().equals(this.get_id())) return true;
		
		if(!this.getClass().isInstance(obj)) return false;
		if(this.get_id().equals( this.getClass().cast(obj).get_id() )){
			return true;
		}
		return false;
	}
	
	@Override
	public int compareTo(T obj){
		if(obj==null) throw new NullPointerException();
		return this.get_id().compareTo(obj.get_id());
		
		
//		if(this.get_id().equals(obj.get_id())) return 0;
//		
//		int asc = 0;
//		if(obj.get_super()!=null){
//			asc = this.compareTo(obj.get_super());
//		}else if(this.get_super()!=null){
//			asc = this.getClass().cast(this.get_super()).compareTo(obj);
//		}else{ //(obj.get_super()==null && this.get_super()==null)
//			if(this.get_name().toLowerCase().equals(obj.get_name().toLowerCase())) return 0;
//			//compare from original factor / most ascent factor 
//			return (this.get_name().toLowerCase().hashCode()>obj.get_name().toLowerCase().hashCode())? 1 : -1;
//		}
//		if(asc!=0) return asc;
//		//return (this.get_name().toLowerCase().hashCode()>obj.get_name().toLowerCase().hashCode())? 1 : -1;
//		return (this.get_rank()<obj.get_rank())? 1: -1;		
	}

}
