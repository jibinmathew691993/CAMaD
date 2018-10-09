package us.ttic.takeshi.html.svg.node;

import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Integer;
import us.ttic.takeshi.html.svg.node.att.val.Str;
import us.ttic.takeshi.html.svg.style.Style_Class;

public class TextLine extends Node {
	
	//static tag
	public static final String TAG = "g";
	
	//style
	public static final String LINK = "link";
	public static final String DEFLINE = "defline";
	public static final String POPLINE = "popline";
	public static final String SNT = "snt";
	
	protected static final Style_Class DEF_POPLINE_STYLE = new Style_Class(String.format(".%s .%s", LINK, POPLINE)) {{
		add_style("visibility", "hidden");
	}};
	protected static final Style_Class POP_POPLINE_STYLE = new Style_Class(String.format(".%s:hover .%s", LINK, POPLINE)) {{
		add_style("visibility", "visible");
	}};
	protected static final Style_Class DEF_SNT_STYLE = new Style_Class(String.format(".%s .%s", LINK, SNT)) {{
		add_style("visibility", "hidden");
	}};
	protected static final Style_Class POP_SNT_STYLE = new Style_Class(String.format(".%s:hover .%s", LINK, SNT)) {{
		add_style("visibility", "visible");
	}};
	public static final Style_Class[] DEF_STYLES = new Style_Class[]{
			DEF_POPLINE_STYLE, POP_POPLINE_STYLE, DEF_SNT_STYLE, POP_SNT_STYLE 
	};
	
	//attributes
	protected int y1;
	protected int y2;
	protected int width;
	
	public Path def_line;
	public Path pop_line;
	public ForeignObject box;
	
	//def val
	public static final String XMLNS_VAL = "http://www.w3.org/1999/xhtml";
	public static final String STYLE_VAL 
	= "overflow-y:auto; background-color: rgb(50, 67, 68); color: #fff; text-align: center; padding: 5px 0; border-radius: 6px;";

	public TextLine(){
		
	}
	
	public TextLine(int x1, int y1, int x2, int y2, Div text_in_box, int width) {
		this.y1 = y1;
		this.y2 = y2;
		this.width = width;

		this.add_att(new Attribute<Str>(Node.CLASS, new Str(LINK)));
		
		//line
		def_line = new Path(x1, y1, x2, y2);
		def_line.add_att(new Attribute<Str>(Node.CLASS, new Str(DEFLINE)));
		pop_line = new Path(x1, y1, x2, y2);
		pop_line.add_att(new Attribute<Str>(Node.CLASS, new Str(POPLINE)));
		add_child(def_line);
		add_child(pop_line);
		
		text_in_box.add_att(new Attribute<Str>(Node.CLASS, new Str(SNT)));
		text_in_box.add_att(new Attribute<Str>(Div.XMLNS, new Str(XMLNS_VAL)));
		box = new ForeignObject(text_in_box);
		add_child(box);
	}

	
	@Override
	public String get_tag() {
		return TAG;
	}
	
	public class ForeignObject extends Node {
		
		public static final String TAG = "foreignObject";

		//attribute names
		public static final String X = "x";
		public static final String Y = "y";
		public static final String WIDTH = "width";
		public static final String HEIGHT = "height";
		public static final String CLASS = "class"; 
		
		
		public ForeignObject(Div text) {
			attributes.add(new Attribute<Integer>(X, new Integer(0)));
			attributes.add(new Attribute<Integer>(Y, new Integer((y1+y2)/2)));
			attributes.add(new Attribute<Integer>(WIDTH, new Integer(width)));
			attributes.add(new Attribute<Integer>(HEIGHT, new Integer(70)));
			
			add_child(text);
		}

		@Override
		public String get_tag() {
			return TAG;
		}
			
	}
	


}
