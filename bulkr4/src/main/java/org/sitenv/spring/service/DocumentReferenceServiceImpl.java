package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.dao.DocumentReferenceDao;
import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("DocumentReferenceService")
@Transactional
public class DocumentReferenceServiceImpl implements DocumentReferenceService {
	
	@Autowired
    private DocumentReferenceDao documentReferenceDao;
	
	@Override
    @Transactional
    public DafDocumentReference getDocumentReferenceById(String id) {
        return this.documentReferenceDao.getDocumentReferenceById(id);
    }
	
	@Override
	@Transactional
	public DafDocumentReference getDocumentReferenceByVersionId(String theId, String versionId) {
		return this.documentReferenceDao.getDocumentReferenceByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafDocumentReference> search(SearchParameterMap paramMap){
        return this.documentReferenceDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafDocumentReference> getDocumentReferenceHistoryById(String theId) {
	   return this.documentReferenceDao.getDocumentReferenceHistoryById(theId);
   }

	@Override
	public List<DafDocumentReference> getDocumentReferenceForBulkData(StringJoiner patients, Date start) {
		return this.documentReferenceDao.getDocumentReferenceForBulkData(patients, start);

	}
}
   

