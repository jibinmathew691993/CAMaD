package us.ttic.takeshi.html.svg.node;

import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Integer;

public class Line extends Node {
	
	//static
	static final String TAG = "line";
	
	//attribute
	static final String X1 = "x1";
	static final String Y1 = "y1";
	static final String X2 = "x2";
	static final String Y2 = "y2";
	public static final String STYLE = "style";

	public Line(Line n) {
		super(n);
	}
	
	public Line(int x1, int y1, int x2, int y2) {
		attributes.add(new Attribute<Integer>(X1, new Integer(x1)));
		attributes.add(new Attribute<Integer>(Y1, new Integer(y1)));
		attributes.add(new Attribute<Integer>(X2, new Integer(x2)));
		attributes.add(new Attribute<Integer>(Y2, new Integer(y2)));
	}

	@Override
	public String get_tag() {
		return TAG;
	}

}
