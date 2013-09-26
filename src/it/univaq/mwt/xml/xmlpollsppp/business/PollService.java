package it.univaq.mwt.xml.xmlpollsppp.business;

import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Poll;

import java.util.List;

public interface PollService {
	
	List<Poll> getAllPollsSkeletons() throws RepositoryError;
	
	List<Poll> getAllSubmittedPolls();

}
