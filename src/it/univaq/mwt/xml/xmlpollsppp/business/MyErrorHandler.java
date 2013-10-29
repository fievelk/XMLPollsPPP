package it.univaq.mwt.xml.xmlpollsppp.business;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MyErrorHandler implements ErrorHandler {

	private int nWarnings, nErrors, nFatals;

	public MyErrorHandler() {
		reset();
	}

	public void reset() {
		nWarnings = 0;
		nErrors = 0;
		nFatals = 0;
	}

	public boolean hasProblems() {
		return (nErrors + nFatals > 0);
	}

	@Override
	public void warning(SAXParseException ex) throws SAXException {
		System.out.println("Warning [" + ex.getSystemId() + ":"
				+ ex.getLineNumber() + "," + ex.getColumnNumber() + "] "
				+ ex.getMessage());
		nWarnings++;
	}

	@Override
	public void error(SAXParseException ex) throws SAXException {
		System.out.println("Errore [" + ex.getSystemId() + ":"
				+ ex.getLineNumber() + "," + ex.getColumnNumber() + "] "
				+ ex.getMessage());
		nErrors++;
	}

	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		System.out.println("Errore Fatale [" + ex.getSystemId() + ":"
				+ ex.getLineNumber() + "," + ex.getColumnNumber() + "] "
				+ ex.getMessage());
		nFatals++;
		throw ex;
	}
}
