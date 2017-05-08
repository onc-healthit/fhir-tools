package org.sitenv.spring.dao;

import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.query.ObservationSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("observationDao")
public class ObservationDaoImpl extends AbstractDao implements ObservationDao {

    private static final Logger logger = LoggerFactory.getLogger(ObservationDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafObservation> getAllObservations() {
        Criteria criteria = getSession().createCriteria(DafObservation.class);
        return (List<DafObservation>) criteria.list();
    }

    @Override
    public DafObservation getObservationResourceById(int id) {
        DafObservation dafObservation = (DafObservation) getSession().get(DafObservation.class, id);
        return dafObservation;
    }
    
    @Override
    public DafObservation getObservationResourceByIdandCategory(int id,String category) {
    	Criteria criteria = getSession().createCriteria(DafObservation.class)
                .add(Restrictions.eq("id", id))
                .add(Restrictions.like("cat_code", category));
        DafObservation dafObservation = (DafObservation) criteria.uniqueResult();
        return dafObservation;
    }

    @Override
    public List<DafObservation> getObservationByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafObservation.class, "observation")
                .createAlias("observation.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafObservation> dafObservation = criteria.list();
        return dafObservation;
    }

    @Override
    public List<DafObservation> getObservationBySearchCriteria(ObservationSearchCriteria observationSearchCriteria) {
        List<DafObservation> dafObservation = getObservations(observationSearchCriteria);
        return dafObservation;
    }

    public List<DafObservation> getObservations(ObservationSearchCriteria observationSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafObservation.class, "observation").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (observationSearchCriteria.getPatient() != null) {
            criteria.add(Restrictions.eq("observation.patient.id", observationSearchCriteria.getPatient().intValue()));
        }

        if (StringUtils.isNotEmpty(observationSearchCriteria.getCategory())) {
            criteria.add(Restrictions.like("cat_code", observationSearchCriteria.getCategory()));
        }

        if (observationSearchCriteria.getCode() != null && observationSearchCriteria.getCode().size() > 0) {
            criteria.add(Restrictions.in("code", observationSearchCriteria.getCode()));
        }

        if (observationSearchCriteria.getRangedates() != null) {
            Date from = null;
            Date to = null;
            ParamPrefixEnum fromParamPrefixEnum = null;
            ParamPrefixEnum toParamPrefixEnum = null;
            if (observationSearchCriteria.getRangedates().getLowerBoundAsInstant() != null) {
                fromParamPrefixEnum = observationSearchCriteria.getRangedates().getLowerBound().getPrefix();
                from = observationSearchCriteria.getRangedates().getLowerBoundAsInstant();
            }
            if (observationSearchCriteria.getRangedates().getUpperBoundAsInstant() != null) {
                toParamPrefixEnum = observationSearchCriteria.getRangedates().getUpperBound().getPrefix();
                to = observationSearchCriteria.getRangedates().getUpperBoundAsInstant();
            }

            if (from == null) {
                if (toParamPrefixEnum.getValue() == "lt") {
                    criteria.add(Restrictions.lt("effectiveDate", to));
                } else if (toParamPrefixEnum.getValue() == "gt") {
                    criteria.add(Restrictions.gt("effectiveDate", to));
                } else if (toParamPrefixEnum.getValue() == "le") {
                    criteria.add(Restrictions.le("effectiveDate", to));
                } else if (toParamPrefixEnum.getValue() == "ge") {
                    criteria.add(Restrictions.ge("effectiveDate", to));
                }
            }
            if (to == null) {
                if (fromParamPrefixEnum.getValue() == "lt") {
                    criteria.add(Restrictions.lt("effectiveDate", from));
                } else if (fromParamPrefixEnum.getValue() == "gt") {
                    criteria.add(Restrictions.gt("effectiveDate", from));
                } else if (fromParamPrefixEnum.getValue() == "le") {
                    criteria.add(Restrictions.le("effectiveDate", from));
                } else if (fromParamPrefixEnum.getValue() == "ge") {
                    criteria.add(Restrictions.ge("effectiveDate", from));
                }
            }
            if (from != null && to != null) {
                criteria.add(Restrictions.between("effectiveDate", from, to));
            }
        }
        return (List<DafObservation>) criteria.list();
    }

    @Override
    public List<DafObservation> getObservationByBPCode(String BPCode) {
        Criteria criteria = getSession().createCriteria(DafObservation.class)
                .add(Restrictions.eq("bp_code", BPCode));
        List<DafObservation> dafObservation = (List<DafObservation>) criteria.list();
        return dafObservation;
    }
}
