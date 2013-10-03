package it.univaq.mwt.xml.xmlpollsppp.business;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;

public class SubmittedPollGenerator {
	
	public static String generateSubmittedPoll(String pollSkeleton) {
		
		parseStAXDocument(pollSkeleton);
//		StAXParseDocument(new File("/home/fievelk/Dropbox/MWT_mia/xml/casa/progettouniPoll/poll.xml"));
		return null;
	}

	
	private static void parseStAXDocument(String inputXmlString) {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = null;
        
        /* Sono costretto a inserire manualmente la stringa con la dichiarazione dell'encoding
         * altrimenti l'XMLStreamReader reagirà al documento come se fosse UTF-8 (per default).
         * Non c'è modo per cambiare questo comportamento tramite metodi o set di parametri */
        
        inputXmlString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+inputXmlString;
    	
        try {
        	// Creo uno Stream che contenga il documento XML da leggere
            // converto la String in un InputStream
        	InputStream inputStream = new ByteArrayInputStream(inputXmlString.getBytes("ISO-8859-1"));
        	
        	System.out.println(inputXmlString);
        	/*StringWriter writer = new StringWriter();
        	try {
				IOUtils.copy(inputStream, writer, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	System.out.println(writer.toString());*/
        	
        	
        	
        	// Genero un XMLStreamReader passandogli lo stream della stringa xml da leggere
			xsr = xif.createXMLStreamReader(inputStream);
//			System.out.println("ENCODING "+xif.createXMLStreamReader(inputStream).getCharacterEncodingScheme());
			
			while (xsr.hasNext()){
				int next = xsr.next();
				if (xsr.isWhiteSpace()) continue;
				switch (next) {
					case XMLStreamReader.START_ELEMENT:	
						System.out.println("STARTELEMENT "+xsr.getLocalName());
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
	}
	
    public static void StAXParseDocument(File f) {
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
    }	
	
}
