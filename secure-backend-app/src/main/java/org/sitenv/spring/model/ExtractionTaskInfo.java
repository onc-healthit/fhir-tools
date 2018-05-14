package org.sitenv.spring.model;

public class ExtractionTaskInfo {
	
	private Integer dataSourceId;
	
	private Integer extractionId;
	
	private String url;
	
	public ExtractionTaskInfo() {
		
	}

	public Integer getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(Integer dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public Integer getExtractionId() {
		return extractionId;
	}

	public void setExtractionId(Integer extractionId) {
		this.extractionId = extractionId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
