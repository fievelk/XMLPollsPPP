package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

public class SubmittedPollGenerator {
	
	private static Map<String,String> fromStringToMap(String inputString) {

		Map<String, String> map = new LinkedHashMap<String, String>();
		String[] keyValues = inputString.split("&");
		for (String keyValue : keyValues) {
			String[] pairs = keyValue.split("=");
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
			System.out.println(pairs[0] + "-> " + pairs[1]);
		}
		return map;
	}
	
	
	public static String generateSubmittedPoll(String pollSkeleton, String pollResults) {
		
		Map<String,String> questionAnswers = fromStringToMap(pollResults);
		
		generateSubmissionPoll(pollSkeleton);
		
//		StAXParseDocument(new File("/home/fievelk/Dropbox/MWT_mia/xml/casa/progettouniPoll/poll.xml"));
		return null;
	}

	
/*	private static void parseStAXDocument(String inputXmlString) {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = null;
        
         Sono costretto a inserire manualmente la stringa con la dichiarazione dell'encoding
         * altrimenti l'XMLStreamReader reagirà al documento come se fosse UTF-8 (per default).
         * Non c'è modo per cambiare questo comportamento tramite metodi o set di parametri 
        
        inputXmlString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+inputXmlString;
    	
        try {
        	// Creo uno Stream che contenga il documento XML da leggere
            // converto la String in un InputStream
        	InputStream inputStream = new ByteArrayInputStream(inputXmlString.getBytes("ISO-8859-1"));
        	
        	System.out.println(inputXmlString);
        	
        	// Genero un XMLStreamReader passandogli lo stream della stringa xml da leggere
			xsr = xif.createXMLStreamReader(inputStream);
			
			while (xsr.hasNext()){
				int next = xsr.next();
				if (xsr.isWhiteSpace()) continue;
				switch (next) {
					case XMLStreamReader.START_ELEMENT:	
//						System.out.println("STARTELEMENT "+xsr.getLocalName());
				}
			} 
		} catch (XMLStreamException e) {
			e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
            try {
                if (xsr != null) {
                    xsr.close();
                }
            } catch (XMLStreamException ex) {
            }
        }
	}*/
	
	/* Questo metodo prende l'xml dello skeleton e lo trasforma in un xml di submission con le risposte */
	private static void generateSubmissionPoll(String inputXmlString) {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        
        /* Sono costretto a inserire manualmente la stringa con la dichiarazione dell'encoding
         * altrimenti l'XMLStreamReader reagirà al documento come se fosse UTF-8 (per default).
         * Non c'è modo per cambiare questo comportamento tramite metodi o set di parametri */
        
        inputXmlString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+inputXmlString;
        XMLEventReader xer = null;
        
        try {
        	// Creo uno Stream che contenga il documento XML da leggere
            // converto la String in un InputStream
        	InputStream inputStream = new ByteArrayInputStream(inputXmlString.getBytes("ISO-8859-1"));
        	xer = xif.createXMLEventReader(inputStream);
        	
        	XMLOutputFactory xof = XMLOutputFactory.newInstance();
        	
        	XMLEventWriter writer = xof.createXMLEventWriter(new OutputStreamWriter(System.out, "UTF-8")); // Perché con ISO-8859-1 non funziona?
        	XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        	
        	
        	while (xer.hasNext()) {
                XMLEvent event = xer.nextEvent();
                
                if (event.getEventType() == XMLEvent.START_ELEMENT) {
//                	System.out.println("elemento: "+event.asStartElement().getName().getLocalPart());
                    if (event.asStartElement().getName().getLocalPart().equals("option")) {
                    	// Lo sostituisco con un elemento answer
                    	writer.add(eventFactory.createStartElement("", null, "answer"));
                        
                    	/* Itero sugli attributi dell'option. Se sono "code" ne prendo il valore e li aggiungo come
                    	 * attributi dell'elemento answer che ho creato	 */
                    	Iterator ite = event.asStartElement().getAttributes();
                    	while (ite.hasNext()) {
                    		Attribute attr = (Attribute) ite.next();
                    		if (attr.getName().getLocalPart().equals("code")) {
                    			String optionCodeValue = attr.getValue();
                    			writer.add(eventFactory.createAttribute("code", optionCodeValue));
                    		} // Cosa succede se il prossimo attributo non è un code?
                    	}
                        event = xer.nextEvent();
                    }
                }
                writer.add(event);
            }
        	writer.close();
        	
		} catch (XMLStreamException e) {
			e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
            try {
                if (xer != null) {
                    xer.close();
                }
            } catch (XMLStreamException ex) {
            }
        }
	}	
	
	/*
	 * XMLInputFactory inFactory = XMLInputFactory.newInstance();
    XMLEventReader eventReader = inFactory.createXMLEventReader(new FileInputStream("1.xml"));
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    XMLEventWriter writer = factory.createXMLEventWriter(new FileWriter(file));
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        writer.add(event);
        if (event.getEventType() == XMLEvent.START_ELEMENT) {
            if (event.asStartElement().getName().toString().equalsIgnoreCase("book")) {
                writer.add(eventFactory.createStartElement("", null, "index"));
                writer.add(eventFactory.createEndElement("", null, "index"));
            }
        }
    }
    writer.close(); */
	
	
	
	
	
	
	
/*    public static void StAXParseDocument(File f) {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = null;
        FileReader fr = null;
        try {
            fr = new FileReader(f);
            xsr = xif.createXMLStreamReader(fr);
            System.out.println("ENCODING: "+xsr.getCharacterEncodingScheme());
            while (xsr.hasNext()) {
                int next = xsr.next();
                if (xsr.isWhiteSpace()) {
                    continue;
                }
                switch (next) {
				case XMLStreamReader.START_ELEMENT:	
					System.out.println("STARTELEMENT "+xsr.getLocalName());
                }
            }
        } catch (FileNotFoundException ex) {
        } catch (XMLStreamException ex) {
            System.out.println("Errore ["
                    + ex.getLocation().getSystemId() + ":"
                    + ex.getLocation().getLineNumber() + ","
                    + ex.getLocation().getColumnNumber() + "] "
                    + ex.getMessage());
        } finally {
            try {
                if (xsr != null) {
                    xsr.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (XMLStreamException ex) {
                //
            } catch (IOException ex) {
                //
            }
        }
    }	*/
	
}
