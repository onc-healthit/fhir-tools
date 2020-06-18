package org.sitenv.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bulk_data_requests")
public class DafBulkDataRequest {
	
	@Id
	@Column(name = "request_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer requestId;
	
	@Column(name="resource_name")
	private String resourceName;
	
	@Column(name="resource_id")
	private String resourceId;
	
	@Column(name="request_resource")
	private String requestResource;
	
	@Column(name="start")
	private String start;
	
	@Column(name="_type")
	private String type;
	
	@Column(name="content_location")
	private String contentLocation;
	
	@Column(name="files")
	private String files;
	
	@Column(name="status")
	private String status;
	
	@Column(name="processed_flag")
	private Boolean processedFlag;
	
	@Column(name="outputFormat")
	private String outputFormat;

	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getRequestResource() {
		return requestResource;
	}

	public void setRequestResource(String requestResource) {
		this.requestResource = requestResource;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContentLocation() {
		return contentLocation;
	}

	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getProcessedFlag() {
		return processedFlag;
	}

	public void setProcessedFlag(Boolean processedFlag) {
		this.processedFlag = processedFlag;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	@Override
	public String toString() {
		return "DafBulkDataRequest [requestId=" + requestId + ", resourceName=" + resourceName + ", resourceId="
				+ resourceId + ", requestResource=" + requestResource + ", start=" + start + ", type=" + type
				+ ", contentLocation=" + contentLocation + ", files=" + files + ", status=" + status
				+ ", processedFlag=" + processedFlag + ", outputFormat=" + outputFormat + "]";
	}
	
}
