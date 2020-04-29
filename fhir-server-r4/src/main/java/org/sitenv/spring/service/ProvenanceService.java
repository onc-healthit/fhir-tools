package org.sitenv.spring.service;

import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ProvenanceService {
	
	 public DafProvenance getProvenanceById(String id);
		
		public DafProvenance getProvenanceByVersionId(String theId, String versionId);
			
		public List<DafProvenance> search(SearchParameterMap paramMap);
		
		public List<DafProvenance> getProvenanceHistoryById(String theId);

		public List<DafProvenance> getProvenanceByResourceId(List<String> resourceID);

}
