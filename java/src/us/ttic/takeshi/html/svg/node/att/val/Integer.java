package us.ttic.takeshi.html.svg.node.att.val;

public class Integer extends Val{
	
	private int val;
	
	public Integer(int v) {
		val = v;
	}
	
	public int get_val() {
		return val;
	}

	@Override
	public java.lang.String get_html() {
		return java.lang.String.format("\"%d\"", val);
	}

}
