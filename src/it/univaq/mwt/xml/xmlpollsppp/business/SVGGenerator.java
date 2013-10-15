package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.model.GraphContainer;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Option;

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
	
	public static List<GraphContainer> generateSVG(List<TreeMap> answersNumbersList) {
		List<GraphContainer> graphContainerList = new ArrayList<GraphContainer>();
		
		for (TreeMap<Option, BigDecimal> answersNumbers : answersNumbersList) {
			GraphContainer graphContainer = generateSingleSVG(answersNumbers);
			graphContainerList.add(graphContainer);
		}
		
		return graphContainerList;
		
		
	}
	public static GraphContainer generateSingleSVG(TreeMap<Option, BigDecimal> answersNumbers) {
		
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
//	circle.setAttributeNS(null, "fill", "lightgrey");
	circle.setAttributeNS(null, "fill-opacity", "0");
	svgRoot.appendChild(circle);

	// Prendo i dati relativi a ogni domanda (quante persone hanno dato una determinata risposta)
	
	// Si usa Integer al posto di int perché i tipi primitivi non possono essere usati come generic arguments
//	Map<String, BigDecimal> answersNumbers = new LinkedHashMap<String, BigDecimal>();
	
	// Questi valori vanno presi tramite query sul db, quindi qui avrò solo la map answerNumbers
//	answersNumbers.put("T1Q1_1", BigDecimal.valueOf(113));
//	answersNumbers.put("T1Q1_2", BigDecimal.valueOf(100));
//	answersNumbers.put("T1Q1_3", BigDecimal.valueOf(50));
//	answersNumbers.put("T1Q1_4", BigDecimal.valueOf(28));
//	answersNumbers.put("T1Q1_5", BigDecimal.valueOf(27));
	
	// Uso dei BigDecimal: https://blogs.oracle.com/CoreJavaTechTips/entry/the_need_for_bigdecimal
	
	// Ottengo la somma dei valori totali delle risposte (numero di opzioni selezionate da ognuno)
	BigDecimal answersValuesSum = new BigDecimal("0");
	Iterator answersValuesIte = answersNumbers.values().iterator();
	while (answersValuesIte.hasNext()) {
		answersValuesSum = answersValuesSum.add((BigDecimal)answersValuesIte.next());
	}
//	System.out.println("answersValuesSum = "+answersValuesSum);
	
	// Se nessuno ha mai fornito neanche una risposta alla domanda, il grafico non viene generato
	if (answersValuesSum.intValue() == 0) {
		return null;
	}
	
	// Ora devo trasformare questi valori in angoli rispetto ai 360 gradi del cerchio.
	BigDecimal roundAngle = new BigDecimal("360");
	for (Option key : answersNumbers.keySet()){
		BigDecimal oldValue = answersNumbers.get(key);
//		BigDecimal oldValue = new BigDecimal(answersNumbers.get(key));
//		System.out.print("Old value = "+oldValue);
		BigDecimal newValue = (oldValue.multiply(roundAngle)).divide(answersValuesSum, 1, RoundingMode.HALF_UP);
//		System.out.print(" --> ");
//		System.out.println("New value = "+newValue);
		answersNumbers.put(key, newValue);
	}

	// Converto ogni angolo in radiante creando una nuova LinkedHashMap su misura
	// (Copiando le key della map answersNumbers)
	Element path = doc.createElementNS(svgNS, "path");
	Map<String, BigDecimal> answersNumbersRadiants = new LinkedHashMap<String, BigDecimal>();
	BigDecimal greekPI = new BigDecimal(Math.PI);
	BigDecimal flatAngle = new BigDecimal("180");
	
//	BigDecimal startAngleRadiantValue = new BigDecimal("0");
//	BigDecimal endAngleRadiantValue = new BigDecimal("0");
	
	BigDecimal startAngle = new BigDecimal("0");
	BigDecimal endAngle = new BigDecimal("0");
	
	for (Option key : answersNumbers.keySet()){
		BigDecimal oldValue = answersNumbers.get(key);
		
		startAngle = endAngle;
		endAngle = startAngle.add(oldValue);
		
		double x1 = centerX + radius*Math.cos(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
		double y1 = centerY + radius*Math.sin(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
		double x2 = centerX + radius*Math.cos(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
		double y2 = centerY + radius*Math.sin(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
		
		x1 = Math.round(x1*100)/100;
		y1 = Math.round(y1*100)/100;
		x2 = Math.round(x2*100)/100;
		y2 = Math.round(y2*100)/100;
		
//		BigDecimal x1 = centerX + radius*Math.cos(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
//		BigDecimal y1 = centerY + radius*Math.sin(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
//		BigDecimal x2 = centerX + radius*Math.cos(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
//		BigDecimal y2 = centerY + radius*Math.sin(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
		
//		x1 = parseInt(200 + 180*Math.cos(Math.PI*startAngle/180));
		
/*			BigDecimal thisAngleRadiantValue = (greekPI.multiply(oldValue)).divide(flatAngle, 1, RoundingMode.HALF_UP); // Trigonometria
		answersNumbersRadiants.put(key, thisAngleRadiantValue);
		
		startAngleRadiantValue = endAngleRadiantValue;
		endAngleRadiantValue = startAngleRadiantValue.add(thisAngleRadiantValue); */
	// Calcolo i punti x e y da incontrare sulla circonferenza per tracciare l'arco relativo all'angolo
		
	// Coordinate del punti di partenza dell'arco sulla circonferenza	
//	double x1 = centerX + radius*Math.cos(startAngleRadiantValue.doubleValue());
//	double y1 = centerY + radius*Math.sin(startAngleRadiantValue.doubleValue());
	
	// Coordinate del punti di fine dell'arco sulla circonferenza
//	double x2 = centerX + radius*Math.cos(endAngleRadiantValue.doubleValue());
//	double y2 = centerY + radius*Math.sin(endAngleRadiantValue.doubleValue());
	
	// Genero un colore random
	Random rand = new Random();
	Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
	int r = randomColor.getRed();
	int g = randomColor.getGreen();
	int b = randomColor.getBlue();
	
	String rgbColor = "rgb("+r+","+g+","+b+")";
	
//	System.out.println("COLOR = " + randomColor);
	
	path = doc.createElementNS(svgNS, "path");
//	String pathString = "M"+centerX+","+centerY + " L"+x1+","+y1 + " A100,100 0 0,1 " + x2+","+y2+ " z";
//	String pathString = "M"+centerX+","+centerY + " L"+x1+","+y1 + " A100,100 0 1,1 " + x2+","+y2+ " z";
	String pathString = "M"+centerX+","+centerY + " L"+x1+","+y1 + " A100,100 0 " + ((endAngle.doubleValue() - startAngle.doubleValue() > 180) ? 1 : 0)+ ",1 " + x2+","+(y2-0.1)+ " z";
	
	path.setAttributeNS(null, "d", pathString);
	path.setAttributeNS(null, "fill", rgbColor);
	svgRoot.appendChild(path);
	
	// Inserisco la chiave dello spicchio e il suo colore nella legendMap
	legendMap.put(key, rgbColor);
	
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
    
    GraphContainer graphContainer = new GraphContainer(SVGresult, legendMap);
    return graphContainer;
}
	
//	public static GraphContainer generateSVG(TreeMap<Option, String> answersNumbers) {
/*		public static GraphContainer generateSVG() {
		
		// Genero una Map per inserire le chiavi (non solo codici risposta) e i rispettivi colori
		Map<String, String> legendMap = new LinkedHashMap<String, String>();
		
		// by choosing the namespace URI and the local name of the root element of SVG, we are creating an SVG document. //
		
		// We are using a constant available on the SVGDOMImplementation,
		// but we could have used "http://www.w3.org/2000/svg".
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		Document doc = impl.createDocument(svgNS, "svg", null);
		
		// Prendo il root element ('svg' element).
		Element svgRoot = doc.getDocumentElement();

		// Imposto width e height sull elemento svg.
		svgRoot.setAttributeNS(null, "width", "650");
		svgRoot.setAttributeNS(null, "height", "450");
		
		// Creo un cerchio
		Integer centerX = 150;
		Integer centerY = 130;
		Integer radius = 100;
		Element circle = doc.createElementNS(svgNS, "circle");
		circle.setAttributeNS(null, "cx", centerX.toString());
		circle.setAttributeNS(null, "cy", centerY.toString());
		circle.setAttributeNS(null, "r", radius.toString());
//		circle.setAttributeNS(null, "fill", "lightgrey");
		circle.setAttributeNS(null, "fill-opacity", "0");
		svgRoot.appendChild(circle);

		// Prendo i dati relativi a ogni domanda (quante persone hanno dato una determinata risposta)
		
		// Si usa Integer al posto di int perché i tipi primitivi non possono essere usati come generic arguments
		Map<String, BigDecimal> answersNumbers = new LinkedHashMap<String, BigDecimal>();
		
		// Questi valori vanno presi tramite query sul db, quindi qui avrò solo la map answerNumbers
		answersNumbers.put("T1Q1_1", BigDecimal.valueOf(113));
//		answersNumbers.put("T1Q1_2", BigDecimal.valueOf(100));
//		answersNumbers.put("T1Q1_3", BigDecimal.valueOf(50));
//		answersNumbers.put("T1Q1_4", BigDecimal.valueOf(28));
//		answersNumbers.put("T1Q1_5", BigDecimal.valueOf(27));
		
		// Uso dei BigDecimal: https://blogs.oracle.com/CoreJavaTechTips/entry/the_need_for_bigdecimal
		
		// Ottengo la somma dei valori totali delle risposte (numero di opzioni selezionate da ognuno)
		BigDecimal answersValuesSum = new BigDecimal("0");
		Iterator answersValuesIte = answersNumbers.values().iterator();
		while (answersValuesIte.hasNext()) {
			answersValuesSum = answersValuesSum.add((BigDecimal)answersValuesIte.next());
		}
		System.out.println("answersValuesSum = "+answersValuesSum);
		
		// Ora devo trasformare questi valori in angoli rispetto ai 360 gradi del cerchio.
		BigDecimal roundAngle = new BigDecimal("360");
		for (String key : answersNumbers.keySet()){
			BigDecimal oldValue = answersNumbers.get(key);
//			BigDecimal oldValue = new BigDecimal(answersNumbers.get(key));
			System.out.print("Old value = "+oldValue);
			BigDecimal newValue = (oldValue.multiply(roundAngle)).divide(answersValuesSum, 1, RoundingMode.HALF_UP);
			System.out.print(" --> ");
			System.out.println("New value = "+newValue);
			answersNumbers.put(key, newValue);
		}

		// Converto ogni angolo in radiante creando una nuova LinkedHashMap su misura
		// (Copiando le key della map answersNumbers)
		Element path = doc.createElementNS(svgNS, "path");
		Map<String, BigDecimal> answersNumbersRadiants = new LinkedHashMap<String, BigDecimal>();
		BigDecimal greekPI = new BigDecimal(Math.PI);
		BigDecimal flatAngle = new BigDecimal("180");
		
//		BigDecimal startAngleRadiantValue = new BigDecimal("0");
//		BigDecimal endAngleRadiantValue = new BigDecimal("0");
		
		BigDecimal startAngle = new BigDecimal("0");
		BigDecimal endAngle = new BigDecimal("0");
		
		for (String key : answersNumbers.keySet()){
			BigDecimal oldValue = answersNumbers.get(key);
			
			startAngle = endAngle;
			endAngle = startAngle.add(oldValue);
			
			double x1 = centerX + radius*Math.cos(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			double y1 = centerY + radius*Math.sin(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			double x2 = centerX + radius*Math.cos(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			double y2 = centerY + radius*Math.sin(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			
			x1 = Math.round(x1*100)/100;
			y1 = Math.round(y1*100)/100;
			x2 = Math.round(x2*100)/100;
			y2 = Math.round(y2*100)/100;
			
//			BigDecimal x1 = centerX + radius*Math.cos(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
//			BigDecimal y1 = centerY + radius*Math.sin(greekPI.multiply(startAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
//			BigDecimal x2 = centerX + radius*Math.cos(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
//			BigDecimal y2 = centerY + radius*Math.sin(greekPI.multiply(endAngle.divide(flatAngle, 1, RoundingMode.HALF_UP)).doubleValue());
			
//			x1 = parseInt(200 + 180*Math.cos(Math.PI*startAngle/180));
			
			BigDecimal thisAngleRadiantValue = (greekPI.multiply(oldValue)).divide(flatAngle, 1, RoundingMode.HALF_UP); // Trigonometria
			answersNumbersRadiants.put(key, thisAngleRadiantValue);
			
			startAngleRadiantValue = endAngleRadiantValue;
			endAngleRadiantValue = startAngleRadiantValue.add(thisAngleRadiantValue); 
		// Calcolo i punti x e y da incontrare sulla circonferenza per tracciare l'arco relativo all'angolo
			
		// Coordinate del punti di partenza dell'arco sulla circonferenza	
//		double x1 = centerX + radius*Math.cos(startAngleRadiantValue.doubleValue());
//		double y1 = centerY + radius*Math.sin(startAngleRadiantValue.doubleValue());
		
		// Coordinate del punti di fine dell'arco sulla circonferenza
//		double x2 = centerX + radius*Math.cos(endAngleRadiantValue.doubleValue());
//		double y2 = centerY + radius*Math.sin(endAngleRadiantValue.doubleValue());
		
		// Genero un colore random
		Random rand = new Random();
		Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
		int r = randomColor.getRed();
		int g = randomColor.getGreen();
		int b = randomColor.getBlue();
		
		String rgbColor = "rgb("+r+","+g+","+b+")";
		
		System.out.println("COLOR = " + randomColor);
		
		path = doc.createElementNS(svgNS, "path");
//		String pathString = "M"+centerX+","+centerY + " L"+x1+","+y1 + " A100,100 0 0,1 " + x2+","+y2+ " z";
//		String pathString = "M"+centerX+","+centerY + " L"+x1+","+y1 + " A100,100 0 1,1 " + x2+","+y2+ " z";
		String pathString = "M"+centerX+","+centerY + " L"+x1+","+y1 + " A100,100 0 " + ((endAngle.doubleValue() - startAngle.doubleValue() > 180) ? 1 : 0)+ ",1 " + x2+","+(y2-0.1)+ " z";
		
		path.setAttributeNS(null, "d", pathString);
		path.setAttributeNS(null, "fill", rgbColor);
		svgRoot.appendChild(path);
		
		
		 var d = "M200,200  L" + x1 + "," + y1 + "  A195,195 0 " + 
                ((endAngle-startAngle > 180) ? 1 : 0) + ",1 " + x2 + "," + y2 + " z";
        //alert(d); // enable to see coords as they are displayed
        var c = parseInt(i / sectorAngleArr.length * 360);
        var arc = makeSVG("path", {d: d, fill: "hsl(" + c + ", 66%, 50%)"});
        paper.appendChild(arc);
		 
		
		
		// Inserisco la chiave dello spicchio e il suo colore nella legendMap
		legendMap.put(key, rgbColor);
		
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
        String result = byteArrayOutputStream.toString().replaceFirst(regexXmlDeclaration, "");
        
        System.out.println(result);
        
        GraphContainer graphContainer = new GraphContainer(result, legendMap);
        return graphContainer;
	}*/

}
