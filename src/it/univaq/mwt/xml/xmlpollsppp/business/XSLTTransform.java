package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
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
			
			// Creo un Result da passare al metodo transform del Transformer 
			StringWriter writer = new StringWriter();
			Result result = new StreamResult(writer);
			
//			 Creo un Source da passare al metodo newTransformer del Transformer 
			Reader xsltString = new StringReader(xslt);
			Source xsltStringSource = new StreamSource(xsltString);
			
//			 Imposto l'xslt come StreamSource del Transformer e poi eseguo la trasformazione:
//			 Process the Source into a Transformer Object. The Source is an XSLT document  
			Transformer t = tf.newTransformer(xsltStringSource);
//			t.setOutputProperty(OutputKeys.INDENT, "yes");
//			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			t.transform(xmlStringSource, result);
			
			output = writer.toString();
			System.out.println(output);
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return output;
    }	
	
/*	public static String transformFromString(String xml, String xslt) {
        TransformerFactory tf = TransformerFactory.newInstance();
        String output = null;
        
        try {
        	
            InputStream xmlStream = new ByteArrayInputStream(xml.getBytes("ISO-8859-1"));
            Source xmlStringSource = new StreamSource(xmlStream);
            
             Creo un Result da passare al metodo transform del Transformer 
            StringWriter writer = new StringWriter(); 
//            OutputStreamWriter writer = new OutputStreamWriter();
            Result result = new StreamResult(writer);
//            Result result = new StreamResult(new OutputStreamWriter(writer2,"ISO-8859-1"));
            
             Creo un Source da passare al metodo newTransformer del Transformer 
            InputStream xsltStream = new ByteArrayInputStream(xslt.getBytes("ISO-8859-1"));
            Source xsltStringSource = new StreamSource(xsltStream);
            
             Imposto l'xslt come StreamSource del Transformer e poi eseguo la trasformazione:
             * Process the Source into a Transformer Object. The Source is an XSLT document  
            Transformer t = tf.newTransformer(xsltStringSource);
            t.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            t.transform(xmlStringSource, result);
            
            output = writer.toString();
//            System.out.println(output);
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
    }	*/
	

}
