package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.model.GraphContainer;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Option;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Poll;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Question;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class SVGGenerator {
	private static Integer centerX = 0;
	private static Integer centerY = 0;
	private static Integer radius = 0;
	private static DOMImplementation impl;
	private static Document doc;
	// Scegliendo il namespace e il local name del root element di SVG, creiamo un documento SVG.
	// Usiamo una costante disponibile in SVGDOMImplementation, ma avremmo potuto usare "http://www.w3.org/2000/svg".
	private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

	
	public static List<GraphContainer> generateSVG(Poll poll) {
		List<GraphContainer> graphContainerList = new ArrayList<GraphContainer>();
		for (Question question : poll.getQuestions()) {
			List<Option> options = question.getOptions();
				GraphContainer graphContainer = generateQuestionStatsSVG(options);
				if (graphContainer != null) {
					graphContainerList.add(graphContainer);	
				}
			}
		return graphContainerList;
	}
	
	
	public static GraphContainer generateQuestionStatsSVG(List<Option> options) {
		GraphContainer graphContainer = new GraphContainer();
		// Genero una Map per inserire le chiavi (non solo codici risposta) e i rispettivi colori
		Map<Option, String> legendMap = new LinkedHashMap<Option, String>();
		
		Element svgRoot = generateSVGRoot();
		Element circle = generateCircle(doc, svgNS);
		svgRoot.appendChild(circle);

		// Prendo i dati relativi a ogni domanda (quante persone hanno dato una determinata risposta).
		// Uso dei BigDecimal: https://blogs.oracle.com/CoreJavaTechTips/entry/the_need_for_bigdecimal
		// Ottengo la somma dei valori totali delle risposte.
		BigDecimal optionCountTotal = BigDecimal.ZERO;
		for (Option option : options) {
			optionCountTotal = optionCountTotal.add(option.getCount());
		}
		// Se nessuno ha mai fornito neanche una risposta alla domanda, il grafico non viene generato
		if (optionCountTotal.intValue() == 0) {
			return null;
		}
		
		generateSlices(options, optionCountTotal, svgRoot, legendMap, graphContainer);
			
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    
	    try {
	    	OutputFormat format = new OutputFormat(doc);
	        format.setIndenting(true);
	    	XMLSerializer serializer = new XMLSerializer(new OutputStreamWriter(byteArrayOutputStream, "UTF-8"), format);
			serializer.serialize(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    // Rimuove la dichiarazione xml dall'output, per non farla comparire nella pagina html in cui il codice SVG verrà incluso
	    String regexXmlDeclaration="<\\?xml(.*?)\\?>";
	    String SVGresult = byteArrayOutputStream.toString().replaceFirst(regexXmlDeclaration, "");
	    
//	    System.out.println(SVGresult);
	    
	    graphContainer.setLegendMap(legendMap);
	    graphContainer.setSVGcode(SVGresult);
	    return graphContainer;
	}
	
	
	public static List<GraphContainer> generateNonReqSVG(Poll poll) {
		List<GraphContainer> graphContainerList = new ArrayList<GraphContainer>();
		GraphContainer graphContainer = generateNonRequiredQuestionsStatsSVG(poll);
		if (graphContainer != null) {
			graphContainerList.add(graphContainer);	
		}
		return graphContainerList;
	}
	
	
	public static GraphContainer<Option, String> generateNonRequiredQuestionsStatsSVG(Poll poll) {
		GraphContainer<Option, String> graphContainer = new GraphContainer<Option, String>();
		// Genero una Map per inserire le chiavi (non solo codici risposta) e i rispettivi colori
		Map<Option, String> legendMap = new LinkedHashMap<Option, String>();
		
	    // Tratto gli slice come se fossero delle Option, per semplificare.
	    List<Option> slices = new ArrayList<Option>();
		
		Element svgRoot = generateSVGRoot();
		Element circle = generateCircle(doc, svgNS);
		svgRoot.appendChild(circle);
		
		BigDecimal pollSubmissions = poll.getPollSubmissions();
		// Se nessuno ha mai fornito neanche una risposta al questionario, il grafico non viene generato
		if (pollSubmissions.intValue() == 0) {
			return null;
		}
		
	    // conto il numero di sondaggi submitted che hanno almeno una risposta fornita a una question non required
	    BigDecimal submissionsWithNonReqAnswer = poll.getSubmissionsWithNonReqAnswer();
	    BigDecimal submissionsWithOnlyReqAnswer = pollSubmissions.subtract(submissionsWithNonReqAnswer);
	    
	    // Creo le slice trattandole come se fossero delle Option e le inserisco in una lista (sulla quale avrà luogo l'iterazione per la creazione dei path)
	    Option sliceNonReq = new Option("Sondaggi inoltrati con risposte opzionali", submissionsWithNonReqAnswer);
	    Option sliceOnlyReq = new Option("Sondaggi inoltrati senza fonire alcuna risposta opzionale", submissionsWithOnlyReqAnswer);
	    slices.add(sliceOnlyReq);
	    slices.add(sliceNonReq);

		generateSlices(slices, pollSubmissions, svgRoot, legendMap, graphContainer);
			
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    
	    try {
	    	OutputFormat format = new OutputFormat(doc);
	        format.setIndenting(true);
	    	XMLSerializer serializer = new XMLSerializer(new OutputStreamWriter(byteArrayOutputStream, "UTF-8"), format);
			serializer.serialize(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    // Rimuove la dichiarazione xml dall'output, per non farla comparire nella pagina html in cui il codice verrà incluso
	    String regexXmlDeclaration="<\\?xml(.*?)\\?>";
	    String SVGresult = byteArrayOutputStream.toString().replaceFirst(regexXmlDeclaration, "");
	    
//	    System.out.println(SVGresult);
	    
	    graphContainer.setLegendMap(legendMap);
	    graphContainer.setSVGcode(SVGresult);
		return graphContainer;
	}
	
	
	private static Element generateSVGRoot(){
		impl = SVGDOMImplementation.getDOMImplementation();
		doc = impl.createDocument(svgNS, "svg", null);
		// Prendo il root element ('svg' element).
		Element svgRoot = doc.getDocumentElement();
		svgRoot.setAttributeNS(null, "width", "450");
		svgRoot.setAttributeNS(null, "height", "240");
		return svgRoot;
	}

	
	private static Element generateCircle(Document doc, String svgNS){
		centerX = 150;
		centerY = 120;
		radius = 100;
		Element circle = doc.createElementNS(svgNS, "circle");
		circle.setAttributeNS(null, "cx", centerX.toString());
		circle.setAttributeNS(null, "cy", centerY.toString());
		circle.setAttributeNS(null, "r", radius.toString());
		circle.setAttributeNS(null, "fill-opacity", "0");
		return circle;
	}
	
	
	private static String generateRGBColor() {
		Random rand = new Random();
		Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
		int r = randomColor.getRed();
		int g = randomColor.getGreen();
		int b = randomColor.getBlue();
		String rgbColor = "rgb("+r+","+g+","+b+")";
		return rgbColor;
	}
	
	
	private static void generateSlices(List<Option> slices, BigDecimal totalAmount, Element svgRoot, Map legendMap, GraphContainer graphContainer) {
		// Devo trasformare i valori delle slice (le opzioni del grafico) in angoli rispetto ai 360 gradi del cerchio.
		BigDecimal roundAngle = new BigDecimal("360");
		BigDecimal oneHundred = new BigDecimal("100");
		
		for (Option slice: slices){
			BigDecimal oldValue = slice.getCount();
			BigDecimal newValue = (oldValue.multiply(roundAngle)).divide(totalAmount, 1, RoundingMode.HALF_UP);
			slice.setAngleValue(newValue);
			slice.setPercentValue((oneHundred.multiply(oldValue)).divide(totalAmount, 1, RoundingMode.HALF_UP));
		}

		// Converto ogni angolo da gradi in radianti
		Element path = doc.createElementNS(svgNS, "path");
		BigDecimal greekPI = new BigDecimal(Math.PI);
		BigDecimal flatAngle = new BigDecimal("180");
		
		BigDecimal startAngle = BigDecimal.ZERO;
		BigDecimal endAngle = BigDecimal.ZERO;
		
		for (Option slice : slices){
			BigDecimal oldAngleValue = slice.getAngleValue();
			
			startAngle = endAngle;
			endAngle = startAngle.add(oldAngleValue);
			
			double x1 = centerX + radius*Math.cos(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			double y1 = centerY + radius*Math.sin(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			double x2 = centerX + radius*Math.cos(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			double y2 = centerY + radius*Math.sin(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			
			x1 = Math.round(x1*100)/100;
			y1 = Math.round(y1*100)/100;
			x2 = Math.round(x2*100)/100;
			y2 = Math.round(y2*100)/100;
			
			String rgbColor = generateRGBColor();
			
			path = doc.createElementNS(svgNS, "path");
			String pathString = "M"+centerX+","+centerY + " L"+x1+","+y1 + " A100,100 0 " + ((endAngle.doubleValue() - startAngle.doubleValue() > 180) ? 1 : 0)+ ",1 " + x2+","+(y2-0.1)+ " z";
			
			path.setAttributeNS(null, "d", pathString);
			path.setAttributeNS(null, "fill", rgbColor);
			svgRoot.appendChild(path);
			
			// Inserisco la option dello spicchio e il suo colore nella legendMap
			legendMap.put(slice, rgbColor);
			
			if (slice.getQuestion() != null) {
				graphContainer.setQuestion(slice.getQuestion());						
			}
		}
	}
}
