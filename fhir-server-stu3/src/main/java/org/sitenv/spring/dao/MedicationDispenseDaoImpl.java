package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafMedicationDispense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("medicationDispenseDao")
public class MedicationDispenseDaoImpl extends AbstractDao implements MedicationDispenseDao {

    private static final Logger logger = LoggerFactory.getLogger(MedicationDispenseDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationDispense> getAllMedicationDispense() {
        Criteria criteria = getSession().createCriteria(DafMedicationDispense.class);
        return (List<DafMedicationDispense>) criteria.list();
    }

    @Override
    public DafMedicationDispense getMedicationDispenseResourceById(int id) {
        DafMedicationDispense dafMedicationDispense = (DafMedicationDispense) getSession().get(DafMedicationDispense.class, id);
        return dafMedicationDispense;
    }

    @Override
    public List<DafMedicationDispense> getMedicationDispenseByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafMedicationDispense.class, "medDispense")
                .createAlias("medDispense.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafMedicationDispense> dafMedicationDispense = criteria.list();
        return dafMedicationDispense;
    }

    @Override
    public List<DafMedicationDispense> getMedicationDispenseByCode(String code) {
        Criteria criteria = getSession().createCriteria(DafMedicationDispense.class, "medDispense")
                .createAlias("medDispense.medicationreference", "dp")
                .add(Restrictions.eq("dp.med_code", code));
        List<DafMedicationDispense> dafMedicationDispense = criteria.list();
        return dafMedicationDispense;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedicationDispense> getMedicationDispenseByIdentifier(String identifierSystem, String identifierValue) {
        Criteria criteria = getSession().createCriteria(DafMedicationDispense.class, "medDispense")
                .add(Restrictions.like("identifier_system", identifierSystem).ignoreCase())
                .add(Restrictions.like("identifier_value", identifierValue).ignoreCase());
        List<DafMedicationDispense> dafMedicationDispense = criteria.list();
        return dafMedicationDispense;
    }

    @Override
    public List<DafMedicationDispense> getMedicationDispenseByMedication(String medication) {
        Criteria criteria = getSession().createCriteria(DafMedicationDispense.class, "medDispense")
                .createAlias("medDispense.medicationreference", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(medication)));
        List<DafMedicationDispense> dafMedicationDispense = criteria.list();
        return dafMedicationDispense;
    }

    @Override
    public List<DafMedicationDispense> getMedicationDispenseByStatus(String status) {
        Criteria criteria = getSession().createCriteria(DafMedicationDispense.class, "medDispense")
                .add(Restrictions.like("status", status).ignoreCase());

        List<DafMedicationDispense> dafMedicationDispense = criteria.list();

        return dafMedicationDispense;
    }

	@Override
	public List<DafMedicationDispense> getMedicationDispenseForBulkData(List<Integer> patients, Date start) {
		Criteria criteria = getSession().createCriteria(DafMedicationDispense.class, "medDispense")
                .createAlias("medDispense.patient", "dp");
        if(patients!=null) {
            criteria.add(Restrictions.in("dp.id", patients));
        }
        if(start != null) {
            criteria.add(Restrictions.ge("updated", start));
        }
        
        return criteria.list();
	}

}
