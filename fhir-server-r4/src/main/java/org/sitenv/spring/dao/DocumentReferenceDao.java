package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface DocumentReferenceDao {
	
	 public DafDocumentReference getDocumentReferenceById(String id);
	 
	 public DafDocumentReference getDocumentReferenceByVersionId(String theId, String versionId);
	 
	 public List<DafDocumentReference> search(SearchParameterMap theMap);
	 
	 public List<DafDocumentReference> getDocumentReferenceHistoryById(String theId);
}
