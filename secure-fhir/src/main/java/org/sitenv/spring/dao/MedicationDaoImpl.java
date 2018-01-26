package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafMedication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("medicationDao")
public class MedicationDaoImpl extends AbstractDao implements MedicationDao {

    private static final Logger logger = LoggerFactory.getLogger(MedicationDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafMedication> getAllMedication() {
        Criteria criteria = getSession().createCriteria(DafMedication.class);
        return (List<DafMedication>) criteria.list();
    }

    @Override
    public DafMedication getMedicationResourceById(int id) {
        DafMedication dafMedication = (DafMedication) getSession().get(DafMedication.class, id);
        return dafMedication;
    }

    @Override
    public List<DafMedication> getMedicationByCode(String code) {
        Criteria criteria = getSession().createCriteria(DafMedication.class, "med_code")
                .add(Restrictions.like("med_code", code).ignoreCase());
        List<DafMedication> dafMedication = criteria.list();

        return dafMedication;
    }
    
    @Override
    public List<DafMedication> getMedicationForBulkData(List<Integer> patients, Date start){
    	
    	Criteria criteria = getSession().createCriteria(DafMedication.class, "medication");
               // .createAlias("medication.patient", "dp");
    			/*if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}*/
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    
	}

}
