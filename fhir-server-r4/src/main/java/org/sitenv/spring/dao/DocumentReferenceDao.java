package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface DocumentReferenceDao {
	
	 public DafDocumentReference getDocumentReferenceById(int id);
	 
	 public DafDocumentReference getDocumentReferenceByVersionId(int theId, String versionId);
	 
	 public List<DafDocumentReference> search(SearchParameterMap theMap);
	 
	 public List<DafDocumentReference> getDocumentReferenceHistoryById(int theId);
}
