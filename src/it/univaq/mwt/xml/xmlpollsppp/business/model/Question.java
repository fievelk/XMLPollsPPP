package it.univaq.mwt.xml.xmlpollsppp.business.model;

import java.util.List;

public class Question {
	
	private String code;
	private String content;
	private List<Option> options;
	private boolean required;
	
	
	public Question() {
		super();
	}

	public Question(String code, String content, List<Option> options) {
		super();
		this.code = code;
		this.content = content;
		this.options = options;
	}
	
	public Question(String code, String content) {
		super();
		this.code = code;
		this.content = content;
	}
	

	public Question(String code, String content, boolean required) {
		super();
		this.code = code;
		this.content = content;
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
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

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
		for (Option option : options) {
			option.setQuestion(this);
		}
	}

	
}
