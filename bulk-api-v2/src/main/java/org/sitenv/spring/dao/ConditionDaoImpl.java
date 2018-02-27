package org.sitenv.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.query.ConditionSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("conditionDao")
public class ConditionDaoImpl extends AbstractDao implements ConditionDao {

    private static final Logger logger = LoggerFactory.getLogger(ConditionDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafCondition> getAllConditions() {
        Criteria criteria = getSession().createCriteria(DafCondition.class);
        return (List<DafCondition>) criteria.list();
    }

    @Override
    public DafCondition getConditionResourceById(int id) {
        DafCondition dafCondition = (DafCondition) getSession().get(DafCondition.class, id);
        return dafCondition;
    }

    @Override
    public List<DafCondition> getConditionByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafCondition.class, "condition")
                .createAlias("condition.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafCondition> dafCondition = criteria.list();
        return dafCondition;
    }

    @Override
    public List<DafCondition> getConditionBySearchOptions(ConditionSearchCriteria conditionSearchCriteria) {
        List<DafCondition> dafCondition = getConditions(conditionSearchCriteria);
        return dafCondition;
    }

    public List<DafCondition> getConditions(ConditionSearchCriteria conditionSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafCondition.class, "condition").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (conditionSearchCriteria.getPatientId() != null) {
            criteria.add(Restrictions.eq("condition.patient.id", conditionSearchCriteria.getPatientId().intValue()));
        }

        if (StringUtils.isNotEmpty(conditionSearchCriteria.getCategory())) {
            criteria.add(Expression.eq("category_code", conditionSearchCriteria.getCategory()).ignoreCase());
        }

        if (conditionSearchCriteria.getStatus() != null && conditionSearchCriteria.getStatus().size() > 0) {
            criteria.add(Restrictions.in("clinical_status", conditionSearchCriteria.getStatus()));
            //criteria.add(Expression.in("clinical_status", conditionSearchCriteria.getStatus()));
        }
        return (List<DafCondition>) criteria.list();
    }

    
    public List<DafCondition> getConditionForBulkData(List<Integer> patients, Date start){
    	
    	Criteria criteria = getSession().createCriteria(DafCondition.class, "condition")
                .createAlias("condition.patient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    }
    
}
