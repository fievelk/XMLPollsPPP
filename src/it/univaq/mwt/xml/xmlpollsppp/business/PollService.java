package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Poll;

import java.util.HashMap;
import java.util.List;

public interface PollService {
	
	List<String> getAllPollsSkeletons() throws RepositoryError;
	
	List<Poll> getAllSubmittedPolls();

	List<String> getPollSkeletonBy(String criteria) throws RepositoryError;

	List<String> getPollSkeletonByCode(String code) throws RepositoryError;
	
	HashMap<String, String> getPollsCodeAndTitleById(String id) throws RepositoryError;
	
}
