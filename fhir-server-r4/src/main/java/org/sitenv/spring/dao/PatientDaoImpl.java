package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("patientDao")
public class PatientDaoImpl extends AbstractDao implements PatientDao {
	
	/**
	 * This method builds criteria for fetching patient record by id.
	 * @param id : ID of the resource
	 * @return : DafPatient object
	 */
	@Override
	public DafPatient getPatientById(String id) {
		List<DafPatient> list = getSession().createNativeQuery(
			"select * from patient where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafPatient.class)
				.getResultList();
		return list.get(0);
    }
	
	/**
	 * This method builds criteria for fetching particular version of the patient record by id.
	 * @param theId : ID of the patient
	 * @param versionId : version of the patient record
	 * @return : DafPatient object
	 */
	@Override
	public DafPatient getPatientByVersionId(String theId, String versionId) {
		DafPatient list = getSession().createNativeQuery(
			"select * from patient where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafPatient.class)
				.getSingleResult();
			return list;
	}
	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafPatient object
	 */
	@SuppressWarnings("unchecked")
	public List<DafPatient> search(SearchParameterMap theMap) {
        Criteria criteria = getSession().createCriteria(DafPatient.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);

        //build criteria for name
        buildNameCriteria(theMap, criteria);

        //build criteria for given
        buildGivenNameCriteria(theMap, criteria);

        //build criteria for family
        buildFamilyNameCriteria(theMap, criteria);

        //build criteria for gender
        buildGenderCriteria(theMap, criteria);
        
        //build criteria for address
        buildAddressCriteria(theMap, criteria);
        
        //build criteria for address-city
        buildAddressCityCriteria(theMap, criteria);
        
        //build criteria for address-state
        buildAddressStateCriteria(theMap, criteria);
        
        //build criteria for address-country
        buildAddressCountryCriteria(theMap, criteria);
        
        //build criteria for address-postalcode
        buildAddressPostalcodeCriteria(theMap, criteria);
        
        //build criteria for birthDate
        buildBirthDateCriteria(theMap, criteria);
        
        //build criteria for active
        buildActiveCriteria(theMap, criteria);
        
        //build criteria for telecom
        buildTelecomCriteria(theMap, criteria);
        
        //build criteria for name
        buildNameCriteria(theMap, criteria);
        
        //build criteria for language
        buildLanguageCriteria(theMap, criteria);
        
        //build criterid for link
        buildLinkCriteria(theMap, criteria);
        
        //build criterid for organization
        buildOrganizationCriteria(theMap, criteria);
        
        //build criterid for deceasedBoolean
        buildDeceasedBooleanCriteria(theMap, criteria);
        
        //build criterid for address-use
        buildAddressUseCriteria(theMap, criteria);
          
        return criteria.list();
    }
	
	/**
	 * This method builds criteria for patient id
	 * @param theMap : search parameter "_id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("_id");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    StringParam id = (StringParam) params;
                    if (id.getValue() != null) {
        				criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id.getValue()+"'"));

                    }
                }
            }
        }
	}
	
	/**
	 * This method builds criteria for patient identifier
	 * @param theMap : search parameter "identifier"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdentifierCriteria(SearchParameterMap theMap, Criteria criteria) {
	    List<List<? extends IQueryParameterType>> list = theMap.get("identifier");

	    if (list != null) {
			StringBuffer inSql = new StringBuffer();
	        for (List<? extends IQueryParameterType> values : list) {
	            for (IQueryParameterType params : values) {
	                TokenParam identifier = (TokenParam) params;
					inSql.append(" (");
					if (identifier.getValue() != null) {
						inSql.append(" segment ->>'value' = '" + identifier.getValue() + "' ");
						if (identifier.getSystem() != null) inSql.append(" and ");
					}

					if (identifier.getSystem() != null) {
						inSql.append(" segment ->>'system' = '" + identifier.getSystem() + "' ");
					}
					inSql.append(") ");
	            }
	        }

	        if(StringUtils.isNotEmpty(inSql.toString())){
				String inSqlStr = "select distinct(id) from patient r, LATERAL json_array_elements(r.data->'identifier') segment WHERE " +inSql.toString();
				criteria.add(Restrictions.sqlRestriction("{alias}.id in ("+inSqlStr+")"));
			}
		}
	}
    
	/**
	 * This method builds criteria for patient given name
	 * @param theMap : search parameter "given"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildGivenNameCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("given");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam givenName = (StringParam) params;
                    Criterion orCond= null;
                    if (givenName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 = '" +givenName.getValue()+"'")
                    			);
                    } else if (givenName.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 ilike '%" + givenName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 ilike '%" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 ilike '%" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 ilike '%" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 ilike '%" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 ilike '%" + givenName.getValue() + "%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 ilike '" + givenName.getValue() + "%'")
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for patient family name
	 * @param theMap : search parameter "family"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildFamilyNameCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("family");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam familyName = (StringParam) params;
                    Criterion orCond= null;
                    if (familyName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' = '" +familyName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' = '" +familyName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' = '" +familyName.getValue()+"'")
                    			);
                    } else if (familyName.isContains()) {
                    	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' ilike '%" +familyName.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' ilike '%" +familyName.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' ilike '%" +familyName.getValue()+"%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
                    			Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' ilike '" +familyName.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' ilike '" +familyName.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' ilike '" +familyName.getValue()+"%'")
                			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for patient telecom
	 * @param theMap : search parameter "telecom"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildTelecomCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("telecom");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam telecom = (StringParam) params;
                    Criterion orCond= null;
                    if (telecom.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' = '" +telecom.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' = '" +telecom.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' = '" +telecom.getValue()+"'")
                    			);
                    } else if (telecom.isContains()) {
                    	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '%" +telecom.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '%" +telecom.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '%" +telecom.getValue()+"%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
                    			Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '" +telecom.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '" +telecom.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '" +telecom.getValue()+"%'")
                			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for patient gender
	 * @param theMap : search parameter "gender"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildGenderCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("gender");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    TokenParam gender = (TokenParam) params;
                    if(gender.getModifier() != null) {
                        TokenParamModifier modifier = gender.getModifier();
                        if(modifier.getValue() == ":not") {
                        	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' not ilike '"+gender.getValue()+"'"));
                        }
                    }else if(StringUtils.isNoneEmpty(gender.getValue())){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' ilike '" + gender.getValue() + "'"));
                    }else if(gender.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' IS NULL"));
                    }else if(!gender.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' IS NOT NULL"));
                    }
                }
            }
        }
    }
    
    /**
	 * This method builds criteria for patient active
	 * @param theMap : search parameter "active"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildActiveCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("active");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    TokenParam active = (TokenParam) params;
                    if(StringUtils.isNoneEmpty(active.getValue())){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' ilike '" + active.getValue() + "'"));
                    }else if(active.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' IS NULL"));

                    }else if(!active.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' IS NOT NULL"));
                    }
                }
            }
        }
    }
    
    /**
  	 * This method builds criteria for patient has been marked as deceased
  	 * @param theMap : search parameter "deceased"
  	 * @param criteria : for retrieving entities by composing Criterion objects
  	 */
    private void buildDeceasedBooleanCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("deceased");
    	if (list != null) {

    		for (List<? extends IQueryParameterType> values : list) {
    			for (IQueryParameterType params : values) {
    				TokenParam deceased = (TokenParam) params;
    				if(StringUtils.isNoneEmpty(deceased.getValue())){
    					criteria.add(Restrictions.sqlRestriction("{alias}.data->>'deceasedBoolean' ilike '" + deceased.getValue() + "'"));
    				}else if(deceased.getMissing()){
    					criteria.add(Restrictions.sqlRestriction("{alias}.data->>'deceasedBoolean' IS NULL"));
	
    				}else if(!deceased.getMissing()){
    					criteria.add(Restrictions.sqlRestriction("{alias}.data->>'deceasedBoolean' IS NOT NULL"));
    				}
    			}
    		}
    	}
    }
      
    /**
	 * This method builds criteria for patient name
	 * @param theMap : search parameter "name"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildNameCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("name");

        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam name = (StringParam) params;
                    Criterion orCond= null;
                    if (name.isExact()) {
                        orCond= Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 = '" +name.getValue()+"'")
                				
                                );
                    } else if (name.isContains()) {
                        orCond= Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 ilike '%" +name.getValue()+"%'")
                                );
                    } else {
                        orCond= Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 ilike '" +name.getValue()+"%'")
                                );
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for patient birthdate
	 * @param theMap : search parameter "birthdate"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildBirthDateCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("birthdate");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	System.out.println(list);
                 for (IQueryParameterType params : values) {
                    DateParam birthDate = (DateParam) params;
                    String birthDateFormat = birthDate.getValueAsString();
                    if(birthDate.getPrefix() != null) {
                        if(birthDate.getPrefix().getValue() == "gt"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'birthDate')::DATE > '"+birthDateFormat+ "'"));
                        }else if(birthDate.getPrefix().getValue() == "lt"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'birthDate')::DATE < '"+birthDateFormat+ "'"));
                        }else if(birthDate.getPrefix().getValue() == "ge"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'birthDate')::DATE >= '"+birthDateFormat+ "'"));
                        }else if(birthDate.getPrefix().getValue() == "le"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'birthDate')::DATE <= '"+birthDateFormat+ "'"));
                        }else if(birthDate.getPrefix().getValue() == "eq"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'birthDate')::DATE = '"+birthDateFormat+"'"));
						}
                    }else{
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'birthDate')::DATE = '"+birthDateFormat+"'"));
					}
                }            
            }
        }
    }

    /**
	 * This method builds criteria for patient address
	 * @param theMap : search parameter "address"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildAddressCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("address");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam address = (StringParam) params;
                    Criterion orCond= null;
                    if(address.isExact()){
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>0 = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>1 = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'district' = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' = '" + address.getValue() + "'")
                    			);
                    }else if(address.isContains()){
                    	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>0 ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>1 ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'district' ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '%" + address.getValue() + "%'")
                			);
                    }else {
                    	orCond = Restrictions.or(
                    			Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>0 ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>1 ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'district' ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '" + address.getValue() + "%'")
            			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for patient address city
	 * @param theMap : search parameter "address-city"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildAddressCityCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("address-city");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam addressCity = (StringParam) params;
                    if(addressCity.isExact()){
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' = '" + addressCity.getValue() + "'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'city' = '" + addressCity.getValue() + "'"));
                    	
                    }else if(addressCity.isContains()){
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' ilike '%" + addressCity.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'city' ilike '%" + addressCity.getValue() + "%'"));

                    }else {
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' ilike '" + addressCity.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'city' ilike '" + addressCity.getValue() + "%'"));

                    }
                }
            }
        }
    }
    
    /**
   	 * This method builds criteria for patient address country
   	 * @param theMap : search parameter "address-country"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildAddressCountryCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("address-country");
    	if (list != null) {
	
	       for (List<? extends IQueryParameterType> values : list) {
	    	   Disjunction disjunction = Restrictions.disjunction();
	           for (IQueryParameterType params : values) {
	               StringParam addressCountry = (StringParam) params;
	               if(addressCountry.isExact()){
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' = '" + addressCountry.getValue() + "'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'country' = '" + addressCountry.getValue() + "'"));

	               }else if(addressCountry.isContains()){
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' ilike '%" + addressCountry.getValue() + "%'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'country' ilike '%" + addressCountry.getValue() + "%'"));

	               }else {
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' ilike '" + addressCountry.getValue() + "%'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'country' ilike '" + addressCountry.getValue() + "%'"));

	               }
	           }
	       }
    	}
    }
    
    /**
   	 * This method builds criteria for patient address state
   	 * @param theMap : search parameter "address-state"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildAddressStateCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("address-state");
    	if (list != null) {
	
	       for (List<? extends IQueryParameterType> values : list) {
	    	   Disjunction disjunction = Restrictions.disjunction();
	           for (IQueryParameterType params : values) {
	               StringParam addressState = (StringParam) params;
	               if(addressState.isExact()){
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' = '" + addressState.getValue() + "'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'state' = '" + addressState.getValue() + "'"));

	               }else if(addressState.isContains()){
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' ilike '%" + addressState.getValue() + "%'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'state' ilike '%" + addressState.getValue() + "%'"));

	               }else {
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' ilike '" + addressState.getValue() + "%'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'state' ilike '" + addressState.getValue() + "%'"));

	               }
	           }
	       }
    	}
    }
    
    /**
   	 * This method builds criteria for patient address postalcode
   	 * @param theMap : search parameter "address-postalcode"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildAddressPostalcodeCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("address-postalcode");
    	if (list != null) {
	
	       for (List<? extends IQueryParameterType> values : list) {
	    	   Disjunction disjunction = Restrictions.disjunction();
	           for (IQueryParameterType params : values) {
	               StringParam addressPostalcode = (StringParam) params;
	               if(addressPostalcode.isExact()){
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' = '" + addressPostalcode.getValue() + "'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' = '" + addressPostalcode.getValue() + "'"));

	               }else if(addressPostalcode.isContains()){
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '%" + addressPostalcode.getValue() + "%'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' ilike '%" + addressPostalcode.getValue() + "%'"));

	               }else {
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '" + addressPostalcode.getValue() + "%'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' ilike '" + addressPostalcode.getValue() + "%'"));

	               }
	           }
	           criteria.add(disjunction);
	       }
    	}
    }
    
    /**
	 * This method builds criteria for patient communication language
	 * @param theMap : search parameter "language"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildLanguageCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("language");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam language = (StringParam) params;
                    if(language.isExact()){
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'system' = '" + language.getValue() + "'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'code' = '" + language.getValue() + "'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'display' = '" + language.getValue() + "'"));
                            				
                    }else if(language.isContains()){
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'system' ilike '%" + language.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'code' ilike '%" + language.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'display' ilike '%" + language.getValue() + "%'"));

                    }else {
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'system' ilike '" + language.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'code' ilike '" + language.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'language'->'coding'->0->>'display' ilike '" + language.getValue() + "%'"));
               
                    }
                }
            }
        }
    }
    
    /**
	 * This method builds criteria for patient linked to the given patient
	 * @param theMap : search parameter "link"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildLinkCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("link");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    ReferenceParam link = (ReferenceParam) params;
                    if(link.getValue() != null){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->'link'->0->'other'->>'reference' = '" + link.getValue() + "'"));		
                    }else if(link.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->'link'->0->'other'->>'reference' IS NULL"));
                    }else if(!link.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->'link'->0->'other'->>'reference' IS NOT NULL"));
                    }
                }
            }
        }
    }
    
    /**
   	 * This method builds criteria for organization that is the custodian of the patient record
   	 * @param theMap : search parameter "organization"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildOrganizationCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("organization");
    	if (list != null) {
	
	       for (List<? extends IQueryParameterType> values : list) {
	    	   Disjunction disjunction = Restrictions.disjunction();
	           for (IQueryParameterType params : values) {
	               StringParam organization = (StringParam) params;
	               if(organization.isExact()) {
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'display' = '" + organization.getValue() + "'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'reference' = '" + organization.getValue() + "'"));
	   				
	               }else if(organization.isContains()){
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'display' ilike '%" + organization.getValue() + "%'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'reference' ilike '%" + organization.getValue() + "%'"));
	
	               }else {
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'display' ilike '" + organization.getValue() + "%'"));
	            	   disjunction.add(Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'reference' ilike '" + organization.getValue() + "%'"));

	               }
	           }
	       }
    	}
    }
    
    /**
   	 * This method builds criteria for patient address-use
   	 * @param theMap : search parameter "address-use"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildAddressUseCriteria(SearchParameterMap theMap, Criteria criteria) {
           List<List<? extends IQueryParameterType>> list = theMap.get("address-use");
           if (list != null) {

           	for (List<? extends IQueryParameterType> values : list) {
           		Disjunction disjunction = Restrictions.disjunction();
           		for (IQueryParameterType params : values) {
           			StringParam addressUse = (StringParam) params;
           			Criterion orCond = null;
           			if(addressUse.getValue() != null ){
           				orCond = Restrictions.or(
           							Restrictions.sqlRestriction("{alias}.data->'address'->0->>'use' ilike '%" + addressUse.getValue() + "%'"),
           							Restrictions.sqlRestriction("{alias}.data->'address'->1->>'use' ilike '%" + addressUse.getValue() + "%'")
           						);
           			}
           			disjunction.add(orCond);
           		}
           		criteria.add(disjunction);
    	       	}
           }
    }
    
    /**
     * This method builds criteria for fetching history of the patient by id
     * @param theId : ID of the patient
     * @return : List of patient records
     */
    @Override
	public List<DafPatient> getPatientHistoryById(String theId) {
    	List<DafPatient> list = getSession().createNativeQuery(
    			"select * from patient where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafPatient.class)
    				.getResultList();
		return list;
	}
	@Override
	public List<DafPatient> getPatientsOnAuthorize() {
		/*
		 * List<DafPatient> list = getSession().createNativeQuery(
		 * "SELECT * FROM patient WHERE data->'meta'->>'versionId' = (SELECT MIN(data->'meta'->>'versionId') FROM patient )"
		 * , DafPatient.class) .getResultList();
		 */
		/*
		 * List<DafPatient> list = getSession().createNativeQuery(
		 * "select * from patient GROUP BY id  ORDER BY max(cast(data->'meta'->>'versionId' as float)) "
		 * , DafPatient.class) .getResultList();
		 */
	//	List<DafPatient> list = getSession().createNativeQuery(
	//	"SELECT * FROM patient WHERE (data->>'id', data->'meta'->>'versionId') IN ( SELECT data->>'id', MAX(data->'meta'->>'versionId') FROM patient GROUP BY data->>'id' ) ORDER BY data->>'id'" , DafPatient.class) .getResultList();
		
		List<DafPatient> list = getSession().createNativeQuery(
		"SELECT * FROM patient  ORDER BY data->>'id'"  , DafPatient.class) .getResultList();
		return list;
	}

}
