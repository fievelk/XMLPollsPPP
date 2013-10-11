package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class SVGGenerator {
	
	public static String generateSVG() {
		
		// by choosing the namespace URI and the local name of the root element of SVG, we are creating an SVG document. //
		
		// We are using a constant available on the SVGDOMImplementation,
		// but we could have used "http://www.w3.org/2000/svg".
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		
		
		Document doc = impl.createDocument(svgNS, "svg", null);
		
		// Get the root element (the 'svg' element).
		Element svgRoot = doc.getDocumentElement();

		// Set the width and height attributes on the root 'svg' element.
		svgRoot.setAttributeNS(null, "width", "400");
		svgRoot.setAttributeNS(null, "height", "450");
//		svgRoot.setAttributeNS(null, "xmlns", "http://www.w3.org/2000/svg");
		
		// Creo un cerchio
		Element circle = doc.createElementNS(svgNS, "circle");
		circle.setAttributeNS(null, "cx", "100");
		circle.setAttributeNS(null, "cy", "100");
		circle.setAttributeNS(null, "r", "100");
		circle.setAttributeNS(null, "fill", "green");
		
		svgRoot.appendChild(circle);
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try {
        	OutputFormat format = new OutputFormat(doc);
            format.setIndenting(true);
//            XMLSerializer serializer = new XMLSerializer(System.out, format);
        	XMLSerializer serializer = new XMLSerializer(new OutputStreamWriter(byteArrayOutputStream, "UTF-8"), format);
			serializer.serialize(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Rimuove la dichiarazione xml dall'output, per non farla comparire nella pagina html in cui il codice verrà incluso
        String regexXmlDeclaration="<\\?xml(.*?)\\?>";
        String result = byteArrayOutputStream.toString().replaceFirst(regexXmlDeclaration, "");

        return result;
	}

}
