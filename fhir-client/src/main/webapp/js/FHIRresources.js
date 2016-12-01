/*var fhirresources = ['DAFAllergyIntolerance','DAFDiagnosticOrder','DAFDiagnosticReport','DAFEncounter','DAFFamilyMemberHistory'
					,'DAFImmunization','DAFLabResults', 'DAFMedication', 'DAFMedicationStatement', 
					'DAFMedicationAdministration','DAFPatient','DAFCondition (Problem)'
					,'DAFProcedure','DAFSmokingStatus','DAFVitalSigns','DAFList'];*/

var fhirresources = {
	'labels':{
		'Patient':'DAFPatient',
		'AllergyIntolerance':'DAFAllergyIntolerance',
		'Immunization':'DAFImmunization',
		'MedicationStatement':'DAFMedicationStatement',
		'Condition':'DAFProblems(Condition)',
		//'Procedure':'DAFProcedure',
		'Observation(Laboratory)' : 'DAFLabResults',
		'Observation(SmokingStatus)':'DAFSmokingStatus',
		'Observation(VitalSigns)':'DAFVitalSigns',		
	},
}

