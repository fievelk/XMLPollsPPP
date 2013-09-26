package it.univaq.mwt.xml.xmlpollsppp.business.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Poll implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long id;
	private long code;
	private String title;
	private String description;
	private String author;
	private Date date;
	private List<Topic> topics;
	
	public Poll() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
