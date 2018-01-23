package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafPractitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository("practitionerDao")
public class PractitionerDaoImpl extends AbstractDao implements PractitionerDao {

    private static final Logger logger = LoggerFactory.getLogger(PractitionerDaoImpl.class);

    @Override
    public DafPractitioner getPractitionerById(int id) {
        DafPractitioner dafPractitioner = (DafPractitioner) getSession().get(DafPractitioner.class, id);
        return dafPractitioner;
    }
    
    @Override
    public List<DafPractitioner> getPractitionerForBulkData(List<Integer> patients, Date start){
    	
    	Criteria criteria = getSession().createCriteria(DafPractitioner.class, "practitioner")
                .createAlias("practitioner.patient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    
	}


}
