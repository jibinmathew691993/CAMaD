package us.ttic.takeshi.html.svg.node;

import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Str;
import us.ttic.takeshi.html.svg.style.Style_Class;

public class Linked_Rects extends TextLine {
	
	
	public static final String DEF_RECT = "def_rect";
	public static final String POP_RECT = "pop_rect";
	protected static final Style_Class DEF_DEFRECT_STYLE = new Style_Class(String.format(".%s .%s", LINK, DEF_RECT)) {{
		add_style("visibility", "visible");
	}};
	protected static final Style_Class DEF_POPRECT_STYLE = new Style_Class(String.format(".%s .%s", LINK, POP_RECT)) {{
		add_style("visibility", "hidden");
	}};
	protected static final Style_Class POP_DEFRECT_STYLE = new Style_Class(String.format(".%s:hover .%s", LINK, DEF_RECT)) {{
		add_style("visibility", "hidden");
	}};
	protected static final Style_Class POP_POPRECT_STYLE = new Style_Class(String.format(".%s:hover .%s", LINK, POP_RECT)) {{
		add_style("visibility", "visible");
	}};
	public static final Style_Class[] DEF_STYLES = new Style_Class[]{
			DEF_POPLINE_STYLE, POP_POPLINE_STYLE, DEF_SNT_STYLE, POP_SNT_STYLE
			, DEF_DEFRECT_STYLE, DEF_POPRECT_STYLE, POP_DEFRECT_STYLE, POP_POPRECT_STYLE 
	};
	
	private int x1;
	private int x2;

	public Linked_Rects(TextBox b1, TextBox b2, Div text_in_box, int width) {
		if(b1.x<b2.x) {
			this.y1 = b1.y + b1.h/2;
			this.y2 = b2.y + b2.h/2;
			this.x1 = b1.x + b1.w - b1.w/5;
			this.x2 = b2.x + b2.w/5;
		}else {
			this.y1 = b2.y + b2.h/2;
			this.y2 = b1.y + b1.h/2;
			this.x1 = b2.x + b2.w - b2.w/5;
			this.x2 = b1.x + b1.w/5;
		}
		this.width = width;

		this.add_att(new Attribute<Str>(Node.CLASS, new Str(LINK)));
		
		//line
		def_line = new Path(x1, y1, x2, y2);
		def_line.add_att(new Attribute<Str>(Node.CLASS, new Str(DEFLINE)));
		pop_line = new Path(x1, y1, x2, y2);
		pop_line.add_att(new Attribute<Str>(Node.CLASS, new Str(POPLINE)));
		add_child(def_line);
		add_child(pop_line);
		
		//rects
		TextBox def1 = new TextBox(b1);
		def1.attributes.add(new Attribute<Str>(Node.CLASS, new Str(DEF_RECT)));
		TextBox def2 = new TextBox(b2);
		def2.attributes.add(new Attribute<Str>(Node.CLASS, new Str(DEF_RECT)));
		
		TextBox pop1 = new TextBox(b1);
		pop1.attributes.add(new Attribute<Str>(Node.CLASS, new Str(POP_RECT)));
		TextBox pop2 = new TextBox(b2);
		pop2.attributes.add(new Attribute<Str>(Node.CLASS, new Str(POP_RECT)));
		
		add_child(def1);
		add_child(def2);
		add_child(pop1);
		add_child(pop2);
		
		text_in_box.add_att(new Attribute<Str>(Node.CLASS, new Str(SNT)));
		text_in_box.add_att(new Attribute<Str>(Div.XMLNS, new Str(XMLNS_VAL)));
		box = new ForeignObject(text_in_box);
		add_child(box);
	}

}
