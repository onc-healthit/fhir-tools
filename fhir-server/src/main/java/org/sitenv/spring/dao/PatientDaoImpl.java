package org.sitenv.spring.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafPatientJson;
import org.sitenv.spring.model.PatientList;
import org.sitenv.spring.query.PatientSearchCriteria;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Prabhushankar.Byrapp on 8/22/2015.
 */
@Repository("patientDao")
public class PatientDaoImpl extends AbstractDao implements PatientDao {


    public DafPatientJson getPatientById(int id) {
        DafPatientJson dafPatientJson = (DafPatientJson) getSession().get(DafPatientJson.class, id);
        return dafPatientJson;

    }

    @Override
    public List<DafPatientJson> getAllPatient() {

        Criteria criteria = getSession().createCriteria(DafPatientJson.class);
        return (List<DafPatientJson>) criteria.list();
    }

    public List<DafPatientJson> getPatientBySearchOption(PatientSearchCriteria criteria) {
        List<DafPatientJson> result = getPatietnt(criteria);
        return result;
    }

    public List<DafPatientJson> getPatietnt(PatientSearchCriteria searchOptions) {

        Criteria criteria = getSession().createCriteria(DafPatientJson.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        //Identifier
        if (searchOptions.getIdentifier() != null) {
            if (searchOptions.getIdentifier().getSystem() != null) {
                criteria.add(Restrictions.sqlRestriction("{alias}.identifier->>'system' = '" + searchOptions.getIdentifier().getSystem() + "'"));
            }
            if (searchOptions.getIdentifier().getValue() != null) {
                criteria.add(Restrictions.sqlRestriction("{alias}.identifier->>'value' = '" + searchOptions.getIdentifier().getValue() + "'"));
            }
        }

        //Full name
        if (StringUtils.isNotBlank(searchOptions.getFullName())) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("fullName", searchOptions.getFullName(), MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("familyName", searchOptions.getFullName(), MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("givenName", searchOptions.getFullName(), MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }

        //familyName
        if (StringUtils.isNotBlank(searchOptions.getFamilyName())) {
            //criteria.add(Restrictions.ilike("familyName", searchOptions.getFamilyName(), MatchMode.ANYWHERE));
            criteria.add(Expression.eq("familyName", searchOptions.getFamilyName()).ignoreCase());
        }

        //givenName
        if (StringUtils.isNotBlank(searchOptions.getGivenName())) {
            //criteria.add(Restrictions.ilike("givenName", searchOptions.getGivenName(), MatchMode.ANYWHERE));
            criteria.add(Expression.eq("givenName", searchOptions.getGivenName()).ignoreCase());
        }

        //language
        if (StringUtils.isNotBlank(searchOptions.getLanguage())) {
            criteria.add(Restrictions.ilike("language", searchOptions.getLanguage(), MatchMode.ANYWHERE));
        }

        //telecom
        if (StringUtils.isNotBlank(searchOptions.getTelecom())) {
            criteria.add(Restrictions.sqlRestriction("{alias}.telecom->>'value' like '%" + searchOptions.getTelecom() + "%'"));
        }

        //gender
        if (StringUtils.isNotBlank(searchOptions.getGender())) {
            //criteria.add(Restrictions.ilike("gender", searchOptions.getGender(), MatchMode.ANYWHERE));
            criteria.add(Expression.eq("gender", searchOptions.getGender()).ignoreCase());
        }

        //birthDate
        if (searchOptions.getBirthDate() != null) {
            criteria.add(Restrictions.eq("birthDate", searchOptions.getBirthDate()));
        }

        //city
        if (StringUtils.isNotBlank(searchOptions.getCity())) {
            criteria.add(Restrictions.ilike("addressCity", searchOptions.getCity(), MatchMode.ANYWHERE));
        }

        //postalCode
        if (StringUtils.isNotBlank(searchOptions.getPostalCode())) {
            criteria.add(Restrictions.ilike("addressZip", searchOptions.getPostalCode(), MatchMode.ANYWHERE));
        }

        //state
        if (StringUtils.isNotBlank(searchOptions.getState())) {
            criteria.add(Restrictions.ilike("addressState", searchOptions.getState(), MatchMode.ANYWHERE));
        }

        //mothersmaidenname
        if (StringUtils.isNotBlank(searchOptions.getMothersmaidenname())) {
            criteria.add(Restrictions.ilike("mothersMaidenName", searchOptions.getMothersmaidenname(), MatchMode.ANYWHERE));
        }

        //race
        if (StringUtils.isNotBlank(searchOptions.getRace())) {
            criteria.add(Restrictions.eq("race", searchOptions.getRace()));
        }

        //ethnicity
        if (StringUtils.isNotBlank(searchOptions.getEthnicity())) {
            criteria.add(Restrictions.eq("ethnicity.code", searchOptions.getEthnicity()));
        }

        return criteria.list();
    }

    @Override
    @Transactional
    public List<PatientList> getPatientsOnAuthorize() {
        Criteria criteria = getSession().createCriteria(PatientList.class);
        return (List<PatientList>) criteria.list();
    }

}
