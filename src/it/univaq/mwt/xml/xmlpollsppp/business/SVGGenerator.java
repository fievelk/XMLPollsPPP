package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.model.GraphContainer;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Option;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Question;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class SVGGenerator {
	
	public static List<GraphContainer> generateSVG(List<Question> questions) {
		List<GraphContainer> graphContainerList = new ArrayList<GraphContainer>();
		
		for (Question question : questions) {
			List<Option> options = question.getOptions();
				GraphContainer graphContainer = generateSingleSVG(options);
				graphContainerList.add(graphContainer);	
			}
		return graphContainerList;
	}
	
	public static GraphContainer generateSingleSVG(List<Option> options) {
		GraphContainer graphContainer = new GraphContainer();
		// Genero una Map per inserire le chiavi (non solo codici risposta) e i rispettivi colori
		Map<Option, String> legendMap = new LinkedHashMap<Option, String>();
		
		// by choosing the namespace URI and the local name of the root element of SVG, we are creating an SVG document. //
		
		// We are using a constant available on the SVGDOMImplementation,
		// but we could have used "http://www.w3.org/2000/svg".
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		Document doc = impl.createDocument(svgNS, "svg", null);
		
		// Prendo il root element ('svg' element).
		Element svgRoot = doc.getDocumentElement();

		// Imposto width e height sull elemento svg.
		svgRoot.setAttributeNS(null, "width", "450");
		svgRoot.setAttributeNS(null, "height", "230");
		
		// Creo un cerchio
		Integer centerX = 150;
		Integer centerY = 100;
		Integer radius = 100;
		Element circle = doc.createElementNS(svgNS, "circle");
		circle.setAttributeNS(null, "cx", centerX.toString());
		circle.setAttributeNS(null, "cy", centerY.toString());
		circle.setAttributeNS(null, "r", radius.toString());
		circle.setAttributeNS(null, "fill-opacity", "0");
		svgRoot.appendChild(circle);

		// Prendo i dati relativi a ogni domanda (quante persone hanno dato una determinata risposta)
		// Uso dei BigDecimal: https://blogs.oracle.com/CoreJavaTechTips/entry/the_need_for_bigdecimal
		// Ottengo la somma dei valori totali delle risposte (numero di opzioni selezionate da ognuno)
		BigDecimal optionCountTotal = new BigDecimal("0");
		
		for (Option option : options) {
			optionCountTotal = optionCountTotal.add(option.getCount());
		}
		
		// Se nessuno ha mai fornito neanche una risposta alla domanda, il grafico non viene generato
		if (optionCountTotal.intValue() == 0) {
			return null;
		}
		
		// Ora devo trasformare questi valori in angoli rispetto ai 360 gradi del cerchio.
		BigDecimal roundAngle = new BigDecimal("360");
		BigDecimal oneHundred = new BigDecimal("100");
		for (Option option : options){
			BigDecimal oldValue = option.getCount();
//			System.out.print("Old value = "+oldValue);
			BigDecimal newValue = (oldValue.multiply(roundAngle)).divide(optionCountTotal, 1, RoundingMode.HALF_UP);
//			System.out.print(" --> ");
//			System.out.println("New value = "+newValue);
//			option.setCount(newValue);
			option.setAngleValue(newValue);
			
			option.setPercentValue((oneHundred.multiply(oldValue)).divide(optionCountTotal, 1, RoundingMode.HALF_UP));
		}

		// Converto ogni angolo in radiante creando una nuova LinkedHashMap su misura
		// (Copiando le key della map answersNumbers)
		Element path = doc.createElementNS(svgNS, "path");
		BigDecimal greekPI = new BigDecimal(Math.PI);
		BigDecimal flatAngle = new BigDecimal("180");
		
		BigDecimal startAngle = new BigDecimal("0");
		BigDecimal endAngle = new BigDecimal("0");
		
		for (Option option : options){
			BigDecimal oldAngleValue = option.getAngleValue();
			
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
			
			// Genero un colore random
			Random rand = new Random();
			Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
			int r = randomColor.getRed();
			int g = randomColor.getGreen();
			int b = randomColor.getBlue();
			
			String rgbColor = "rgb("+r+","+g+","+b+")";
			
	//		System.out.println("COLOR = " + randomColor);
			
			path = doc.createElementNS(svgNS, "path");
			String pathString = "M"+centerX+","+centerY + " L"+x1+","+y1 + " A100,100 0 " + ((endAngle.doubleValue() - startAngle.doubleValue() > 180) ? 1 : 0)+ ",1 " + x2+","+(y2-0.1)+ " z";
			
			path.setAttributeNS(null, "d", pathString);
			path.setAttributeNS(null, "fill", rgbColor);
			svgRoot.appendChild(path);
			
			// Inserisco la option dello spicchio e il suo colore nella legendMap
			legendMap.put(option, rgbColor);
			
			graphContainer.setQuestion(option.getQuestion());
		}
			
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
	    
	    System.out.println(SVGresult);
	    
	    graphContainer.setLegendMap(legendMap);
	    graphContainer.setSVGcode(SVGresult);
//	    GraphContainer graphContainer = new GraphContainer(SVGresult, legendMap);
	    return graphContainer;
	
	}
}
