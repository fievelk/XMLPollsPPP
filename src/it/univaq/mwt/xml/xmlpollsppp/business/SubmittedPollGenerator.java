package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class SubmittedPollGenerator {
	
	private static ListMultimap<String, String> fromMapToMultiMap(Map<String, String[]> map) {
		ListMultimap<String, String> multiMap = ArrayListMultimap.create();
		for (String key : map.keySet()){
			String[] values = map.get(key);
			for (String value : values) {
				multiMap.put(key, value);
			}
		}
		return multiMap;
	}
	
/*	private static ListMultimap<String, String> fromStringToMap(String inputString) {
		ListMultimap<String, String> map = ArrayListMultimap.create();
		String[] keyValues = inputString.split("&");
		for (String keyValue : keyValues) {
			String[] pairs = keyValue.split("=");
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
		}
		return map;
	}*/
	
	/* Questo metodo prende l'xml dello skeleton e lo trasforma in un xml di submission con le risposte */
//	public static String generateSubmissionPoll(String pollSkeleton, String pollResults) {
		public static String generateSubmissionPoll(String pollSkeleton, Map pollResults) {
//		ListMultimap<String,String> questionAnswers = fromStringToMap(pollResults);
		ListMultimap<String,String> questionAnswers = fromMapToMultiMap(pollResults);
        XMLInputFactory xif = XMLInputFactory.newInstance();
        
        /* Sono costretto a inserire manualmente la stringa con la dichiarazione dell'encoding
         * altrimenti l'XMLStreamReader reagirà al documento come se fosse UTF-8 (per default).
         * Non c'è modo per cambiare questo comportamento tramite metodi o set di parametri */
        
//        pollSkeleton = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+pollSkeleton;
        pollSkeleton = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+pollSkeleton;
        XMLEventReader xer = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try {
        	// Creo uno Stream che contenga il documento XML da leggere
            // converto la String in un InputStream
        	InputStream inputStream = new ByteArrayInputStream(pollSkeleton.getBytes("UTF-8"));
        	xer = xif.createXMLEventReader(inputStream);
        	XMLOutputFactory xof = XMLOutputFactory.newInstance();

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
                    Potrebbero essere presenti altri attributi prima di 'code'.
                    canConvertOptionToAnswer() returns true se il valore dell'attributo 'code' è contenuto nella map "questionAnswers"
                    Se c'è il match converte <option> in <answers>, altrimenti skippa l'<option> fino a </option>
                     */
                	if (canConvertOptionToAnswer(optionStartElementEvent, questionAnswers)) {
                		convertOptionToAnswer(whitespaceBeforeOptionStartElement, optionStartElementEvent, xer, writer, eventFactory);
                	} else {
                		skipOption(xer);
                	}
                	whitespaceBeforeOptionStartElement=null;
                	
                } else if (event.getEventType() == XMLEvent.START_ELEMENT && event.asStartElement().getName().getLocalPart().equals("poll")) {
                
                	StartElement pollStartElementEvent = event.asStartElement();
                	convertPollToSubmittedPoll(pollStartElementEvent, xer, writer, eventFactory);
                }
                else {
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
//        System.out.println(result);
		return result;
	}


	private static void skipOption(XMLEventReader xer) throws XMLStreamException {
		/* Finché non arriva a </option>, l'XMLEventReader skippa gli eventi, non inviandoli al writer.
		 * Dà per scontato che l'evento precedente dell'XMLEventReader xer fosse START_ELEMENT <option> */
		XMLEvent eventWithinOptionElement;
		do {
	      eventWithinOptionElement = xer.nextEvent();
	    }
	    while (!(eventWithinOptionElement.getEventType() == XMLEvent.END_ELEMENT && eventWithinOptionElement.asEndElement().getName().getLocalPart().equals("option")));
	}



	private static boolean canConvertOptionToAnswer(StartElement optionStartElementEvent, ListMultimap<String, String> questionAnswers) {
		Iterator ite = optionStartElementEvent.getAttributes();
		while (ite.hasNext()) {
    		Attribute attr = (Attribute) ite.next();
    		if (attr.getName().getLocalPart().equals("code") && questionAnswers.containsValue(attr.getValue())) {
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
		Copia gli attributi. N.B.: getAttributes() potrebbe non restituire gli attributi nello stesso ordine in cui compaiono nel documento XML,
		in quanto (secondo la specifica XML) l'ordine degli attributi non è importante.
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
	
	private static void convertPollToSubmittedPoll(StartElement pollStartElementEvent, XMLEventReader xer,	XMLEventWriter writer, XMLEventFactory eventFactory) throws XMLStreamException {
		
		writer.add(eventFactory.createStartElement("", null, "submittedPoll"));
		// Specifica i namespaces del documento xml submittedPoll 
		writer.add(eventFactory.createAttribute("", "", "xmlns", "http://it.univaq.mwt.xml/submittedpoll"));
		writer.add(eventFactory.createAttribute("xmlns", "http://it.univaq.mwt.xml/submittedpoll", "xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		writer.add(eventFactory.createAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", "http://it.univaq.mwt.xml/submittedpoll submittedpoll.xsd"));
	    // E' in grado di sostituire automaticamente il tag di chiusura da /poll a /submittedPoll
	}
	
}