package us.ttic.takeshi.html.svg.node;

import java.util.ArrayList;
import java.util.TreeSet;

import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Integer;

public class Rectangle extends Node {
	
	//attribute
	static public final String TAG = "rect";
	static public final String X = "x";
	static public final String Y = "y";
	static public final String WIDTH = "width";
	static public final String HEIGHT= "height";
	static public final String FILL = "fill";
	static public final String STROKE = "stroke";
	static public final String STYLE = "style";
	static public final String RX = "rx";
	static public final String RY = "ry";
	
	public Rectangle(Rectangle rect) {
		attributes = new TreeSet<>(rect.attributes);
		children   = new ArrayList<>(rect.children);
	}
	public Rectangle(int x, int y, int width, int height) {
		this(x,y,width,height,0.0);
	}
	
	public Rectangle(int x, int y, int width, int height, double rounding) {
		attributes.add(new Attribute<Integer>(X, new Integer(x)));
		attributes.add(new Attribute<Integer>(Y, new Integer(y)));
		attributes.add(new Attribute<Integer>(WIDTH, new Integer(width)));
		attributes.add(new Attribute<Integer>(HEIGHT, new Integer(height)));
		
		int r = (int)(Math.min(width, height)*rounding);
		attributes.add(new Attribute<Integer>(RX, new Integer(r)));
		attributes.add(new Attribute<Integer>(RY, new Integer(r)));
		
	}

	@Override
	public String get_tag() {
		return TAG;
	}

}
