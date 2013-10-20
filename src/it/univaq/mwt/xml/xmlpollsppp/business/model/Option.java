package it.univaq.mwt.xml.xmlpollsppp.business.model;

import java.math.BigDecimal;

public class Option {
	
	private String code;
	private String content;
	private Question question;
	private BigDecimal count;
	private BigDecimal angleValue;
	private BigDecimal percentValue;
	
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

	// Utilizzato per costruire le slice in SVGGenerator
	public Option(String content, BigDecimal count) {
		super();
		this.content = content;
		this.count = count;
	}

	public BigDecimal getAngleValue() {
		return angleValue;
	}

	public void setAngleValue(BigDecimal angleValue) {
		this.angleValue = angleValue;
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

	public BigDecimal getPercentValue() {
		return percentValue;
	}

	public void setPercentValue(BigDecimal percentValue) {
		this.percentValue = percentValue;
	}
	
}
