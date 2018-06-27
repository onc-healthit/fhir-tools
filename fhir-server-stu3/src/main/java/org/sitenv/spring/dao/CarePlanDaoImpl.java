package org.sitenv.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.model.DafCarePlanParticipant;
import org.sitenv.spring.query.CarePlanSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("carPlanDao")
public class CarePlanDaoImpl extends AbstractDao implements CarePlanDao {

    private static final Logger logger = LoggerFactory.getLogger(CarePlanDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafCarePlan> getAllCarePlan() {
        Criteria criteria = getSession().createCriteria(DafCarePlan.class);
        return (List<DafCarePlan>) criteria.list();
    }

    @Override
    public DafCarePlan getCarePlanById(int id) {
        DafCarePlan dafCarePlan = (DafCarePlan) getSession().get(DafCarePlan.class, id);
        return dafCarePlan;
    }

    @Override
    public List<DafCarePlan> getCarePlanByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafCarePlan.class, "careplan")
                .createAlias("careplan.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafCarePlan> dafCarePlan = criteria.list();
        return dafCarePlan;
    }

    @Override
    public List<DafCarePlan> getCarePlanBySearchCriteria(CarePlanSearchCriteria carePlanSearchCriteria) {
        List<DafCarePlan> dafCarePlan = getCarePlan(carePlanSearchCriteria);
        return dafCarePlan;
    }

    public List<DafCarePlan> getCarePlan(CarePlanSearchCriteria carePlanSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafCarePlan.class, "careplan").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (carePlanSearchCriteria.getPatient() != null) {
            criteria.add(Restrictions.eq("careplan.patient.id", carePlanSearchCriteria.getPatient().intValue()));
        }

        if (StringUtils.isNotEmpty(carePlanSearchCriteria.getCat_code())) {
            criteria.add(Restrictions.eq("cat_code", carePlanSearchCriteria.getCat_code()));
        }

        if (StringUtils.isNotEmpty(carePlanSearchCriteria.getStatus())) {
            criteria.add(Expression.eq("status", carePlanSearchCriteria.getStatus()).ignoreCase());
        }
        return (List<DafCarePlan>) criteria.list();
    }

    @Override
    public List<DafCarePlanParticipant> getCarePlanparticipantByCarePlan(int id) {
        Criteria criteria = getSession().createCriteria(DafCarePlanParticipant.class, "dp")
                .add(Restrictions.eq("careplan", id));
        List<DafCarePlanParticipant> dafCarePlan = criteria.list();
        return dafCarePlan;
    }

}
