package it.univaq.mwt.xml.xmlpollsppp.business.model;

import java.math.BigDecimal;
import java.util.List;

public class Poll {
	private List<Question> questions;
	private List<Option> options;
	private BigDecimal pollSubmissions;
	private BigDecimal submissionsWithNonReqAnswer;
	
	public Poll() {
		super();
	}


	public BigDecimal getSubmissionsWithNonReqAnswer() {
		return submissionsWithNonReqAnswer;
	}


	public void setSubmissionsWithNonReqAnswer(
			BigDecimal submissionsWithNonReqAnswer) {
		this.submissionsWithNonReqAnswer = submissionsWithNonReqAnswer;
	}




	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public BigDecimal getPollSubmissions() {
		return pollSubmissions;
	}

	public void setPollSubmissions(BigDecimal pollSubmissions) {
		this.pollSubmissions = pollSubmissions;
	}
	

}
