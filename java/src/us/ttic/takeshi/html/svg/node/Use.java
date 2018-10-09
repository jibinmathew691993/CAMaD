package us.ttic.takeshi.html.svg.node;

import us.ttic.takeshi.html.svg.node.att.Attribute;
import us.ttic.takeshi.html.svg.node.att.val.Str;

public class Use extends Leaf {

	public static String TAG = "use";
	
	public static String XLINK = "xlink:href";
	
	public Use(String id) {
		this.add_att(new Attribute<Str>(XLINK, new Str("#"+id)));
	}

	@Override
	public String get_tag() {
		return TAG;
	}


}
