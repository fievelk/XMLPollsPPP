package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Option;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Poll;

import java.util.List;
import java.util.TreeMap;

public interface PollService {
	
	List<String> getPollSkeletonBy(String criteria) throws RepositoryError;

	String getPollSkeletonByCode(String code) throws RepositoryError;
	
	TreeMap<String, String> getAllPollsCodeAndTitle() throws RepositoryError;

	String getPollsXSLT() throws RepositoryError;

	List<Option> getPollAnswersStats(int pollCode, String questionCode) throws RepositoryError;
	
	Poll getPollInfos(int pollCode) throws RepositoryError;
	
	boolean storePoll(String submittedPoll) throws RepositoryError;

	String getSubmittedPollsXSD() throws RepositoryError;
	
}
