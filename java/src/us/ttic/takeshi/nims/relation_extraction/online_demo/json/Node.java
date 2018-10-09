/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction.online_demo.json;

/**
 * @author ikumu
 *
 */
public class Node implements Comparable<Node>{

	private final String name;
	private final String type;
	
	public Node(String NAME, String TYPE) {
		name = NAME;
		type = TYPE;
	}
	
	
	public final String getName() {
		return name;
	}

	public final String getType() {
		return type;
	}
	
	public final String toString(){
		return name+"("+type+")";
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(!this.getClass().isInstance(o)) return false;
		
		Node other = (Node)o;
		if(other.name.equals(name) && other.type.equals(type)) return true;
		return false;
	}


	@Override
	public int compareTo(Node o) {
		String self = name+" : "+type;
		String other= o.name+" : "+o.type;
		return self.compareTo(other);
	}
	
	@Override
	public int hashCode(){
		return toString().hashCode();
	}

}
