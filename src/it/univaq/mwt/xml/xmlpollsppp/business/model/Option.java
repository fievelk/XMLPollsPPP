package it.univaq.mwt.xml.xmlpollsppp.business.model;

public class Option implements Comparable {
	
	private String code;
	private String content;
	
	
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		
		return 0;
	}

	
}
