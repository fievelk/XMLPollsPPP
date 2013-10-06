package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class SubmittedPollGenerator {
	
	private static Map<String,String> fromStringToMap(String inputString) {

		Map<String, String> map = new LinkedHashMap<String, String>();
		String[] keyValues = inputString.split("&");
		for (String keyValue : keyValues) {
			String[] pairs = keyValue.split("=");
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
//			System.out.println(pairs[0] + "-> " + pairs[1]);
		}
		return map;
	}
	
	
	
	/* Questo metodo prende l'xml dello skeleton e lo trasforma in un xml di submission con le risposte */
	public static String generateSubmissionPoll(String pollSkeleton, String pollResults) {
		Map<String,String> questionAnswers = fromStringToMap(pollResults);
		
        XMLInputFactory xif = XMLInputFactory.newInstance();
        
        /* Sono costretto a inserire manualmente la stringa con la dichiarazione dell'encoding
         * altrimenti l'XMLStreamReader reagirà al documento come se fosse UTF-8 (per default).
         * Non c'è modo per cambiare questo comportamento tramite metodi o set di parametri */
        
        pollSkeleton = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+pollSkeleton;
        XMLEventReader xer = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try {
        	// Creo uno Stream che contenga il documento XML da leggere
            // converto la String in un InputStream
        	InputStream inputStream = new ByteArrayInputStream(pollSkeleton.getBytes("ISO-8859-1"));
        	xer = xif.createXMLEventReader(inputStream);
        	
        	XMLOutputFactory xof = XMLOutputFactory.newInstance();
        	
        	

			//        	XMLEventWriter writer = xof.createXMLEventWriter(new OutputStreamWriter(System.out, "UTF-8")); // Perché con ISO-8859-1 non funziona?
        	XMLEventWriter writer = xof.createXMLEventWriter(new OutputStreamWriter(byteArrayOutputStream, "UTF-8")); // Perché con ISO-8859-1 non funziona?
        	XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        	
        	XMLEvent whitespaceBeforeOptionStartElement=null;
        	
        	while (xer.hasNext()) {
                XMLEvent event = xer.nextEvent();
                
                // Se l'evento attuale è un whitespace e il suo prossimo evento è un option, lo associo alla variabile whitespaceBeforeOptionStartElement
                if (event.getEventType() == XMLEvent.CHARACTERS && event.asCharacters().isWhiteSpace() && 
                	xer.peek().getEventType() == XMLEvent.START_ELEMENT && xer.peek().asStartElement().getName().getLocalPart().equals("option")) {
                	
                	whitespaceBeforeOptionStartElement = event;
                
                // Invece se l'evento attuale è un option, lo associo alla variabile optionStartElementEvent	
                } else if (event.getEventType() == XMLEvent.START_ELEMENT && event.asStartElement().getName().getLocalPart().equals("option")) {

                	StartElement optionStartElementEvent = event.asStartElement();
                	
                	/*
                    In general there can  be some other attribute before code attribute.
                    canConvertOptionToAnswer() returns true if "code" attribute value is contained in  "questionAnswers" map
                    If there is a match do conversion of <option> to <answer> else skip <option> until </option>
                     */
                	
                	if (canConvertOptionToAnswer(optionStartElementEvent, questionAnswers)) {
                		convertOptionToAnswer(whitespaceBeforeOptionStartElement, optionStartElementEvent, xer, writer, eventFactory);
                	} else {
                		skipOption(xer);
                	}
                	whitespaceBeforeOptionStartElement=null;
                } else {
                writer.add(event);
                }
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
        
        String result = byteArrayOutputStream.toString();
        System.out.println(result);
		return result;
	}


	private static void skipOption(XMLEventReader xer) throws XMLStreamException {
		/* Finché non arriva a </option>, l'XMLEventReader skippa gli eventi, non inviandoli al writer.
		 * Dà per scontato che l'evento precedente dell' XMLEventReader xer fosse START_ELEMENT <option> */
		XMLEvent eventWithinOptionElement;
		do {
	      eventWithinOptionElement = xer.nextEvent();
	    }
	    while (!(eventWithinOptionElement.getEventType() == XMLEvent.END_ELEMENT && eventWithinOptionElement.asEndElement().getName().getLocalPart().equals("option")));
	}



	private static boolean canConvertOptionToAnswer(StartElement optionStartElementEvent,	Map<String, String> questionAnswers) {
		Iterator ite = optionStartElementEvent.getAttributes();
//		System.out.println(optionStartElementEvent.toString());
		while (ite.hasNext()) {
    		Attribute attr = (Attribute) ite.next();
    		System.out.println(attr.getName().getLocalPart()+": "+attr.getValue()); // Errore: mi restituisce due volte "code: T4Q3_2"
    		if (attr.getName().getLocalPart().equals("code") && questionAnswers.containsValue(attr.getValue())) {
    			System.out.println(attr.getName().getLocalPart()+": "+attr.getValue());
    			return true;
    		}
    	}
		return false;
	}	
	

	private static void convertOptionToAnswer(XMLEvent whitespaceBeforeOptionStartElement, StartElement optionStartElementEvent, XMLEventReader xer, XMLEventWriter writer, XMLEventFactory eventFactory) throws XMLStreamException {

		if (whitespaceBeforeOptionStartElement != null) {
			writer.add(whitespaceBeforeOptionStartElement);
		}
		
		writer.add(eventFactory.createStartElement("", null, "answer"));
		
		/*
	    copy attributes. Note that getAttributes() may not return attributes in the same order as they appear in xml document
	    because according to XML spec order of attributes is not important.
	     */
	    Iterator ite = optionStartElementEvent.getAttributes();
	    while(ite.hasNext())
	    {
	      Attribute attribute = (Attribute) ite.next();
	      writer.add(attribute);
	    }
	    
	    //copia tutto quello che compare fino all'elemento </option>:
	    XMLEvent eventWithinOptionElement = xer.nextEvent();
	    while (!(eventWithinOptionElement.getEventType() == XMLEvent.END_ELEMENT && eventWithinOptionElement.asEndElement().getName().getLocalPart().equals("option"))) {
	    	writer.add(eventWithinOptionElement);
	        eventWithinOptionElement = xer.nextEvent();
	    }
	    writer.add(eventFactory.createEndElement("", null, "answer"));	    
	}	
	
	
	
	
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
