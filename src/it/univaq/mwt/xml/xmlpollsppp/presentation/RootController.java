package it.univaq.mwt.xml.xmlpollsppp.presentation;

import it.univaq.mwt.xml.xmlpollsppp.business.PollService;
import it.univaq.mwt.xml.xmlpollsppp.business.StringToXMLProcessor;
import it.univaq.mwt.xml.xmlpollsppp.business.SubmittedPollGenerator;
import it.univaq.mwt.xml.xmlpollsppp.business.XSLTTransform;
import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xmldb.api.base.XMLDBException;

@Controller
public class RootController {

	@Autowired
	private PollService service;

	@RequestMapping("/")
	public String getAllPolls(Model model) throws RepositoryError {
		TreeMap<String,String> codeTitles = service.getAllPollsCodeAndTitle();
		model.addAttribute("codeTitles", codeTitles);
		return "common.index";
	}
	
	@RequestMapping("/polls/{skeletonId}")
	public String pollForm(@PathVariable("skeletonId") String skeletonId, Model model)  throws RepositoryError {
		String pollSkeleton = service.getPollSkeletonByCode(skeletonId);
		String xslt = service.getPollsXSLT();
		String outputxml = XSLTTransform.transformFromString(pollSkeleton, xslt);
		model.addAttribute("poll",outputxml);
		return "poll.form";
	}
	
	@RequestMapping(value="/polls/{skeletonId}/submitpoll.do", method=RequestMethod.POST)
	public String submitPoll(@RequestBody String pollResults, @PathVariable("skeletonId") String skeletonId, Model model) throws RepositoryError {

		String pollSkeleton = service.getPollSkeletonByCode(skeletonId);
		
		String submittedPoll = SubmittedPollGenerator.generateSubmissionPoll(pollSkeleton, pollResults); // Crea il submittedPoll a partire dal pollSkeleton
		
		System.out.println(pollResults);
		model.addAttribute("result","<xmp>"+submittedPoll+"</xmp>");
		
		return "poll.result";
	}	

}