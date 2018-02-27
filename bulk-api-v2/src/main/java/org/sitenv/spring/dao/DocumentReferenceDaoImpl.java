package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.model.DafDocumentReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Prabhushankar.Byrapp on 8/8/2015.
 */
@Repository("documentReferenceDao")
public class DocumentReferenceDaoImpl extends AbstractDao implements DocumentReferenceDao {

    private static final Logger logger = LoggerFactory.getLogger(DocumentReferenceDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafDocumentReference> getAllDocumentReference() {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class);
        return (List<DafDocumentReference>) criteria.list();
    }

    @Override
    public DafDocumentReference getDocumentReferenceById(int id) {
        DafDocumentReference dafDocumentReference = (DafDocumentReference) getSession().get(DafDocumentReference.class, id);
        return dafDocumentReference;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<DafDocumentReference> getDocumentReferenceByIdentifier(String identifierSystem, String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef")
                .add(Restrictions.like("identifierSystem", identifierSystem))
                .add(Restrictions.like("identifierValue", identifierValue));
        List<DafDocumentReference> dafDocumentReferences = criteria.list();

        return dafDocumentReferences;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<DafDocumentReference> getDocumentReferenceBySubject(String subject) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef")
                .createAlias("docRef.dafPatient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(subject)));
        List<DafDocumentReference> dafDocumentReferences = criteria.list();

        return dafDocumentReferences;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafDocumentReference> getDocumentReferenceBySubjectIdentifier(String identifierSystem, String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef")
                .add(Restrictions.like("identifierSystem", identifierSystem))
                .add(Restrictions.like("identifierValue", identifierValue));
        List<DafDocumentReference> dafDocumentReferences = criteria.list();
        return dafDocumentReferences;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<DafDocumentReference> getDocumentReferenceByCreatedDate(String comparatorStr, Date createdDate) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef");
        if (comparatorStr == "" || comparatorStr == "=") {
            criteria.add(Restrictions.eq("created", createdDate));
        } else if (comparatorStr == ">=") {
            criteria.add(Restrictions.ge("created", createdDate));
        } else if (comparatorStr == "<=") {
            criteria.add(Restrictions.le("created", createdDate));
        } else if (comparatorStr == ">") {
            criteria.add(Restrictions.gt("created", createdDate));
        } else if (comparatorStr == "<") {
            criteria.add(Restrictions.lt("created", createdDate));
        }

        List<DafDocumentReference> dafDocumentReferences = criteria.list();
        return dafDocumentReferences;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafDocumentReference> getDocumentReferenceByCreatedDateOptional(String patientId, String comparatorStr, Date createdDate) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef");
        if (comparatorStr == "" || comparatorStr == "=") {
            criteria.add(Restrictions.eq("created", createdDate))
                    .add(Restrictions.eq("docRef.dafPatient.id", Integer.valueOf(patientId)));
        } else if (comparatorStr == ">=") {
            criteria.add(Restrictions.ge("created", createdDate))
                    .add(Restrictions.eq("docRef.dafPatient.id", Integer.valueOf(patientId)));
        } else if (comparatorStr == "<=") {
            criteria.add(Restrictions.le("created", createdDate))
                    .add(Restrictions.eq("docRef.dafPatient.id", Integer.valueOf(patientId)));
        } else if (comparatorStr == ">") {
            criteria.add(Restrictions.gt("created", createdDate))
                    .add(Restrictions.eq("docRef.dafPatient.id", Integer.valueOf(patientId)));
        } else if (comparatorStr == "<") {
            criteria.add(Restrictions.lt("created", createdDate))
                    .add(Restrictions.eq("docRef.dafPatient.id", Integer.valueOf(patientId)));
        }

        List<DafDocumentReference> dafDocumentReferences = criteria.list();
        return dafDocumentReferences;
    }


    public List<DafDocumentReference> getDocumentReferenceByPeriod(String fromComparatorStr, Date fromDate, String toComparatorStr, Date toDate) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef")
                .createAlias("docRef.dafContextPeriod", "dp");

        if (fromComparatorStr == "" || fromComparatorStr == "=") {
            criteria.add(Restrictions.eq("dp.start", fromDate));
        } else if (fromComparatorStr == ">=") {
            criteria.add(Restrictions.ge("dp.start", fromDate));
        } else if (fromComparatorStr == "<=") {
            criteria.add(Restrictions.le("dp.start", fromDate));
        } else if (fromComparatorStr == ">") {
            criteria.add(Restrictions.gt("dp.start", fromDate));
        } else if (fromComparatorStr == "<") {
            criteria.add(Restrictions.lt("dp.start", fromDate));
        }

        if (toDate != null) {
            if (toComparatorStr == "" || toComparatorStr == "=") {
                criteria.add(Restrictions.eq("dp.end", toDate));
            } else if (toComparatorStr == ">=") {
                criteria.add(Restrictions.ge("dp.end", toDate));
            } else if (toComparatorStr == "<=") {
                criteria.add(Restrictions.le("dp.end", toDate));
            } else if (toComparatorStr == ">") {
                criteria.add(Restrictions.gt("dp.end", toDate));
            } else if (toComparatorStr == "<") {
                criteria.add(Restrictions.lt("dp.end", toDate));
            }
        }

        List<DafDocumentReference> dafDocumentReferences = criteria.list();
        return dafDocumentReferences;

    }

    @Override
    public List<DafDocumentReference> getDocumentReferenceByType(
            String identifierSystem, String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef")
                .createAlias("docRef.dafDocumentTypeCodes", "dp")
                .add(Restrictions.like("dp.system", identifierSystem))
                .add(Restrictions.like("dp.code", identifierValue));
        List<DafDocumentReference> dafDocumentReferences = criteria.list();
        return dafDocumentReferences;
    }

    @Override
    public List<DafDocumentReference> getDocumentReferenceByTypeOptional(
            String patientId, String identifierSystem, String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef")
                .createAlias("docRef.dafDocumentTypeCodes", "dp")
                .add(Restrictions.eq("docRef.dafPatient.id", Integer.valueOf(patientId)))
                .add(Restrictions.like("dp.system", identifierSystem))
                .add(Restrictions.like("dp.code", identifierValue));
        List<DafDocumentReference> dafDocumentReferences = criteria.list();
        return dafDocumentReferences;
    }

    @Override
    public List<DafDocumentReference> getDocumentReferenceByStatus(String status) {
        Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef")
                .add(Restrictions.like("status", status).ignoreCase());

        List<DafDocumentReference> dafDocumentReferences = criteria.list();

        return dafDocumentReferences;
    }
    
    public List<DafDocumentReference> getDocumentReferenceForBulkData(List<Integer> patients, Date start){
       	
    	Criteria criteria = getSession().createCriteria(DafDocumentReference.class, "docRef")
                .createAlias("docRef.dafPatient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    }
    
  

}
