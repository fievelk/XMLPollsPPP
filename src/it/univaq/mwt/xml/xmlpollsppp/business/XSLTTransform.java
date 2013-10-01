package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XSLTTransform {

	public static String transformFromString(String xml, String xslt) {
        TransformerFactory tf = TransformerFactory.newInstance();
        String output = null;
        
        try {
        	
			/* Creo una Source a partire dalla String xml 
			 * (convertendola prima in StringReader da passare come parametro a StreamSource) */
			Reader xmlString = new StringReader(xml);
			Source xmlStringSource = new StreamSource(xmlString);
			
			/* Creo un Result da passare al metodo transform del Transformer */
			StringWriter writer = new StringWriter();
			Result result = new StreamResult(writer);
			
			/* Creo un Source da passare al metodo newTransformer del Transformer */
			Reader xsltString = new StringReader(xslt);
			Source xsltStringSource = new StreamSource(xsltString);
			
			/* Imposto l'xslt come StreamSource del Transformer e poi eseguo la trasformazione:
			 * Process the Source into a Transformer Object. The Source is an XSLT document  */
			Transformer t = tf.newTransformer(xsltStringSource);
			t.transform(xmlStringSource, result);
			
			output = writer.toString();
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return output;
    }	
	
    /*public static String transformFromString(String xml, File xslt) {
    TransformerFactory tf = TransformerFactory.newInstance();
    String output = null;
    
    try {
    	
		Transformer t = tf.newTransformer(new StreamSource(xslt));
		Reader xmlString = new StringReader(xml);
		Source xmlStringSource = new StreamSource(xmlString);
//		t.transform(xmlStringSource, new StreamResult(System.out));
		
		StringWriter writer = new StringWriter();
		t.transform(xmlStringSource, new StreamResult(writer));
		output = writer.toString();
		System.out.println(output);
	} catch (TransformerConfigurationException e) {
		e.printStackTrace();
	} catch (TransformerException e) {
		e.printStackTrace();
	}
	return output;
}*/	
}
