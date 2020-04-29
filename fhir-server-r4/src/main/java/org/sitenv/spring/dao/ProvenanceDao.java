package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ProvenanceDao {
	
     public DafProvenance getProvenanceById(String id);
	 
	 public DafProvenance getProvenanceByVersionId(String theId, String versionId);
	 
	 public List<DafProvenance> search(SearchParameterMap theMap);
	 
	 public List<DafProvenance> getProvenanceHistoryById(String theId);

	public List<DafProvenance> getProvenanceByResourceId(List<String> resourceID);
}
