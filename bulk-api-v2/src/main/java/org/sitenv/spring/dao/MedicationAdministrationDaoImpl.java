package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafMedicationAdministration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("medicationAdministrationDao")
public class MedicationAdministrationDaoImpl extends AbstractDao implements MedicationAdministrationDao {

    private static final Logger logger = LoggerFactory.getLogger(MedicationAdministrationDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationAdministration> getAllMedicationAdministration() {
        Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class);
        return (List<DafMedicationAdministration>) criteria.list();
    }

    @Override
    public DafMedicationAdministration getMedicationAdministrationResourceById(int id) {
        DafMedicationAdministration dafMedicationAdministration = (DafMedicationAdministration) getSession().get(DafMedicationAdministration.class, id);
        return dafMedicationAdministration;
    }

    @Override
    public List<DafMedicationAdministration> getMedicationAdministrationByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class, "medAdministration")
                .createAlias("medAdministration.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafMedicationAdministration> dafMedicationAdministration = criteria.list();
        return dafMedicationAdministration;
    }

    @Override
    public List<DafMedicationAdministration> getMedicationAdministrationByCode(String code) {
        Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class, "medAdministration")
                .createAlias("medAdministration.medicationreference", "dp")
                .add(Restrictions.eq("dp.med_code", code));
        List<DafMedicationAdministration> dafMedicationAdministration = criteria.list();
        return dafMedicationAdministration;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationAdministration> getMedicationAdministrationByIdentifier(String identifierSystem, String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class, "medAdministration")
                .add(Restrictions.like("identifier_system", identifierSystem).ignoreCase())
                .add(Restrictions.like("identifier_value", identifierValue).ignoreCase());
        List<DafMedicationAdministration> dafMedicationAdministration = criteria.list();
        return dafMedicationAdministration;
    }

    @Override
    public List<DafMedicationAdministration> getMedicationAdministrationByMedication(String medication) {
        Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class, "medAdministration")
                .createAlias("medAdministration.medicationreference", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(medication)));
        List<DafMedicationAdministration> dafMedicationAdministration = criteria.list();
        return dafMedicationAdministration;
    }

    @Override
    public List<DafMedicationAdministration> getMedicationAdministrationByStatus(String status) {
        Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class, "medAdministration")
                .add(Restrictions.like("status", status).ignoreCase());

        List<DafMedicationAdministration> dafMedicationAdministration = criteria.list();

        return dafMedicationAdministration;
    }
    
    @Override
    public List<DafMedicationAdministration> getMedicationAdministrationForBulkData(List<Integer> patients, Date start){
    	
    	Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class, "medAdministration")
                .createAlias("medAdministration.patient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
   
    }

}
