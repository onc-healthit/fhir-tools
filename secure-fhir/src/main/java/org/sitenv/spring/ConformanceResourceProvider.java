package org.sitenv.spring;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.model.dstu2.resource.Conformance.Rest;
import ca.uhn.fhir.model.dstu2.resource.Conformance.RestSecurity;
import ca.uhn.fhir.model.dstu2.resource.Conformance.Software;
import ca.uhn.fhir.model.dstu2.valueset.ConformanceResourceStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.RestfulConformanceModeEnum;
import ca.uhn.fhir.model.dstu2.valueset.RestfulSecurityServiceEnum;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.server.provider.dstu2.ServerConformanceProvider;

public class ConformanceResourceProvider extends ServerConformanceProvider{
	

	@Metadata
	public Conformance getConformance(HttpServletRequest request){
		
		String uri = request.getScheme() + "://" +
                request.getServerName() +
                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
                request.getContextPath();

		Conformance conformance = super.getServerConformance(request);
		
		conformance.setId("ONC FHIR Server");
        conformance.setUrl("/fhir/metadata");
        conformance.setVersion("2.0");
        conformance.setName("ONC Argonaut FHIR Metadata");
        conformance.setStatus(ConformanceResourceStatusEnum.ACTIVE);
        conformance.setPublisher("Office of the National Coordinator for Health Information Technology (ONC)");
        
        //Set Software
        Software software = new Software();
        software.setName("ONC Argonaut FHIR Server");
        software.setVersion("1,6");
        conformance.setSoftware(software);
        
        //Set Rest
        
        List<Rest> restList = new ArrayList<Rest>();
		Rest rest = new Rest();
		rest.setMode(RestfulConformanceModeEnum.SERVER);
		
          RestSecurity restSecurity = new RestSecurity();

          ExtensionDt conformanceExtension = new ExtensionDt(false, "http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris");
         
          conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "authorize", new UriDt(uri+"/authorize")));
          conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "token", new UriDt(uri+"/token")));
          conformanceExtension.addUndeclaredExtension(new ExtensionDt(false, "register", new UriDt(uri+"/view/newuser.html")));

          restSecurity.addUndeclaredExtension(conformanceExtension);
          
          BoundCodeableConceptDt<RestfulSecurityServiceEnum> boundCodeableConceptDt =
                  new BoundCodeableConceptDt<>(
                          RestfulSecurityServiceEnum.VALUESET_BINDER, RestfulSecurityServiceEnum.SMART_ON_FHIR);
          boundCodeableConceptDt.setText("OAuth2 using SMART-on-FHIR profile (see http://docs.smarthealthit.org)");
          restSecurity.getService().add(boundCodeableConceptDt);
          restSecurity.setCors(true);
          rest.setSecurity(restSecurity);
          
          rest.setResource(conformance.getRest().get(0).getResource());
          restList.add(rest);
          conformance.setRest(restList);
		
		return conformance;
	}

}
