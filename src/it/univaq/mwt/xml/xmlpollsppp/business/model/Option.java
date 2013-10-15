package it.univaq.mwt.xml.xmlpollsppp.business.model;

public class Option {
	
	private String code;
	private String content;
	private Question question;
	
	
	public Option(String code, String content) {
		super();
		this.code = code;
		this.content = content;
	}
	
	public Option() {
		super();
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
