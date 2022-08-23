package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface ProvenanceDao {
	
     public DafProvenance getProvenanceById(String id);
	 
	 public DafProvenance getProvenanceByVersionId(String theId, String versionId);
	 
	 public List<DafProvenance> search(SearchParameterMap theMap);
	 
	 public List<DafProvenance> getProvenanceHistoryById(String theId);

	public List<DafProvenance> getProvenanceRequestDataRequest(StringJoiner patient, Date start);
	
	public List<DafProvenance> getProvenanceByResourceId(List<String> resourceID);

}
