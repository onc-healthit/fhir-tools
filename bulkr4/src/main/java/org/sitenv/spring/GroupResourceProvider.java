package org.sitenv.spring;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafBulkDataRequest;
import org.sitenv.spring.model.DafGroup;
import org.sitenv.spring.service.BulkDataRequestService;
import org.sitenv.spring.service.GroupService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

public class GroupResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Group";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	private GroupService groupService;
	BulkDataRequestService bdrService;

	public GroupResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		groupService = (GroupService) context.getBean("groupService");
		bdrService = (BulkDataRequestService) context.getBean("bulkDataRequestService");
	}

	/**
	 * The getResourceType method comes from IResourceProvider, and must be
	 * overridden to indicate what type of resource this provider supplies.
	 */
	public Class<Group> getResourceType() {

		return Group.class;
	}

	@Operation(name = "$export", idempotent = true, manualResponse = true, global = true)
	public void patientTypeOperation(@IdParam IdType groupId, @OperationParam(name = "_since") DateRangeParam theStart,
			@OperationParam(name = "_type") String type, RequestDetails requestDetails, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		FhirContext ctx = FhirContext.forR4();
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		if (map.get("prefer") != null && map.get("accept") != null) {
			if (map.get("prefer").equals("respond-async") && map.get("accept").equals("application/fhir+json")) {
				String resourceId = groupId.getIdPart();
				DafGroup dafGroup = groupService.getGroupById(resourceId);
				List<DafBulkDataRequest> acceptOrReject = bdrService.getBulkDataRequestsByProcessedFlag(false);
				if (dafGroup != null) {
					if (acceptOrReject.isEmpty()) {
						DafBulkDataRequest bdr = new DafBulkDataRequest();
						if (theStart != null && theStart.getLowerBound() != null) {
							bdr.setStart(theStart.getLowerBound().getValueAsString());
						}
						bdr.setResourceName("Group");
						bdr.setResourceId(resourceId);
						bdr.setStatus("Accepted");
						bdr.setProcessedFlag(false);
						if (theStart != null) {
							bdr.setStart(theStart.toString());
						}
						bdr.setType(type);
						bdr.setRequestResource(request.getRequestURL().toString());
						DafBulkDataRequest responseBDR = bdrService.saveBulkDataRequest(bdr);
						String uri = request.getScheme() + "://" + request.getServerName()
								+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
										|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
												: ":" + request.getServerPort())
								+ request.getContextPath();
						response.setStatus(Constants.STATUS_HTTP_202_ACCEPTED);
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(new Date());
						cal.setTimeZone(TimeZone.getTimeZone("GMT"));
						cal.add(Calendar.DATE, 10);
						String o = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz").format(cal.getTime());
						response.setContentType(Constants.CT_JSON);
						response.setHeader("Expires", o);
						response.setHeader("Retry-After", "120");
						response.setHeader("Content-Location", uri + "/bulkdata/" + responseBDR.getRequestId());
						response.setContentType(Constants.CT_JSON);
					} else {
					//	throw new UnprocessableEntityException("group id could not be processed");
						response.setContentType(Constants.CT_JSON);
						response.setStatus(429);
						response.setHeader("Retry-After", "120");
						org.hl7.fhir.r4.model.OperationOutcome outcome = new org.hl7.fhir.r4.model.OperationOutcome();
						outcome.addIssue().setSeverity(IssueSeverity.ERROR).setDiagnostics("Too many requests!");
						String results = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
						response.getWriter().println(results);
					}
				} else {
					response.setContentType(Constants.CT_JSON);
					response.setStatus(401);
					org.hl7.fhir.r4.model.OperationOutcome outcome = new org.hl7.fhir.r4.model.OperationOutcome();
					outcome.addIssue().setSeverity(IssueSeverity.ERROR).setDiagnostics("group id could not be processed");
					String results = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
					response.getWriter().println(results);
				}

			} else {
				// throw new UnprocessableEntityException("Invalid header values!");
				response.setContentType(Constants.CT_JSON);
				response.setStatus(Constants.STATUS_HTTP_422_UNPROCESSABLE_ENTITY);
				org.hl7.fhir.r4.model.OperationOutcome outcome = new org.hl7.fhir.r4.model.OperationOutcome();
				outcome.addIssue().setSeverity(IssueSeverity.ERROR).setDiagnostics("Invalid header values!");
				String results = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
				response.getWriter().println(results);
			}
		} else {
			response.setContentType(Constants.CT_JSON);
			response.setStatus(Constants.STATUS_HTTP_422_UNPROCESSABLE_ENTITY);
			org.hl7.fhir.r4.model.OperationOutcome outcome = new org.hl7.fhir.r4.model.OperationOutcome();
			outcome.addIssue().setSeverity(IssueSeverity.ERROR).setDiagnostics("Prefer or Accepted Header is missing!");
			String results = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
			response.getWriter().println(results);
		}
	}
}
