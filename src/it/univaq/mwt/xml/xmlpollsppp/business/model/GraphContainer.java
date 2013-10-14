package it.univaq.mwt.xml.xmlpollsppp.business.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class GraphContainer {

	private String SVGcode;
	private Map<String, String> legendMap = new LinkedHashMap<String, String>();
	
	public GraphContainer(String sVGcode, Map<String, String> legendMap) {
		super();
		SVGcode = sVGcode;
		this.legendMap = legendMap;
	}

	public String getSVGcode() {
		return SVGcode;
	}
	
	public void setSVGcode(String sVGcode) {
		SVGcode = sVGcode;
	}
	
	public Map<String, String> getLegendMap() {
		return legendMap;
	}
	
	public void setLegendMap(Map<String, String> legendMap) {
		this.legendMap = legendMap;
	}

}
