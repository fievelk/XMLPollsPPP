package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class MyValidator {

	public boolean validate(String xml, String xsdSchema) {
		System.out.print("In validazione...");
		MyErrorHandler errorHandler = new MyErrorHandler();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        sf.setErrorHandler(errorHandler);
        // Devo trasformare xsdSchema in Source;
//        System.out.println("XML nel validator: "+xml);
		Source xsdSource = new StreamSource(new StringReader(xsdSchema));
        try {
            Schema s;
            if (xsdSchema == null) {
                s = sf.newSchema(); //con schema dichiarato nel documento
            } else {
                s = sf.newSchema(xsdSource); // uso il costruttore con Source, non con File
            }
            Validator v = s.newValidator();
            errorHandler.reset();
            v.setErrorHandler(errorHandler);
//            v.validate(new StreamSource(new StringReader(xml)), new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
            v.validate(new StreamSource(new StringReader(xml)));
        } catch (SAXException ex) {
        	//
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        if (errorHandler.hasProblems()) {
            System.out.println("NON valido!");
            return false;
        } else {
            System.out.println("valido!");
            return true;
        }
	}
	
}
