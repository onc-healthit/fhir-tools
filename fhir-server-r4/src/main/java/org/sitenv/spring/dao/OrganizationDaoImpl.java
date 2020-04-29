package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("organizationDao")
public class OrganizationDaoImpl extends AbstractDao implements OrganizationDao {

	/**
	 * This method builds criteria for fetching organization record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the organization
	 */
	public DafOrganization getOrganizationById(String id) {
		Criteria criteria = getSession().createCriteria(DafOrganization.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafOrganization) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the
	 * organization record by id.
	 * 
	 * @param theId     : ID of the organization
	 * @param versionId : version of the organization record
	 * @return : DAF object of the organization
	 */
	@Override
	public DafOrganization getOrganizationByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafOrganization.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" + versionId + "'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + theId + "'"));
		criteria.add(versionConjunction);
		return (DafOrganization) criteria.uniqueResult();
	}

	/**
	 * This method builds criteria for fetching history of the organization by id
	 * 
	 * @param id : ID of the organization
	 * @return : List of organization DAF records
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafOrganization> getOrganizationHistoryById(String id) {
		Criteria criteria = getSession().createCriteria(DafOrganization.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + id + "'"));
		return (List<DafOrganization>) criteria.list();
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF organization object
	 */
	@SuppressWarnings("unchecked")
	public List<DafOrganization> search(SearchParameterMap theMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafOrganization.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for address
		buildAddressCriteria(theMap, criteria);

		// build criteria for address_state
		buildAddressStateCriteria(theMap, criteria);

		// build criteria for address_country
		buildAddressCountryCriteria(theMap, criteria);

		// build criteria for address_postalcode
		buildAddressPostalcodeCriteria(theMap, criteria);

		// build criteria for address_city
		buildAddressCityCriteria(theMap, criteria);

		// build criteria for name
		buildNameCriteria(theMap, criteria);

		// build criteria for endPoint
		buildEndPointCriteria(theMap, criteria);

		// build criteria for partOf
		buildPartOfCriteria(theMap, criteria);

		// build criteria for active
		buildActiveCriteria(theMap, criteria);

		// build criteria for type
		buildTypeCriteria(theMap, criteria);

		// build criteria for telecom
		buildTelecomCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for organization telecom
	 * 
	 * @param theMap   : search parameter "telecom"
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
	 * This method builds criteria for organization id
	 * 
	 * @param theMap   : search parameter "_id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("_id");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam id = (StringParam) params;
					if (id.getValue() != null) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + id.getValue() + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for organization identifier
	 * 
	 * @param theMap   : search parameter "identifier"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdentifierCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("identifier");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam identifier = (TokenParam) params;
					Criterion orCond = null;
					if (identifier.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '"
										+ identifier.getValue() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization active
	 * 
	 * @param theMap   : search parameter "active"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildActiveCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("active");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam active = (TokenParam) params;
					if (StringUtils.isNoneEmpty(active.getValue())) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'active' ilike '%" + active.getValue() + "%'"));
					} else if (active.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' IS NULL"));
					} else if (!active.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' IS NOT NULL"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for organization partOf
	 * 
	 * @param theMap   : search parameter "partOf"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPartOfCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("partof");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam partof = (ReferenceParam) params;
					Criterion orCond= null;
					if (partof.getValue() != null) {
						orCond = Restrictions.or(
									Restrictions.sqlRestriction("{alias}.data->'partOf'->>'reference' ilike '%" + partof.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'partOf'->>'display' ilike '%" + partof.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'partOf'->>'type' ilike '%" + partof.getValue() + "%'")
								);
					} else if (partof.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NULL"));
					} else if (!partof.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization type
	 * 
	 * @param theMap   : search parameter "type"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildTypeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("type");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam type = (TokenParam) params;
					if (type.getValue() != null) {
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->0->'coding'->0->>'system' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->0->'coding'->0->>'code' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->0->'coding'->0->>'display' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->0->'coding'->1->>'system' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->0->'coding'->1->>'code' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->0->'coding'->1->>'display' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->1->'coding'->0->>'system' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->1->'coding'->0->>'code' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->1->'coding'->0->>'display' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->1->'coding'->1->>'system' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->1->'coding'->1->>'code' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->1->'coding'->1->>'display' ilike '" + type.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization endPoint
	 * 
	 * @param theMap   : search parameter "endPoint"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildEndPointCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("endpoint");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam endpoint = (ReferenceParam) params;
					Criterion orCond= null;
					if (endpoint.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'endpoint'->0->>'reference' ilike '%" + endpoint.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'endpoint'->0->>'display' ilike '%" + endpoint.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'endpoint'->0->>'type' ilike '%" + endpoint.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'endpoint'->1->>'reference' ilike '%" + endpoint.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'endpoint'->1->>'display' ilike '%" + endpoint.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'endpoint'->1->>'type' ilike '%" + endpoint.getValue() + "%'")
								
								);
					} else if (endpoint.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'endpoint' IS NULL"));
					} else if (!endpoint.getMissing()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->>'endpoint' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization name
	 * 
	 * @param theMap   : search parameter "name"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildNameCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("name");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam name = (StringParam) params;
					if (name.isExact()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'name' = '" + name.getValue() + "'"));
					} else if (name.isContains()) {
						criteria.add(
								Restrictions.sqlRestriction("{alias}.data->>'name' ilike '%" + name.getValue() + "%'"));
					} else {
						criteria.add(
								Restrictions.sqlRestriction("{alias}.data->>'name' ilike '" + name.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for organization address
	 * 
	 * @param theMap   : search parameter "address"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAddressCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("address");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam address = (StringParam) params;
					Criterion orCond = null;
					if (address.isExact()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->'line'->>0 = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->'line'->>1 = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'city' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'district' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'state' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'country' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'postalCode' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->'line'->>0 = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->'line'->>1 = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'city' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'district' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'state' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'country' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'postalCode' = '" + address.getValue() + "'")
								);
					} else if (address.isContains()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->'line'->>0 ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->'line'->>1 ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'city' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'district' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'state' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'country' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '%"
										+ address.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->'line'->>0 ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->'line'->>1 ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'city' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'district' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'state' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'country' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' ilike '%"
										+ address.getValue() + "%'")
								);
					} else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->'line'->>0 ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->'line'->>1 ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'city' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'district' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'state' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->0->>'country' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '"
										+ address.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->'line'->>0 ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->'line'->>1 ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'city' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'district' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'state' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'country' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' ilike '"
										+ address.getValue() + "%'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization address state
	 * 
	 * @param theMap   : search parameter "address-state"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAddressStateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("address-state");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam addressState = (StringParam) params;
					Criterion orCond = null;
					if (addressState.isExact()) {
						orCond = Restrictions.or(
									Restrictions.sqlRestriction(
											"{alias}.data->'address'->0->>'state' = '" + addressState.getValue() + "'"),
									Restrictions.sqlRestriction(
											"{alias}.data->'address'->1->>'state' = '" + addressState.getValue() + "'")
								);
					} else if (addressState.isContains()) {
						orCond = Restrictions.or(
									Restrictions.sqlRestriction(
									"{alias}.data->'address'->0->>'state' ilike '%" + addressState.getValue() + "%'"),
									Restrictions.sqlRestriction(
											"{alias}.data->'address'->1->>'state' ilike '%" + addressState.getValue() + "%'")
								);
					} else {
						orCond = Restrictions.or(
									Restrictions.sqlRestriction(
									"{alias}.data->'address'->0->>'state' ilike '" + addressState.getValue() + "%'"),
									Restrictions.sqlRestriction(
											"{alias}.data->'address'->1->>'state' ilike '" + addressState.getValue() + "%'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization address city
	 * 
	 * @param theMap   : search parameter "address-city"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAddressCityCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("address-city");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam addressCity = (StringParam) params;
					Criterion orCond = null;
					if (addressCity.isExact()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
								"{alias}.data->'address'->0->>'city' = '" + addressCity.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'city' = '" + addressCity.getValue() + "'")
								);
					} else if (addressCity.isContains()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
								"{alias}.data->'address'->0->>'city' ilike '%" + addressCity.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'city' ilike '%" + addressCity.getValue() + "%'")
								);
					} else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
								"{alias}.data->'address'->0->>'city' ilike '" + addressCity.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'city' ilike '" + addressCity.getValue() + "%'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization address postalCode
	 * 
	 * @param theMap   : search parameter "address-postalCode"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAddressPostalcodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("address-postalcode");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam addressPostalcode = (StringParam) params;
					Criterion orCond = null;
					if (addressPostalcode.isExact()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->'address'->0->>'postalCode' = '" + addressPostalcode.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'postalCode' = '" + addressPostalcode.getValue() + "'")
								);
					} else if (addressPostalcode.isContains()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '%"
								+ addressPostalcode.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' ilike '%"
										+ addressPostalcode.getValue() + "%'")
								);
					} else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '"
								+ addressPostalcode.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' ilike '"
										+ addressPostalcode.getValue() + "%'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization address country
	 * 
	 * @param theMap   : search parameter "address-country"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAddressCountryCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("address-country");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam addressCountry = (StringParam) params;
					Criterion orCond = null;
					if (addressCountry.isExact()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
								"{alias}.data->'address'->0->>'country' = '" + addressCountry.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'country' = '" + addressCountry.getValue() + "'")
								);
					} else if (addressCountry.isContains()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->'address'->0->>'country' ilike '%" + addressCountry.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'country' ilike '%" + addressCountry.getValue() + "%'")
								);
					} else {
						orCond = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->'address'->0->>'country' ilike '" + addressCountry.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->1->>'country' ilike '" + addressCountry.getValue() + "%'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}
}
