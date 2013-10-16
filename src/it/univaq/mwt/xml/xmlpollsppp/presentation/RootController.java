package it.univaq.mwt.xml.xmlpollsppp.presentation;

import it.univaq.mwt.xml.xmlpollsppp.business.PollService;
import it.univaq.mwt.xml.xmlpollsppp.business.SVGGenerator;
import it.univaq.mwt.xml.xmlpollsppp.business.SubmittedPollGenerator;
import it.univaq.mwt.xml.xmlpollsppp.business.XSLTTransform;
import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;
import it.univaq.mwt.xml.xmlpollsppp.business.model.GraphContainer;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Option;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Question;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
//		System.out.println(pollResults);
		service.createSubmittedPoll(submittedPoll);
		model.addAttribute("result","<xmp>"+submittedPoll+"</xmp>");
		
		return "poll.result";
	}	
	
	@RequestMapping(value="/polls/{skeletonId}/stats.do")
	public String pollStats(@PathVariable("skeletonId") int skeletonId, Model model) throws RepositoryError {

		List<Question> questions = service.getAllPollQuestions(skeletonId);
//		List<TreeMap> stats = new ArrayList<TreeMap>();
		
		// Per ogni question, ottengo la mappa delle sue options e del numero di persone che le hanno selezionate
		// Inserisco il risultato in una lista di queste mappe 
		for (Question question : questions) {
//			TreeMap<Option, BigDecimal> answersNumbers = service.getPollAnswersStats(skeletonId, question.getCode());
			List<Option> options = service.getPollAnswersStats(skeletonId, question.getCode());
//			stats.add(answersNumbers);
			question.setOptions(options);
		}
//		for (Question question : questions) {
//			for (Option opt : question.getOptions())
//				System.out.println(opt.getCount());
//		}
		
//		List<GraphContainer> graphContainerList = SVGGenerator.generateSVG(stats);
		List<GraphContainer> graphContainerList = SVGGenerator.generateSVG(questions);
		
		// answersNumbers è una TreeMap contenente l'opzione e il numero di persone che l'hanno selezionata
		model.addAttribute("graphContainerList",graphContainerList);
		
		return "poll.stats";
	}	

}