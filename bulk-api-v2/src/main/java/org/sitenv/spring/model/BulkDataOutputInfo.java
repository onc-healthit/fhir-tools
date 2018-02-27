package org.sitenv.spring.model;

public class BulkDataOutputInfo {
	
	private String type;
	private String url;
	
	public BulkDataOutputInfo() {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		
		if(url.contains("Patient"))
			type = "Patient";
		else if(url.contains("AllergyIntolerance"))
			type = "AllergyIntolerance";
		else if(url.contains("CarePlan"))
			type = "CarePlan";
		else if(url.contains("Condition"))
			type = "Condition";
		else if(url.contains("Device"))
			type = "Device";
		else if(url.contains("DiagnosticReport"))
			type = "DiagnosticReport";
		else if(url.contains("DocumentReference"))
			type = "DocumentReference";
		else if(url.contains("Goal"))
			type = "Goal";
		else if(url.contains("Immunization"))
			type = "Immunization";
		else if(url.contains("Location"))
			type = "location";
		else if(url.contains("MedicationAdministration"))
			type = "MedicationAdministration";
		else if(url.contains("MedicationDispense"))
			type = "MedicationDispense";
		else if(url.contains("MedicationOrder"))
			type = "MedicationOrder";
		else if(url.contains("MedicationStatement"))
			type = "MedicationStatement";
		else if(url.contains("Medication"))
			type = "Medication";
		else if(url.contains("Observation"))
			type = "Observation";
		else if(url.contains("Organization"))
			type = "Organization";
		else if(url.contains("Procedure"))
			type = "Procedure";
		else 
			type = "Unknown Resource";
			
	}

}
