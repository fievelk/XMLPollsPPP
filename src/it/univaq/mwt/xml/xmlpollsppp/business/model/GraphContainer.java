package it.univaq.mwt.xml.xmlpollsppp.business.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class GraphContainer {

	private String SVGcode;
	private Map<Option, String> legendMap = new LinkedHashMap<Option, String>();
	private Question question; 
	
	public GraphContainer() {
		super();
	}

	public GraphContainer(String sVGcode, Map<Option, String> legendMap) {
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
	
	public Map<Option, String> getLegendMap() {
		return legendMap;
	}
	
	public void setLegendMap(Map<Option, String> legendMap) {
		this.legendMap = legendMap;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	
	
}
