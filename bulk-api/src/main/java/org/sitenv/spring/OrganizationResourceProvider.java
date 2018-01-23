package org.sitenv.spring;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.query.OrganizationSearchCriteria;
import org.sitenv.spring.service.OrganizationService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Organization;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

public class OrganizationResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Organization";
	public static final String VERSION_ID = "2.0";
	AbstractApplicationContext context;
	OrganizationService service;

	public OrganizationResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (OrganizationService) context.getBean("organizationService");
	}

	@Override
	public Class<Organization> getResourceType() {
		return Organization.class;
	}

	@Search
	public List<Organization> getAllOrganizations(@IncludeParam(allow = "*") Set<Include> theIncludes,
			@Sort SortSpec theSort, @Count Integer theCount) {

		List<DafOrganization> dafOrganizationList = service.getAllOrganizations();

		List<Organization> organizationList = new ArrayList<Organization>();

		for (DafOrganization dafOrganization : dafOrganizationList) {
			organizationList.add(createOrganizationObject(dafOrganization));
		}

		return organizationList;
	}
	
	
	  public List<Organization> getOrganizationForBulkDataRequest(List<Integer> patients, Date start) {
	    	
	    	
			
			List<DafOrganization> dafOrganizationList = service.getOrganizationForBulkData(patients, start);

			List<Organization> organizationList = new ArrayList<Organization>();

			for (DafOrganization dafOrganization : dafOrganizationList) {
				organizationList.add(createOrganizationObject(dafOrganization));
			}
			
			return organizationList;
		}

	
	
	@Read()
	public Organization getOrganizationResourceById(@IdParam IdDt theId) {

		try {
			Integer organizationId = theId.getIdPartAsLong().intValue();
			DafOrganization dafOrganization = service.getOrganizationResourceById(organizationId);
			return createOrganizationObject(dafOrganization);
		} catch (Exception e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an
			 * unknown resource
			 */
			throw new ResourceNotFoundException(theId);
		}
	}
	
	@Search()
    public List<Organization> getOrganizationByIdentifierValue(@RequiredParam(name = Organization.SP_IDENTIFIER) TokenParam theIdentifierValue,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        OrganizationSearchCriteria organizationSearchCriteria = new OrganizationSearchCriteria();
        organizationSearchCriteria.setIdentifier(theIdentifierValue);
        List<DafOrganization> dafOrganiztionList = service.getOrganizationBySearchCriteria(organizationSearchCriteria);

        List<Organization> organizationList = new ArrayList<Organization>();
        for (DafOrganization dafOrganization : dafOrganiztionList) {
        	organizationList.add(createOrganizationObject(dafOrganization));
        }
        return organizationList;
    }
	
	@Search()
    public List<Organization> getOrganizationByName(@RequiredParam(name = Organization.SP_NAME) StringDt nameValue,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        OrganizationSearchCriteria organizationSearchCriteria = new OrganizationSearchCriteria();
        organizationSearchCriteria.setName(nameValue.getValue());
        List<DafOrganization> dafOrganiztionList = service.getOrganizationBySearchCriteria(organizationSearchCriteria);

        List<Organization> organizationList = new ArrayList<Organization>();
        for (DafOrganization dafOrganization : dafOrganiztionList) {
        	organizationList.add(createOrganizationObject(dafOrganization));
        }
        return organizationList;
    }
	
	@Search()
    public List<Organization> getOrganizationByAddress(@RequiredParam(name = Organization.SP_ADDRESS) StringDt addressValue,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        OrganizationSearchCriteria organizationSearchCriteria = new OrganizationSearchCriteria();
        organizationSearchCriteria.setAddress(addressValue.getValue());
        List<DafOrganization> dafOrganiztionList = service.getOrganizationBySearchCriteria(organizationSearchCriteria);

        List<Organization> organizationList = new ArrayList<Organization>();
        for (DafOrganization dafOrganization : dafOrganiztionList) {
        	organizationList.add(createOrganizationObject(dafOrganization));
        }
        return organizationList;
    }
	
	@Search()
    public List<Organization> getOrganizationByAddressCity(@RequiredParam(name = Organization.SP_ADDRESS_CITY) StringDt addressValue,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        OrganizationSearchCriteria organizationSearchCriteria = new OrganizationSearchCriteria();
        organizationSearchCriteria.setAddress_city(addressValue.getValue());
        List<DafOrganization> dafOrganiztionList = service.getOrganizationBySearchCriteria(organizationSearchCriteria);

        List<Organization> organizationList = new ArrayList<Organization>();
        for (DafOrganization dafOrganization : dafOrganiztionList) {
        	organizationList.add(createOrganizationObject(dafOrganization));
        }
        return organizationList;
    }
	
	@Search()
    public List<Organization> getOrganizationByAddressCountry(@RequiredParam(name = Organization.SP_ADDRESS_COUNTRY) StringDt addressValue,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        OrganizationSearchCriteria organizationSearchCriteria = new OrganizationSearchCriteria();
        organizationSearchCriteria.setAddress_country(addressValue.getValue());
        List<DafOrganization> dafOrganiztionList = service.getOrganizationBySearchCriteria(organizationSearchCriteria);

        List<Organization> organizationList = new ArrayList<Organization>();
        for (DafOrganization dafOrganization : dafOrganiztionList) {
        	organizationList.add(createOrganizationObject(dafOrganization));
        }
        return organizationList;
    }
	
	@Search()
    public List<Organization> getOrganizationByAddressState(@RequiredParam(name = Organization.SP_ADDRESS_STATE) StringDt addressValue,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        OrganizationSearchCriteria organizationSearchCriteria = new OrganizationSearchCriteria();
        organizationSearchCriteria.setAddress_state(addressValue.getValue());
        List<DafOrganization> dafOrganiztionList = service.getOrganizationBySearchCriteria(organizationSearchCriteria);

        List<Organization> organizationList = new ArrayList<Organization>();
        for (DafOrganization dafOrganization : dafOrganiztionList) {
        	organizationList.add(createOrganizationObject(dafOrganization));
        }
        return organizationList;
    }
	
	@Search()
    public List<Organization> getOrganizationByAddressPostalCode(@RequiredParam(name = Organization.SP_ADDRESS_POSTALCODE) StringDt addressValue,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        OrganizationSearchCriteria organizationSearchCriteria = new OrganizationSearchCriteria();
        organizationSearchCriteria.setAddress_postalCode(addressValue.getValue());
        List<DafOrganization> dafOrganiztionList = service.getOrganizationBySearchCriteria(organizationSearchCriteria);

        List<Organization> organizationList = new ArrayList<Organization>();
        for (DafOrganization dafOrganization : dafOrganiztionList) {
        	organizationList.add(createOrganizationObject(dafOrganization));
        }
        return organizationList;
    }

	private Organization createOrganizationObject(DafOrganization dafOrganization) {

		Organization organization = new Organization();

		// Set Version
		organization.setId(new IdDt(RESOURCE_TYPE, dafOrganization.getId() + "", VERSION_ID));
		
		// set Organization Identifier
		Map<String, String> identifier = HapiUtils.convertToJsonMap(dafOrganization.getIdentifier());
		List<IdentifierDt> identifierDts = new ArrayList<IdentifierDt>();
		IdentifierDt identifierDt = new IdentifierDt();
		identifierDt.setSystem(identifier.get("system"));
		identifierDt.setValue(identifier.get("value"));
		identifierDts.add(identifierDt);
		organization.setIdentifier(identifierDts);
		
		//set Organization active
		organization.setActive(dafOrganization.isActive());
		
		//set Organization Telecom
		Map<String, String> telecom = HapiUtils.convertToJsonMap(dafOrganization.getTelecom());
		List<ContactPointDt> contactPointDts = new ArrayList<ContactPointDt>();
		ContactPointDt contactPointDt = new ContactPointDt();
		contactPointDt.setSystem(ContactPointSystemEnum.valueOf(telecom.get("system").toUpperCase()));
		contactPointDt.setValue(telecom.get("value"));
		contactPointDts.add(contactPointDt);
		organization.setTelecom(contactPointDts);

		//Set organization Address
		Map<String, String> address = HapiUtils.convertToJsonMap(dafOrganization.getAddress());
		List<AddressDt> addressDts = new ArrayList<AddressDt>();
		AddressDt addressDt = new AddressDt();
		List<StringDt> stringDts = new ArrayList<StringDt>();
		StringDt stringDt = new StringDt();
		stringDt.setValue(address.get("line"));
		stringDts.add(stringDt);
		addressDt.setLine(stringDts);
		addressDt.setCity(address.get("city"));
		addressDt.setState(address.get("state"));
		addressDt.setPostalCode(address.get("postalCode"));
		addressDt.setCountry(address.get("country"));
		addressDts.add(addressDt);
		organization.setAddress(addressDts);
		
		//Set Organization Name
		organization.setName(dafOrganization.getName().trim());
		
		return organization;

	}

}
