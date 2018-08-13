package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.query.MedicationRequestSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("medicationRequestDao")
public class MedicationRequestDaoImpl extends AbstractDao implements MedicationRequestDao {

    private static final Logger logger = LoggerFactory.getLogger(MedicationRequestDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationRequest> getAllMedicationRequest() {
        Criteria criteria = getSession().createCriteria(DafMedicationRequest.class);
        return (List<DafMedicationRequest>) criteria.list();
    }

    @Override
    public DafMedicationRequest getMedicationRequestResourceById(int id) {
        DafMedicationRequest dafMedicationRequest = (DafMedicationRequest) getSession().get(DafMedicationRequest.class,
                id);
        return dafMedicationRequest;
    }

    @Override
    public List<DafMedicationRequest> getMedicationRequestBySearchCriteria(
            MedicationRequestSearchCriteria medicationRequestSearchCriteria) {
        List<DafMedicationRequest> dafMedicationRequest = getMedicationRequest(medicationRequestSearchCriteria);
        return dafMedicationRequest;
    }

    public List<DafMedicationRequest> getMedicationRequest(
            MedicationRequestSearchCriteria medicationRequestSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafMedicationRequest.class, "medicationRequest")
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (medicationRequestSearchCriteria.getSubject() != null) {
            criteria.add(Restrictions.eq("medicationRequest.subject.id", medicationRequestSearchCriteria.getSubject().intValue()));
        }

        return (List<DafMedicationRequest>) criteria.list();
    }

	@Override
	public List<DafMedicationRequest> getMedicationRequestForBulkData(List<Integer> patients, Date start) {
		Criteria criteria = getSession().createCriteria(DafMedicationRequest.class, "medRequest")
                .createAlias("medRequest.patient", "dp");
        if(patients!=null) {
            criteria.add(Restrictions.in("dp.id", patients));
        }
        if(start != null) {
            criteria.add(Restrictions.ge("updated", start));
        }
        
        return criteria.list();
	}
}
