package it.univaq.mwt.xml.xmlpollsppp.business.model;

import java.math.BigDecimal;

public class Option {
	
	private String code;
	private String content;
	private Question question;
	private BigDecimal count;
	
	public Option(String code, String content, Question question,
			BigDecimal count) {
		super();
		this.code = code;
		this.content = content;
		this.question = question;
		this.count = count;
	}

	public Option(String code, String content, BigDecimal count) {
		super();
		this.code = code;
		this.content = content;
		this.count = count;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}


	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

}
