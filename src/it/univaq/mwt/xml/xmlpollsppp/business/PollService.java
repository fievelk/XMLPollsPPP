package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;

import java.util.List;
import java.util.TreeMap;

import org.xmldb.api.base.ResourceSet;

public interface PollService {
	
	List<String> getPollSkeletonBy(String criteria) throws RepositoryError;

	String getPollSkeletonByCode(String code) throws RepositoryError;
	
	TreeMap<String, String> getAllPollsCodeAndTitle() throws RepositoryError;

	String getPollsXSLT() throws RepositoryError;

	String createSubmittedPoll(String submittedPoll) throws RepositoryError;
	
}
