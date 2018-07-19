(function(){
	var patientid = localStorage.getItem("patientid");


	openpatmodal = function(){
	    	$('#patresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }
	    openallergymodal = function(){
	    	$('#allergyresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openconditionmodal = function(){
	    	$('#conditionresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    opencareteammodal = function(){
	    	$('#careteamresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openmedStatementmodal = function(){
	    	$('#medicationStatementresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openmedOrdermodal = function(){
	    	$('#medicationOrderresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    opendevicemodal = function(){
	    	$('#deviceresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	     opendocrefmodal = function(){
	    	$('#docrefresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    opengoalmodal = function(){
	    	$('#goalresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openproceduremodal = function(){
	    	$('#procedureresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openimmunizationmodal = function(){
	    	$('#immunizationresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    opendiagnosticreportmodal = function(){
	    	$('#diagnosticreportresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openobslaboratorymodal = function(){
	    	$('#obslaboratoryresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    opensmokingstatusmodal = function(){
	    	$('#smokingstatusresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openvitalsignsmodal = function(){
	    	$('#vitalsignsresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	runDSTU2Tests =  function(){
		$('.pass').html('');
		$('.run').html('');
		/*localStorage.setItem("execution",true);*/
		var checkedValues = [];
		checkedValues = $('.dstu2table input:checkbox:checked').map(function() {
		    return this.value;
		}).get();
		
		if(checkedValues.length <= 0){
			bootbox.alert("Please Select atleast one Resource");
			return;
		}
		/*if($('#selectall').prop("checked") == true){
			console.log(checkedValues.slice(1));
		}else{
			console.log(checkedValues);
		}*/
		for(var i=0; i<checkedValues.length; i++){
			var strurl = localStorage.getItem("strurl");
			if(checkedValues[i] == "Patient"){
				$('.patientpass').html('');
				$('.patientrun').html('Running...');
				$('.patientrun').parent().removeClass('bg-danger');
				$('.patientremovabletr').remove();
				patientById(strurl);
			}
			if(checkedValues[i] == "AllergyIntolerance"){
				$('.allergypass').html('');
				$('.allergyrun').html('Running...');
				$('.allergyrun').parent().removeClass('bg-danger');
				$('.allergyremovabletr').remove();
				allergyByPatientId(strurl);
			}
			if(checkedValues[i] == "Condition"){
				$('.conditionpass').html('');
				$('.conditionrun').html('Running...');
				$('.conditionrun').parent().removeClass('bg-danger');
				$('.conditionremovabletr').remove();
				conditionByPatient(strurl);
			}
			if(checkedValues[i] == "CarePlan"){
				$('.careteampass').html('');
				$('.careteamrun').html('Running...');
				$('.careteamrun').parent().removeClass('bg-danger');
				$('.careteamremovabletr').remove();
				careteamByPatient(strurl);
			}
			if(checkedValues[i] == "MedicationStatement"){
				$('.medStatementpass').html('');
				$('.medStatementrun').html('Running...');
				$('.medStatementrun').parent().removeClass('bg-danger');
				$('.medStatementremovabletr').remove();
				medicationStatementByPatientId(strurl);
			}
			if(checkedValues[i] == "Device"){
				$('.devicepass').html('');
				$('.devicerun').html('Running...');
				$('.devicerun').parent().removeClass('bg-danger');
				$('.deviceremovabletr').remove();
				deviceByPatId(strurl);
			}
			if(checkedValues[i] == "DocumentReference"){
				$('.docrefpass').html('');
				$('.docrefrun').html('Running...');
				$('.docrefrun').parent().removeClass('bg-danger');
				$('.deviceremovabletr').remove();
				docrefByPatId(strurl);
			}
			if(checkedValues[i] == "Goal"){
				$('.goalpass').html('');
				$('.goalrun').html('Running...');
				$('.goalrun').parent().removeClass('bg-danger');
				$('.goalremovabletr').remove();
				goalByPatId(strurl);
			}
			if(checkedValues[i] == "Procedure"){
				$('.procedurepass').html('');
				$('.procedurerun').html('Running...');
				$('.procedurerun').parent().removeClass('bg-danger');
				$('.procedureremovabletr').remove();
				procedureByPatId(strurl);
			}
			if(checkedValues[i] == "Immunization"){
				$('.immunizationpass').html('');
				$('.immunizationrun').html('Running...');
				$('.immunizationrun').parent().removeClass('bg-danger');
				$('.immunizationremovabletr').remove();
				immunizationByPatId(strurl);
			}
			if(checkedValues[i] == "DiagnosticReport"){
				$('.diagnosticreportpass').html('');
				$('.diagnosticreportrun').html('Running...');
				$('.diagnosticreportrun').parent().removeClass('bg-danger');
				$('.diagnosticreportremovabletr').remove();
				diagnosticreportByPatId(strurl);
			}
			if(checkedValues[i] == "MedicationOrder"){
				$('.medOrderpass').html('');
				$('.medOrderrun').html('Running...');
				$('.medOrderrun').parent().removeClass('bg-danger');
				$('.medorderremovabletr').remove();
				medicationOrderByPatientId(strurl);
			}
			if(checkedValues[i] == "Observation(Laboratory)"){
				$('.obslaboratorypass').html('');
				$('.obslaboratoryrun').html('Running...');
				$('.obslaboratoryrun').parent().removeClass('bg-danger');
				$('.obslaboratoryremovabletr').remove();
				obslaboratoryByPatId(strurl);
			}
			if(checkedValues[i] == "Observation(Smoking Status)"){
				$('.smokingstatuspass').html('');
				$('.smokingstatusrun').html('Running...');
				$('.smokingstatusrun').parent().removeClass('bg-danger');
				$('.smokingstatusremovabletr').remove();
				smokingstatusByPatId(strurl);
			}
			if(checkedValues[i] == "Observation(Vital Signs)"){
				$('.vitalsignspass').html('');
				$('.vitalsignsrun').html('Running...');
				$('.vitalsignsrun').parent().removeClass('bg-danger');
				$('.vitalremovabletr').remove();
				vitalsignsByPatId(strurl);
			}
		}
	}


	// Response Validation
	renderdstu2ResponseValidation = function(collapsediv, data){
		var dstu2ValidateURL = window.location.protocol + "//" + window.location.host + "/fhirvalidator/dstu2/json/validate";

		$.ajax({
			type: "POST",
			url: dstu2ValidateURL,
			data: JSON.stringify(data), //pass data to second request
			contentType: "application/json; charset=utf-8",
			traditional: true,
			success: function(valData){
				valbodyDiv = $('<div class="col-md-12"><label class="col-md-3">Response Validation:</label></div>');
				valbodyDivres = $('<div class="col-md-9" style="word-break:break-all"><pre class="validation more">'+valData+'</pre></div>');
				valbodyDiv.append(valbodyDivres);
				collapsediv.append(valbodyDiv);
				$('.validation').shorten();
			} // handler if second request succeeds
		});
	}

}).call(this);