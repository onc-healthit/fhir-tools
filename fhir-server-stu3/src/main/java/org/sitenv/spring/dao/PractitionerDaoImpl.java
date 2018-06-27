package org.sitenv.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.query.PractitionerSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("practitionerDao")
public class PractitionerDaoImpl extends AbstractDao implements PractitionerDao {

    private static final Logger logger = LoggerFactory.getLogger(PractitionerDaoImpl.class);

    @Override
    public DafPractitioner getPractitionerById(int id) {
        DafPractitioner dafPractitioner = (DafPractitioner) getSession().get(DafPractitioner.class, id);
        return dafPractitioner;
    }

    @Override
    public List<DafPractitioner> getAllPractitioners() {
        Criteria criteria = getSession().createCriteria(DafPractitioner.class);
        return (List<DafPractitioner>) criteria.list();
    }

    @Override
    public List<DafPractitioner> getPractitionerBySearchCriteria(PractitionerSearchCriteria practitionerSearchCriteria) {
        List<DafPractitioner> dafPractitioners = getPractitioner(practitionerSearchCriteria);
        return dafPractitioners;
    }

    public List<DafPractitioner> getPractitioner(PractitionerSearchCriteria practitionerSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafPractitioner.class, "practitioner").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        //Identifier
        if (practitionerSearchCriteria.getIdentifier() != null) {
            if (practitionerSearchCriteria.getIdentifier().getSystem() != null) {
                criteria.add(Restrictions.sqlRestriction("{alias}.identifier->>'system' = '" + practitionerSearchCriteria.getIdentifier().getSystem() + "'"));
            }
            if (practitionerSearchCriteria.getIdentifier().getValue() != null) {
                criteria.add(Restrictions.sqlRestriction("{alias}.identifier->>'value' = '" + practitionerSearchCriteria.getIdentifier().getValue() + "'"));
            }
        }

        if (StringUtils.isNotEmpty(practitionerSearchCriteria.getFamilyName())) {
            criteria.add(Restrictions.eq("familyName", practitionerSearchCriteria.getFamilyName()).ignoreCase());
        }

        if (StringUtils.isNotEmpty(practitionerSearchCriteria.getGivenName())) {
            criteria.add(Restrictions.eq("givenName", practitionerSearchCriteria.getGivenName()).ignoreCase());
        }

        return (List<DafPractitioner>) criteria.list();
    }

}
