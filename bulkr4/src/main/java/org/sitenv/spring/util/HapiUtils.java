package org.sitenv.spring.util;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

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
    public static CodeableConcept setCodeableConceptValues(String display, String code, String system) {

        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem(system);
        coding.setCode(code);
        coding.setDisplay(display);
        codeableConcept.addCoding(coding);

        return codeableConcept;
    }

}
