package org.sitenv.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "extract_tasks")
public class ExtractionTask {
	
	@Id
    @Column(name = "et_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer extractionTaskId;
	
	@Column(name="group_id")
	private Integer groupId;
	
	@Column(name="request_url")
	private String requestURL;
	
	@Column(name="content_location")
	private String contentLocation;
	
	@Column(name="links")
	private String links;
	
	@Column(name="status")
	private String status;
	
	@Column(name="response_body")
	private String responseBody;
	
	@Column(name="process_flag")
	private Boolean processFlag;
	
	@Column(name="auth_mode")
	private String authenticationMode;

	public Integer getExtractionTaskId() {
		return extractionTaskId;
	}

	public void setExtractionTaskId(Integer extractionTaskId) {
		this.extractionTaskId = extractionTaskId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getContentLocation() {
		return contentLocation;
	}

	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public Boolean getProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(Boolean processFlag) {
		this.processFlag = processFlag;
	}

	public String getAuthenticationMode() {
		return authenticationMode;
	}

	public void setAuthenticationMode(String authenticationMode) {
		this.authenticationMode = authenticationMode;
	}
	
}
