package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ProvenanceDao {
	
     public DafProvenance getProvenanceById(int id);
	 
	 public DafProvenance getProvenanceByVersionId(int theId, String versionId);
	 
	 public List<DafProvenance> search(SearchParameterMap theMap);
	 
	 public List<DafProvenance> getProvenanceHistoryById(int theId);

}
