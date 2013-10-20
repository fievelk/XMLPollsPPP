package it.univaq.mwt.xml.xmlpollsppp.business.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class GraphContainer<K, V> {

	private String SVGcode;
	private Map<K,V> legendMap = new LinkedHashMap<K, V>();
	private Question question; 
	
	public GraphContainer() {
		super();
	}

	public GraphContainer(String sVGcode, Map<K, V> legendMap) {
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
	
	public Map<K, V> getLegendMap() {
		return legendMap;
	}
	
	public void setLegendMap(Map<K, V> legendMap) {
		this.legendMap = legendMap;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	
	
}
