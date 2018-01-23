package org.sitenv.spring.util;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prabhushankar.Byrapp on 8/22/2015.
 */
public final class HapiUtils {

    public static Map<String, String> convertToJsonMap(String json) {
        Map<String, String> map = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            //convert JSON string to Map
            map = mapper.readValue(json,
                    new TypeReference<HashMap<String, String>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    //set Codeable concept values
    public static CodeableConceptDt setCodeableConceptDtValues(String display, String code, String system) {

        CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
        CodingDt codingDt = new CodingDt();
        codingDt.setSystem(system);
        codingDt.setCode(code);
        codingDt.setDisplay(display);
        codeableConceptDt.addCoding(codingDt);

        return codeableConceptDt;
    }

}
