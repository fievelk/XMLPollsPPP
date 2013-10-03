package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StringToXMLProcessor {

	public static Map<String,String> fromStringToMap(String inputString) {

		Map<String, String> map = new LinkedHashMap<String, String>();
		String[] keyValues = inputString.split("&");
		for (String keyValue : keyValues) {
			String[] pairs = keyValue.split("=");
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
			System.out.println(pairs[0] + "-> " + pairs[1]);
		}
		return map;
	}
	
	public static XMLStreamWriter createStAXDocument(String rootElement) {
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xsw = null;

        try {
			xsw = xof.createXMLStreamWriter(new OutputStreamWriter(System.out, "ISO-8859-1")); // E ORA?
			xsw = new IndentingXMLStreamWriter(xsw);
			xsw.writeStartDocument("ISO-8859-1", "1.0");
			xsw.writeDTD("<!DOCTYPE " + rootElement + " \">");
			return xsw;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

        if (xsw != null) {
            try {
                xsw.close();
            } catch (XMLStreamException ex) {
            }
        }
        return null;
	}
	
	public static void fillStAXDocument(XMLStreamWriter xsw) {
        try {
			xsw.writeComment("Documento generato automaticamente");
			xsw.writeStartElement("submittedPoll");
			xsw.writeStartElement("pollHead");
			xsw.writeStartElement("skeletonCode");
			xsw.writeCharacters("1"); // rendere dinamico
			xsw.writeEndElement();
			xsw.writeStartElement("submissionCode");
			xsw.writeCharacters("121"); // rendere dinamico (come?)
			xsw.writeEndElement();
			xsw.writeStartElement("user");
			xsw.writeCharacters("nome utente");
			xsw.writeEndElement();
			xsw.writeStartElement("date");
			xsw.writeCharacters("2013-09-22T14:45:00"); // Rendere dinamica
			xsw.writeEndElement();
			xsw.writeEndElement(); // fine pollHead
			
			xsw.writeStartElement("pollBody");
			xsw.writeStartElement("topic");
			xsw.writeAttribute("code", "T1"); // rendere dinamico o togliere

			xsw.writeStartElement("question");
			xsw.writeAttribute("type", "unique"); // rendere dinamico o togliere
			xsw.writeCharacters("T1Q1"); // rendere dinamico
			xsw.writeEndElement();

			xsw.writeStartElement("option");
			xsw.writeCharacters("T1Q1_2");
			xsw.writeEndElement();
			
			xsw.writeEndElement();
			xsw.writeEndElement();
			
			xsw.writeEndElement();
			xsw.writeEndDocument();
			
			// Devo trovare il modo per rendere dinamici molti valori. Forse la Map di question=option non è sufficiente.
			
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
    }	

	
	public static void createAndFillStAXDocument() {
		XMLStreamWriter xsw = createStAXDocument("submittedPoll");
		if (xsw != null) {
		    fillStAXDocument(xsw);
		    try {
		        xsw.close();
		    } catch (XMLStreamException ex) {
		        //
		    }
		}
	}
}
