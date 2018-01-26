package org.sitenv.spring.dao;

import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.model.DafGoals;
import org.sitenv.spring.query.GoalsSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("goalsDao")
public class GoalsDaoImpl extends AbstractDao implements GoalsDao {

    private static final Logger logger = LoggerFactory.getLogger(GoalsDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafGoals> getAllGoals() {
        Criteria criteria = getSession().createCriteria(DafGoals.class);
        return (List<DafGoals>) criteria.list();
    }

    @Override
    public DafGoals getGoalsById(int id) {
        DafGoals dafGoals = (DafGoals) getSession().get(DafGoals.class, id);
        return dafGoals;
    }

    @Override
    public List<DafGoals> getGoalsByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafGoals.class, "goals")
                .createAlias("goals.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafGoals> dafGoals = criteria.list();
        return dafGoals;
    }

    @Override
    public List<DafGoals> getGoalsBySearchCriteria(GoalsSearchCriteria goalsSearchCriteria) {
        List<DafGoals> dafGoals = getGoals(goalsSearchCriteria);
        return dafGoals;
    }

    public List<DafGoals> getGoals(GoalsSearchCriteria goalsSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafGoals.class, "goals").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (goalsSearchCriteria.getPatient() != null) {
            criteria.add(Restrictions.eq("goals.patient.id", goalsSearchCriteria.getPatient().intValue()));
        }

        if (StringUtils.isNotEmpty(goalsSearchCriteria.getStatus())) {
            criteria.add(Expression.eq("status", goalsSearchCriteria.getStatus()).ignoreCase());
        }

        if (goalsSearchCriteria.getDate() != null) {
            Date from = null;
            Date to = null;
            ParamPrefixEnum fromParamPrefixEnum = null;
            ParamPrefixEnum toParamPrefixEnum = null;
            if (goalsSearchCriteria.getDate().getLowerBoundAsInstant() != null) {
                fromParamPrefixEnum = goalsSearchCriteria.getDate().getLowerBound().getPrefix();
                from = goalsSearchCriteria.getDate().getLowerBoundAsInstant();
            }
            if (goalsSearchCriteria.getDate().getUpperBoundAsInstant() != null) {
                toParamPrefixEnum = goalsSearchCriteria.getDate().getUpperBound().getPrefix();
                to = goalsSearchCriteria.getDate().getUpperBoundAsInstant();
            }

            if (from == null) {
                if (toParamPrefixEnum.getValue() == "lt") {
                    criteria.add(Restrictions.lt("date", to));
                } else if (toParamPrefixEnum.getValue() == "gt") {
                    criteria.add(Restrictions.gt("date", to));
                } else if (toParamPrefixEnum.getValue() == "le") {
                    criteria.add(Restrictions.le("date", to));
                } else if (toParamPrefixEnum.getValue() == "ge") {
                    criteria.add(Restrictions.ge("date", to));
                }
            }
            if (to == null) {
                if (fromParamPrefixEnum.getValue() == "lt") {
                    criteria.add(Restrictions.lt("date", from));
                } else if (fromParamPrefixEnum.getValue() == "gt") {
                    criteria.add(Restrictions.gt("date", from));
                } else if (fromParamPrefixEnum.getValue() == "le") {
                    criteria.add(Restrictions.le("date", from));
                } else if (fromParamPrefixEnum.getValue() == "ge") {
                    criteria.add(Restrictions.ge("date", from));
                }
            }
            if (from != null && to != null) {
                criteria.add(Restrictions.between("date", from, to));
            }
        }
        return (List<DafGoals>) criteria.list();
    }
    
    public List<DafGoals> getGoalsForBulkData(List<Integer> patients, Date start){
    	
    	Criteria criteria = getSession().createCriteria(DafGoals.class, "goals")
                .createAlias("goals.patient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    }
}
