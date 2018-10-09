package us.ttic.takeshi.html.svg.node.att.val;

public class Str extends Val{

	private java.lang.String val;
	
	public Str(java.lang.String v) {
		val = v;
	}
	
	public java.lang.String get_val() {
		return val;
	}

	@Override
	public java.lang.String get_html() {
		return java.lang.String.format("\"%s\"", val);
	}

}
