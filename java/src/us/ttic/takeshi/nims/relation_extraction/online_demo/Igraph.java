/**
 * 
 */
package us.ttic.takeshi.nims.relation_extraction.online_demo;

//import org.json.simple.JSONObject;

import us.ttic.takeshi.html.svg.node.Chart;

/**
 * @author ikumu
 *
 */
public interface Igraph {
	//public JSONObject get_json();
	
	public Chart get_svg(int width);
}
