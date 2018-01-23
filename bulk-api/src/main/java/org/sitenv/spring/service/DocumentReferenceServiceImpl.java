package org.sitenv.spring.service;

import org.sitenv.spring.dao.DocumentReferenceDao;
import org.sitenv.spring.model.DafDocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Prabhushankar.Byrapp on 8/7/2015.
 */

@Service("documentReferenceService")
@Transactional
public class DocumentReferenceServiceImpl implements DocumentReferenceService {

    @Autowired
    private DocumentReferenceDao documentReferenceDao;


    @Override
    @Transactional
    public List<DafDocumentReference> getAllDocumentReference() {
        return this.documentReferenceDao.getAllDocumentReference();
    }


    @Override
    @Transactional
    public DafDocumentReference getDocumentReferenceById(int id) {
        return this.documentReferenceDao.getDocumentReferenceById(id);
    }


    @Override
    @Transactional
    public List<DafDocumentReference> getDocumentReferenceByIdentifier(String identifierSystem, String identifierValue) {
        return this.documentReferenceDao.getDocumentReferenceByIdentifier(identifierSystem, identifierValue);
    }

    @Override
    @Transactional
    public List<DafDocumentReference> getDocumentReferenceBySubject(String subject) {
        return this.documentReferenceDao.getDocumentReferenceBySubject(subject);
    }

    @Override
    @Transactional
    public List<DafDocumentReference> getDocumentReferenceBySubjectIdentifier(String identifierSystem, String identifierValue) {
        return this.documentReferenceDao.getDocumentReferenceBySubjectIdentifier(identifierSystem, identifierValue);
    }


    @Override
    @Transactional
    public List<DafDocumentReference> getDocumentReferenceByCreatedDate(String comparatorStr, Date createdDate) {
        return this.documentReferenceDao.getDocumentReferenceByCreatedDate(comparatorStr, createdDate);
    }

    @Override
    @Transactional
    public List<DafDocumentReference> getDocumentReferenceByCreatedDateOptional(String patientId, String comparatorStr, Date createdDate) {
        return this.documentReferenceDao.getDocumentReferenceByCreatedDateOptional(patientId, comparatorStr, createdDate);
    }

    public List<DafDocumentReference> getDocumentReferenceByPeriod(String fromComparatorStr, Date fromDate, String toComparatorStr, Date toDate) {
        return this.documentReferenceDao.getDocumentReferenceByPeriod(fromComparatorStr, fromDate, toComparatorStr, toDate);
    }


    @Override
    public List<DafDocumentReference> getDocumentReferenceByType(
            String identifierSystem, String identifierValue) {
        return this.documentReferenceDao.getDocumentReferenceByType(identifierSystem, identifierValue);
    }

    @Override
    public List<DafDocumentReference> getDocumentReferenceByTypeOptional(
            String patientId, String identifierSystem, String identifierValue) {
        return this.documentReferenceDao.getDocumentReferenceByTypeOptional(patientId, identifierSystem, identifierValue);
    }


    @Override
    public List<DafDocumentReference> getDocumentReferenceByStatus(String status) {
        return this.documentReferenceDao.getDocumentReferenceByStatus(status);
    }
    
    @Override
    public List<DafDocumentReference> getDocumentReferenceForBulkData(List<Integer> patients, Date start){
    	return this.documentReferenceDao.getDocumentReferenceForBulkData(patients, start);
    }

}
