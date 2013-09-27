package it.univaq.mwt.xml.xmlpollsppp.presentation;

import it.univaq.mwt.xml.xmlpollsppp.business.PollService;
import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootController {

	@Autowired
	private PollService service;

	@RequestMapping("/")
	public String getAllPolls(Model model) throws RepositoryError {
		List<String> pollTitles = service.getAllPollsSkeletons();
		model.addAttribute("pollTitles",pollTitles);
		
		List<String> pollBoh = service.getPollSkeletonByCode("1");
		System.out.println("Controller getPollSkeletonByCode "+pollBoh);
		
		HashMap<String,String> codeTitles = service.getPollsCodeAndTitleById("poll.xml");
		System.out.println("CONTROLLER CODETITLE " + codeTitles);
		
		model.addAttribute("codeTitles", codeTitles);
		
		return "common.index";
	}
	
/*	@RequestMapping("/")
	public String getAllPolls(Model model) throws RepositoryError {
		List<String> polls = service.getAllPollsSkeletons();
		model.addAttribute("polls",polls);
		return "common.index";
	}*/
}