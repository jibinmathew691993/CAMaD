package us.ttic.takeshi.html.svg.node;


public class Div extends Leaf {
	
	
	//static
	public static final String TAG = "div";
	public static final String XMLNS = "xmlns";
	public static final String STYLE = "style";
	
	public Div(String text) {
		this.txt = text;
	}
	
	@Override
	public String get_tag() {
		return TAG;
	}
	

}
