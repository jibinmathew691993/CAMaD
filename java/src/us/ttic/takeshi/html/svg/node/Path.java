package us.ttic.takeshi.html.svg.node;

import java.util.ArrayList;
import java.util.TreeSet;

import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Str;

public class Path extends Node {

	//tag
	public static final String TAG = "path";
	
	//att
	public static final String D = "d";
	public static final String STROKE="stroke";
	public static final String STROKE_WIDTH = "stroke-width";
	
	
	//temp
	private static final String LINE_TEMP = "M%d %d L%d %d";
	
	public Path(int x1, int y1, int x2, int y2){
		add_att(new Attribute<Str>(D, new Str(String.format(LINE_TEMP, x1,y1,x2,y2))));
	}
	public Path(String d_path){
		add_att(new Attribute<Str>(D, new Str(d_path)));
	}
	public Path(Path n){
		attributes = new TreeSet<>(n.attributes);
		children   = new ArrayList<>(n.children);
	}
	
	@Override
	public String get_tag() {
		return TAG;
	}

}
