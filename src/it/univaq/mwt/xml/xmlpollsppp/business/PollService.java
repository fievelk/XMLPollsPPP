package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Option;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Question;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

import org.xmldb.api.base.ResourceSet;

public interface PollService {
	
	List<String> getPollSkeletonBy(String criteria) throws RepositoryError;

	String getPollSkeletonByCode(String code) throws RepositoryError;
	
	TreeMap<String, String> getAllPollsCodeAndTitle() throws RepositoryError;

	String getPollsXSLT() throws RepositoryError;

	String createSubmittedPoll(String submittedPoll) throws RepositoryError;
	
	TreeMap<Option, BigDecimal> getPollAnswersStats(int pollCode, String questionCode) throws RepositoryError;
	
	List<Question> getAllPollQuestions(int pollCode) throws RepositoryError;
	
}
