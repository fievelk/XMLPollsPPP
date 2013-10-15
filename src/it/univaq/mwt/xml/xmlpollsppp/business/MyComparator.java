package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.model.Option;

import java.util.Comparator;

public class MyComparator implements Comparator<Option> {

	// Questa classe implementa il comparatore per la TreeMap di <Options, BigDecimal> in XMLDBPollService
	
    @Override
    public int compare(Option opt1, Option opt2) {
    	return opt1.getCode().compareTo(opt2.getCode());
    }

}
