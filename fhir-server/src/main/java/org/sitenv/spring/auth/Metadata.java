package org.sitenv.spring.auth;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(urlPatterns = {"/fhir/metadata"}, displayName = "FHIR Server Meatdata")
public class Metadata extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        /** fhirtest.sitenv.org*/
        String uri = request.getScheme() + "://" +
                request.getServerName() +
                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
                request.getContextPath();
        String jsonString = "{\"resourceType\": \"Conformance\",\"rest\": [{\"security\": {\"service\":[{\"coding\":[{\"system\": \"http://hl7.org/fhir/restful-security-service\",\"code\": \"SMART-on-FHIR\"}]}],\"extension\": [{\"url\": \"http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris\", \"extension\": [{ \"url\": \"token\",\"valueUri\": \"" + uri + "/token\"  },{\"url\": \"authorize\",\"valueUri\": \"" + uri + "/authorize\"}]}]}}]}";
        /** fhirprod.sitenv.org*/
        /*String jsonString = "{\"resourceType\": \"Conformance\",\"rest\": [{\"security\": {\"service\":[{\"coding\":[{\"system\": \"http://hl7.org/fhir/restful-security-service\",\"code\": \"SMART-on-FHIR\"}]}],\"extension\": [{\"url\": \"http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris\", \"extension\": [{ \"url\": \"token\",\"valueUri\": \"https://fhirprod.sitenv.org/hapi/token\"  },{\"url\": \"authorize\",\"valueUri\": \"https://fhirprod.sitenv.org/hapi/authorize\"}]}]}}]}";*/
        response.addHeader("Content-Type", "application/json");
        JSONObject json = new JSONObject(jsonString);
        out.println(json);
    }

}
