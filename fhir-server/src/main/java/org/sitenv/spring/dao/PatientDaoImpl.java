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

import ca.uhn.fhir.rest.param.ParamPrefixEnum;

import javax.transaction.Transactional;

import java.util.Date;
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
        if (searchOptions.getFullName() != null && StringUtils.isNotBlank(searchOptions.getFullName().getValue())) {
        	if(searchOptions.getFullName().isContains()){
            Disjunction disjunction = Restrictions.disjunction();
           // disjunction.add(Restrictions.ilike("fullName", searchOptions.getFullName(), MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("familyName", searchOptions.getFullName().getValue(), MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("givenName", searchOptions.getFullName().getValue(), MatchMode.ANYWHERE));
            criteria.add(disjunction);
        	}else{
        		Disjunction disjunction = Restrictions.disjunction();
                disjunction.add(Restrictions.eq("familyName", searchOptions.getFullName().getValue()).ignoreCase());
                disjunction.add(Restrictions.eq("givenName", searchOptions.getFullName().getValue()).ignoreCase());
                criteria.add(disjunction);
        	}
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
        if(searchOptions.getGender()!= null){
        	if(searchOptions.getGender().getMissing()!= null && searchOptions.getGender().getMissing()){
        		 Disjunction disjunction = Restrictions.disjunction();
        		disjunction.add(Restrictions.eq("gender",""));
        		disjunction.add(Restrictions.isNull("gender"));
        		criteria.add(disjunction);
        	}else if(searchOptions.getGender().getMissing()!= null && !searchOptions.getGender().getMissing()){
            //criteria.add(Restrictions.ilike("gender", searchOptions.getGender(), MatchMode.ANYWHERE));
        		//System.out.println("value : "+searchOptions.getGender().getValue());
        		//criteria.add(Restrictions.isNotEmpty("Gender"));
        		Disjunction disjunction = Restrictions.disjunction();
        		disjunction.add(Restrictions.isNotNull("gender"));
        		disjunction.add(Restrictions.ne("gender", ""));
        		criteria.add(disjunction);
        	}
        	if(StringUtils.isNotBlank(searchOptions.getGender().getValue())){
        		criteria.add(Expression.eq("gender", searchOptions.getGender().getValue()).ignoreCase());
        	}
        }
       /* if (StringUtils.isNotBlank(searchOptions.getGender())) {
            //criteria.add(Restrictions.ilike("gender", searchOptions.getGender(), MatchMode.ANYWHERE));
            criteria.add(Expression.eq("gender", searchOptions.getGender()).ignoreCase());
        }*/

        //birthDate
        if (searchOptions.getBirthDate() != null){
    		Date birthDate = null;
    		ParamPrefixEnum paramPrefixEnum = null;
    		if(searchOptions.getBirthDate() != null){
    			paramPrefixEnum = searchOptions.getBirthDate().getPrefix();
    			birthDate = searchOptions.getBirthDate().getValue();
    		}
    		
    		if(paramPrefixEnum != null){
    			if(paramPrefixEnum.getValue() == "lt"){
    				criteria.add(Restrictions.lt("birthDate", birthDate));
    			}else if(paramPrefixEnum.getValue() == "gt"){
    				criteria.add(Restrictions.gt("birthDate", birthDate));
    			}else if(paramPrefixEnum.getValue() == "le"){
    				criteria.add(Restrictions.le("birthDate", birthDate));
    			}else if(paramPrefixEnum.getValue() == "ge"){
    				criteria.add(Restrictions.ge("birthDate", birthDate));
    			}else if(paramPrefixEnum.getValue() == "ne"){
    				criteria.add(Restrictions.ne("birthDate", birthDate));
    			}
    		}else{
				criteria.add(Restrictions.eq("birthDate", birthDate));
			}
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
