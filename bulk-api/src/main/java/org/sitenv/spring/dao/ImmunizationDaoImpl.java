package org.sitenv.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.query.ImmunizationSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("immunizationDao")
public class ImmunizationDaoImpl extends AbstractDao implements ImmunizationDao {


    private static final Logger logger = LoggerFactory.getLogger(ImmunizationDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafImmunization> getAllImmunization() {
        Criteria criteria = getSession().createCriteria(DafImmunization.class);
        return (List<DafImmunization>) criteria.list();
    }

    @Override
    public DafImmunization getImmunizationById(int id) {
        DafImmunization dafImmunization = (DafImmunization) getSession().get(DafImmunization.class, id);
        return dafImmunization;
    }

    @Override
    public List<DafImmunization> getImmunizationByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafImmunization.class, "immunization")
                .createAlias("immunization.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafImmunization> dafImmunizations = criteria.list();
        return dafImmunizations;
    }

    @Override
    public List<DafImmunization> getImmunizationBySearchCriteria(ImmunizationSearchCriteria immunizationSearchCriteria) {
        List<DafImmunization> dafImmunizations = getImmunization(immunizationSearchCriteria);
        return dafImmunizations;
    }

    public List<DafImmunization> getImmunization(ImmunizationSearchCriteria immunizationSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafImmunization.class, "immunization").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (immunizationSearchCriteria.getPatient() != null) {
            criteria.add(Restrictions.eq("immunization.patient.id", immunizationSearchCriteria.getPatient().intValue()));
        }

        if (StringUtils.isNotEmpty(immunizationSearchCriteria.getStatus())) {
            criteria.add(Expression.eq("status", immunizationSearchCriteria.getStatus()).ignoreCase());
        }
        return (List<DafImmunization>) criteria.list();
    }
    
    public List<DafImmunization> getImmunizationForBulkData(List<Integer> patients, Date start){
     	
    	Criteria criteria = getSession().createCriteria(DafImmunization.class, "immunization")
                .createAlias("immunization.patient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    }
    
    	
   

}
