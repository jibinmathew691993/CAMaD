package us.ttic.takeshi.html.svg.node;

import java.util.ArrayList;
import java.util.TreeSet;

import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Integer;
import us.ttic.takeshi.html.svg.node.att.val.Str;

public class TextBox extends Node {
	
	//static
	public static final String TAG = "g";
	//attribute names
	static final String X = "x";
	static final String Y = "y";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";

	//element in group
	public int x;
	public int y;
	public int w;
	public int h;
	public Rectangle rect;
	
	//def val
	public static int MARGINE=10;
	public static final String XMLNS_VAL = "http://www.w3.org/1999/xhtml";
	private static final String STYLE_VAL = "overflow-y:auto";
	
	public TextBox(TextBox box) {
		this.x = box.x;
		this.y = box.y;
		this.w = box.w;
		this.h = box.h;
		this.rect = new Rectangle(box.rect);
		
		attributes = new TreeSet<>(box.attributes);
		children   = new ArrayList<>(box.children);
	}
	   
	public TextBox(int x_pos, int y_pos, int width, int height, double rounding, String text_in_box) {
		x=x_pos;
		y=y_pos;
		w=width;
		h=height;
		
		rect = new Rectangle(x, y, w, h, rounding);
		Div txt = new Div(text_in_box);
		txt.add_att(new Attribute<Str>(Div.XMLNS, new Str(XMLNS_VAL)));
		txt.add_att(new Attribute<Str>(Div.STYLE, new Str(STYLE_VAL)));
		ForeignObject box = new ForeignObject(txt);
		add_child(rect);
		add_child(box);
	}
	public TextBox(int x, int y, int width, int height, String text_in_box) {
		this(x,y,width,height,0.0, text_in_box);
	}

	public TextBox(int x_pos, int y_pos, int width, int height, double rounding, Div text_in_box) {
		x=x_pos;
		y=y_pos;
		w=width;
		h=height;
		
		rect = new Rectangle(x, y, w, h, rounding);
		ForeignObject box = new ForeignObject(text_in_box);
		add_child(rect);
		add_child(box);
	}

	@Override
	public String get_tag() {
		return TAG;
	}
	
	
	protected class ForeignObject extends Node {
		
		public static final String TAG = "foreignObject";

		//attribute names
		static final String X = "x";
		static final String Y = "y";
		public static final String WIDTH = "width";
		public static final String HEIGHT = "height";
		
		public ForeignObject(Div text) {
			attributes.add(new Attribute<Integer>(X, new Integer(x+MARGINE)));
			attributes.add(new Attribute<Integer>(Y, new Integer(y+MARGINE)));
			attributes.add(new Attribute<Integer>(WIDTH, new Integer(w-2*MARGINE)));
			attributes.add(new Attribute<Integer>(HEIGHT, new Integer(h-2*MARGINE)));
			
			add_child(text);
		}

		@Override
		public String get_tag() {
			return TAG;
		}
			
	}
	
	
	
	//debug script
	public static void main(String[] args) { 
		TextBox box = new TextBox(5, 10, 100, 200, "this is dummy sentence. Do you like it?");
		box.rect.add_att(new Attribute<Str>(Rectangle.STYLE, new Str("fill:red;stroke:black;stroke-width:5;opacity:0.5")));
		System.out.println(box.get_html());
	}
}

