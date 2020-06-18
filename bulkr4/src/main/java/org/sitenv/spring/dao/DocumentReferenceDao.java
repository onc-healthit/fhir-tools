package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.util.SearchParameterMap;

public interface DocumentReferenceDao {
	
	 public DafDocumentReference getDocumentReferenceById(String id);
	 
	 public DafDocumentReference getDocumentReferenceByVersionId(String theId, String versionId);
	 
	 public List<DafDocumentReference> search(SearchParameterMap theMap);
	 
	 public List<DafDocumentReference> getDocumentReferenceHistoryById(String theId);

	public List<DafDocumentReference> getDocumentReferenceForBulkData(StringJoiner patient, Date start);
}
