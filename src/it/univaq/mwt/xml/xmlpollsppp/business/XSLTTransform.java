package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XSLTTransform {

    public static String transformFromString(String xml, File xslt) {
        TransformerFactory tf = TransformerFactory.newInstance();
        String output = null;
        
        try {
        	
			Transformer t = tf.newTransformer(new StreamSource(xslt));
			Reader xmlString = new StringReader(xml);
			Source xmlStringSource = new StreamSource(xmlString);
//			t.transform(xmlStringSource, new StreamResult(System.out));
			
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
    }
}
