package org.sitenv.spring;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafBulkDataRequest;
import org.sitenv.spring.model.DafGroup;
import org.sitenv.spring.service.BulkDataRequestService;
import org.sitenv.spring.service.GroupService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Binary;
import ca.uhn.fhir.model.dstu2.resource.Group;
import ca.uhn.fhir.model.dstu2.resource.Group.Member;
import ca.uhn.fhir.model.dstu2.valueset.GroupTypeEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

public class GroupResourceProvider implements IResourceProvider{
	
	public static final String RESOURCE_TYPE = "Group";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    GroupService service;
    BulkDataRequestService bdrService;
    
    
    public GroupResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (GroupService) context.getBean("groupService");
        bdrService = (BulkDataRequestService) context.getBean("bulkDataRequestService");
    }

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	public Class<Group> getResourceType() {
		
		return Group.class;
	}
	
	/**
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available Immunization records.
     * 
     * Ex: http://<server name>/<context>/fhir/Immunization?_pretty=true&_format=json
     */
    @Search
    public List<Group> getAllGroups(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
    	List<Group> groups = new ArrayList<Group>();
    	try {
        List<DafGroup> dafGroups = service.getAllGroups();

        

        for (DafGroup dafGroup : dafGroups) {
            groups.add(createGroupObject(dafGroup));
        }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}

        return groups;
    }
    
    @Read()
    public Group getGroupById(@IdParam IdDt theId) {

        DafGroup dafGroup = service.getGroupById(theId.getIdPartAsLong().intValue());

        Group group = createGroupObject(dafGroup);

        return group;
    }
    
    @Create
    public MethodOutcome  saveOrUpdateGroup(@ResourceParam Group group)throws JsonGenerationException, JsonMappingException, IOException {
    	 MethodOutcome retVal = new MethodOutcome();
    	try {
    	DafGroup dafGroup = convertGroupToDafGroup(group );
        
        DafGroup persistedgrp = service.saveOrUpdateGroup(dafGroup );
        String theId = Integer.toString(persistedgrp.getId());
        retVal.setId(new IdDt("Group", theId, "2.0"));
         
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return retVal;
    }
    
	@Operation(name = "$export", idempotent = true)
	public Binary patientTypeOperation(@IdParam IdDt groupId,
			@OperationParam(name = "_since") DateDt theStart, 
			//@OperationParam(name = "end") DateDt theEnd,
			@OperationParam(name = "_type") String type, RequestDetails requestDetails, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException {
	
		Binary retVal = new Binary();
		try {
		if (requestDetails.getHeader("Prefer") != null && requestDetails.getHeader("Prefer").equals("respond-async")) {
			
			Integer resourceId = groupId.getIdPartAsLong().intValue();
			Date start = null;
	         if(theStart!=null) {
	        	 start = theStart.getValue();
	         }

			DafBulkDataRequest bdr = new DafBulkDataRequest();
			bdr.setResourceName("Group");
			bdr.setResourceId(resourceId);
			bdr.setStatus("Accepted");
			bdr.setProcessedFlag(false);
			if (theStart != null) {
				bdr.setStart(theStart.getValueAsString());
			}

			bdr.setType(type);
			bdr.setRequestResource(request.getRequestURL().toString());

			DafBulkDataRequest responseBDR = bdrService.saveBulkDataRequest(bdr);

			String uri = request.getScheme() + "://" + request.getServerName()
					+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
							|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
									: ":" + request.getServerPort())
					+ request.getContextPath();

			response.setStatus(202);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(new Date());
			cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			cal.add(Calendar.DATE, 10);
	        //HTTP header date format: Thu, 01 Dec 1994 16:00:00 GMT
	        String o = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz").format( cal.getTime() );    
	        System.out.println(o);
	        response.setHeader("Expires", o);
			response.setHeader("Content-Location", uri + "/bulkdata/" + responseBDR.getRequestId());

			retVal.setContentType("application/json+fhir");
			
		} else {
			throw new UnprocessableEntityException("No Prefer Header supplied");
		}
		}catch(Exception e) {
    		e.printStackTrace();
    	}
		return retVal;

	}

    private  DafGroup convertGroupToDafGroup(Group group) throws JsonGenerationException, JsonMappingException, IOException{
		
    	 DafGroup dafGroup = new  DafGroup();
       
           //Set type
         dafGroup.setType(group.getType());
       
        //Set Actual
       dafGroup.setActual(group.getActual());
       
       //Set Name
       dafGroup.setName(group.getName());
       
        //Set Member
       FhirContext ctx = new FhirContext().forDstu2();
   	   String groupString = ctx.newJsonParser().encodeResourceToString(group);
   		
   		JSONObject obj = new JSONObject(groupString);
   		String member = obj.getJSONArray("member").toString();
   		dafGroup.setMember(member);
   		
   		return dafGroup;
    }
		
	
    /**
     * This method converts DafImmunization object to Immunization object
     */
    private Group createGroupObject(DafGroup dafGroup) {
        Group group = new Group();

           //Set Version
           group.setId(new IdDt(RESOURCE_TYPE, dafGroup.getId() + "", VERSION_ID));

           //Set type
           group.setType(GroupTypeEnum.valueOf(dafGroup.getType().toUpperCase().trim()));
           
           //Set Actual
           group.setActual(dafGroup.getActual());
           
           //Set Name
           group.setName(dafGroup.getName());
           
           //Set Member
           List<Member> members = new ArrayList<Member>();
           JSONArray jsonArr = new JSONArray(dafGroup.getMember());

		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);

			Member member = new Member();
			if (jsonObj.has("entity")&&jsonObj.getJSONObject("entity") != null) {
				ResourceReferenceDt entity = new ResourceReferenceDt();
				entity.setReference(jsonObj.getJSONObject("entity").getString("reference"));
				member.setEntity(entity);
			}

			if (jsonObj.has("period")&&jsonObj.getJSONObject("period") != null) {
				PeriodDt period = new PeriodDt();
				period.setStart(new DateTimeDt(jsonObj.getJSONObject("period").getString("start")));
				member.setPeriod(period);
			}

			members.add(member);

		}

		group.setMember(members);

           return group;
    }

}
