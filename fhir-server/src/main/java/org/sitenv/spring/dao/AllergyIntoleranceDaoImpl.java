package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("allergyIntoleranceDao")
public class AllergyIntoleranceDaoImpl extends AbstractDao implements AllergyIntoleranceDao {

    private static final Logger logger = LoggerFactory.getLogger(AllergyIntoleranceDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafAllergyIntolerance> getAllAllergyIntolerance() {
        Criteria criteria = getSession().createCriteria(DafAllergyIntolerance.class);
        return (List<DafAllergyIntolerance>) criteria.list();
    }

    @Override
    public DafAllergyIntolerance getAllergyIntoleranceResourceById(int id) {
        DafAllergyIntolerance dafAllergyIntolerance = (DafAllergyIntolerance) getSession().get(DafAllergyIntolerance.class, id);
        return dafAllergyIntolerance;
    }

    @Override
    public List<DafAllergyIntolerance> getAllergyIntoleranceByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafAllergyIntolerance.class, "allergyIntolerance")
                .createAlias("allergyIntolerance.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafAllergyIntolerance> dafAllergyIntolerance = criteria.list();
        return dafAllergyIntolerance;
    }

}
