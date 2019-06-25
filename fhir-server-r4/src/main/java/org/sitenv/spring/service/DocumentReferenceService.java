package org.sitenv.spring.service;

import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface DocumentReferenceService {
	
	public DafDocumentReference getDocumentReferenceById(int id);
	
	public DafDocumentReference getDocumentReferenceByVersionId(int theId, String versionId);
		
	public List<DafDocumentReference> search(SearchParameterMap paramMap);
	
	public List<DafDocumentReference> getDocumentReferenceHistoryById(int theId);
}
