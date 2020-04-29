package org.sitenv.spring.service;

import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface DocumentReferenceService {
	
	public DafDocumentReference getDocumentReferenceById(String id);
	
	public DafDocumentReference getDocumentReferenceByVersionId(String theId, String versionId);
		
	public List<DafDocumentReference> search(SearchParameterMap paramMap);
	
	public List<DafDocumentReference> getDocumentReferenceHistoryById(String theId);
}
