package it.univaq.mwt.xml.xmlpollsppp.presentation;

import it.univaq.mwt.xml.xmlpollsppp.business.PollService;
import it.univaq.mwt.xml.xmlpollsppp.business.XSLTTransform;
import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;

import java.io.File;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xmldb.api.base.XMLDBException;

@Controller
public class RootController {

	@Autowired
	private PollService service;

	@RequestMapping("/")
	public String getAllPolls(Model model) throws RepositoryError, XMLDBException {
		TreeMap<String,String> codeTitles = service.getAllPollsCodeAndTitle();
		model.addAttribute("codeTitles", codeTitles);
		return "common.index";
	}
	
	@RequestMapping("/polls/{pollId}")
	public String pollForm(@PathVariable("pollId")String prodId, Model model) throws RepositoryError, XMLDBException {
		String pollSkeleton = service.getPollSkeletonByCode(prodId);
//		String outputxml = XSLTTransform.transformFromString(pollSkeleton, new File("/home/fievelk/Dropbox/MWT_mia/xml/casa/progettouniPoll/poll_prova.xslt"));
		String xslt = service.getPollsXSLT();
		String outputxml = XSLTTransform.transformFromString(pollSkeleton, xslt);
		model.addAttribute("poll",outputxml);
		return "poll.form";
	}

}