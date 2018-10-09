package us.ttic.takeshi.html.svg.node.att;

import us.ttic.takeshi.html.svg.node.att.val.IVal;

public class Attribute<T extends IVal> implements IAttribute, Comparable<Attribute<?>> {
	
	private T val;
	private String name;
	
	public Attribute(String NAME, T VAL){
		name = NAME;
		val = VAL;
	}
	
	public String get_name() {
		return name;
	}
	public String get_val() {
		return val.get_html();
	}

	@Override
	public int compareTo(Attribute<?> o) {
		return this.name.compareTo(o.name);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Attribute<?>)) return false;
		Attribute<?> a = (Attribute<?>) o;
		return this.name.equals(a.name);
	}
}
