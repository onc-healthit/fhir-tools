package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.TokenParamModifier;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("deviceDao")
public class DeviceDaoImpl extends AbstractDao implements DeviceDao {

	/**
	 * This method builds criteria for fetching device record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the device
	 */
	public DafDevice getDeviceById(String id) {

		Criteria criteria = getSession().createCriteria(DafDevice.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafDevice) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the device
	 * record by id.
	 * 
	 * @param theId     : ID of the device
	 * @param versionId : version of the device record
	 * @return : DAF object of the device
	 */
	public DafDevice getDeviceByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafDevice.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" + versionId + "'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + theId + "'"));
		criteria.add(versionConjunction);
		System.out.println("inside getDeviceHistoryById");
		return (DafDevice) criteria.uniqueResult();
	}

	/**
	 * This method builds criteria for fetching history of the device by id
	 * 
	 * @param theId : ID of the device
	 * @return : List of device DAF records
	 */
	public List<DafDevice> getDeviceHistoryById(String theId) {
		
		List<DafDevice> list = getSession().createNativeQuery(
				"select * from device where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafDevice.class)
		    	.getResultList();
		return list;
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF device object
	 */
	@SuppressWarnings("unchecked")
	public List<DafDevice> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafDevice.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for status
		buildStatusCriteria(theMap, criteria);

		// build criteria for manufacturer
		buildManufacturerCriteria(theMap, criteria);

		// build criteria for lotNumber
		buildLotNumberCriteria(theMap, criteria);

		// build criteria for serialNumber
		buildSerialNumberCriteria(theMap, criteria);

		// build criteria for patient
		buildPatientCriteria(theMap, criteria);

		// build criteria for owner
		buildOwnerCriteria(theMap, criteria);

		// build criteria for model-number
		buildModelNumberCriteria(theMap, criteria);

		// build criteria for type
		buildTypeCriteria(theMap, criteria);

		// build criteria for device-name
		buildDeviceNameCriteria(theMap, criteria);

		// build criteria for Udi_carrier
		buildUdiCarrierCriteria(theMap, criteria);

		// build criteria for expirationDate
		buildExpirationDateCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for device expiration-date
	 * 
	 * @param theMap   : search parameter "expiration-date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildExpirationDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("expiration-date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam expirationDate = (DateParam) params;
					String expirationDateFormat = expirationDate.getValueAsString();
					if (expirationDate.getPrefix() != null) {
						if (expirationDate.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'expirationDate')::DATE > '" + expirationDateFormat + "'"));
						} else if (expirationDate.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'expirationDate')::DATE < '" + expirationDateFormat + "'"));
						} else if (expirationDate.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'expirationDate')::DATE >= '" + expirationDateFormat + "'"));
						} else if (expirationDate.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'expirationDate')::DATE <= '" + expirationDateFormat + "'"));
						} else if (expirationDate.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'expirationDate')::DATE = '" + expirationDateFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'expirationDate')::DATE = '" + expirationDateFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for device uri_carrier
	 * 
	 * @param theMap   : search parameter "uri_carrier"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildUdiCarrierCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("udi-carrier");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam udiCarrier = (StringParam) params;
					Criterion orCond = null;
					if (udiCarrier.isExact()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'jurisdiction' = '"
										+ udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'deviceIdentifier' = '"
										+ udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'issuer' = '" 
										+ udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'carrierHRF' = '"
										+ udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'entryType' = '"
										+ udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'jurisdiction' = '"
										+ udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'deviceIdentifier' = '"
										+ udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'issuer' = '" + udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'carrierHRF' = '"
										+ udiCarrier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'entryType' = '"
										+ udiCarrier.getValue() + "'")
								);
					} else if (udiCarrier.isContains()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'jurisdiction' ilike '%"
										+ udiCarrier.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'udiCarrier'->0->>'deviceIdentifier' ilike '%"
												+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'issuer' ilike '%"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'carrierHRF' ilike '%"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'entryType' ilike '%"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'jurisdiction' ilike '%"
										+ udiCarrier.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'udiCarrier'->1->>'deviceIdentifier' ilike '%"
												+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'issuer' ilike '%"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'carrierHRF' ilike '%"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'entryType' ilike '%"
										+ udiCarrier.getValue() + "%'")
								);
					} else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'jurisdiction' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'deviceIdentifier' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'issuer' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'carrierHRF' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->0->>'entryType' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'jurisdiction' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'deviceIdentifier' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'issuer' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'carrierHRF' ilike '"
										+ udiCarrier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'udiCarrier'->1->>'entryType' ilike '"
										+ udiCarrier.getValue() + "%'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for device device-name
	 * 
	 * @param theMap   : search parameter "device-name"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildDeviceNameCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("device-name");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam deviceName = (StringParam) params;
					Criterion orCond = null;
					if (deviceName.isExact()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'deviceName'->0->>'name' = '" + deviceName.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'deviceName'->0->>'type' = '" + deviceName.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'deviceName'->1->>'name' = '" + deviceName.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'deviceName'->1->>'type' = '" + deviceName.getValue() + "'")
								);
					} else if (deviceName.isContains()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'deviceName'->0->>'name' ilike '%"
										+ deviceName.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'deviceName'->0->>'type' ilike '%"
										+ deviceName.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'deviceName'->1->>'name' ilike '%"
										+ deviceName.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'deviceName'->1->>'type' ilike '%"
										+ deviceName.getValue() + "%'")
								);
					} else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'deviceName'->0->>'name' ilike '"
										+ deviceName.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'deviceName'->0->>'type' ilike '"
										+ deviceName.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'deviceName'->1->>'name' ilike '"
										+ deviceName.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'deviceName'->1->>'type' ilike '"
										+ deviceName.getValue() + "%'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for device type
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
								"{alias}.data->'type'->'coding'->0->>'system' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->0->>'code' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->0->>'display' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->1->>'system' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->1->>'code' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->1->>'display' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->0->>'system' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->0->>'code' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->0->>'display' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->1->>'system' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->1->>'code' ilike '" + type.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'type'->'coding'->1->>'display' ilike '" + type.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for device model-number
	 * 
	 * @param theMap   : search parameter "model-number"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildModelNumberCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("model-number");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam modelNumber = (StringParam) params;
					if (modelNumber.isExact()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'modelNumber' = '" + modelNumber.getValue() + "'"));
					} else if (modelNumber.isContains()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'modelNumber' ilike '%" + modelNumber.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'modelNumber' ilike '" + modelNumber.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for device owner
	 * 
	 * @param theMap   : search parameter "owner"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildOwnerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("owner");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam owner = (StringParam) params;
					Criterion orCond = null;
					if (owner.isExact()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'reference' = '" + owner.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'display' = '" + owner.getValue() + "'"),
								Restrictions
										.sqlRestriction("{alias}.data->'owner'->>'type' = '" + owner.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'identifier' = '" + owner.getValue() + "'"));
					} else if (owner.isContains()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'reference' ilike '%" + owner.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'display' ilike '%" + owner.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'type' ilike '%" + owner.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'identifier' ilike '%" + owner.getValue() + "%'"));
					} else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'reference' ilike '" + owner.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'display' ilike '" + owner.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'type' ilike '" + owner.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'owner'->>'identifier' ilike '" + owner.getValue() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for device patient
	 * 
	 * @param theMap   : search parameter "patient"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPatientCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("patient");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam patient = (StringParam) params;
					Criterion orCond = null;
					if (patient.isExact()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' = '" + patient.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' = '" + patient.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' = '" + patient.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' = '" + patient.getValue() + "'"));
					} else if (patient.isContains()) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' ilike '%" + patient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' ilike '%" + patient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' ilike '%" + patient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' ilike '%" + patient.getValue() + "%'"));
					} else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' ilike '%" + patient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' ilike '%" + patient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' ilike '%" + patient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' ilike '%" + patient.getValue() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for device serialNumber
	 * 
	 * @param theMap   : search parameter "serialNumber"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSerialNumberCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("serial-number");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam serialNumber = (StringParam) params;
					if (serialNumber.isExact()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'serialNumber' = '" + serialNumber.getValue() + "'"));
					} else if (serialNumber.isContains()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'serialNumber' ilike '%" + serialNumber.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'serialNumber' ilike '" + serialNumber.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for device LotNumber
	 * 
	 * @param theMap   : search parameter "lotNumber"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildLotNumberCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("lot-number");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam lotNumber = (StringParam) params;
					if (lotNumber.isExact()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'lotNumber' = '" + lotNumber.getValue() + "'"));
					} else if (lotNumber.isContains()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'lotNumber' ilike '%" + lotNumber.getValue() + "%'"));
					} else {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'lotNumber' ilike '" + lotNumber.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for device status
	 * 
	 * @param theMap   : search parameter "status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam status = (TokenParam) params;
					if (status.getModifier() != null) {
						TokenParamModifier modifier = status.getModifier();
						if (modifier.getValue() == ":not") {
							criteria.add(Restrictions
									.sqlRestriction("{alias}.data->>'status' not ilike '" + status.getValue() + "'"));
						}
					} else if (StringUtils.isNoneEmpty(status.getValue())) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'status' ilike '" + status.getValue() + "'"));
					} else if (status.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' IS NULL"));
					} else if (!status.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' IS NOT NULL"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for device manufacturer
	 * 
	 * @param theMap   : search parameter "manufacturer"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildManufacturerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("manufacturer");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam manufacturer = (StringParam) params;
					if (manufacturer.isExact()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'manufacturer' = '" + manufacturer.getValue() + "'"));
					} else if (manufacturer.isContains()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'manufacturer' ilike '%" + manufacturer.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'manufacturer' ilike '" + manufacturer.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for device id
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
	 * This method builds criteria for device identifier
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
					if(identifier.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%"
										+ identifier.getValue() + "%'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}
}
