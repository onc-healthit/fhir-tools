(function(){
	var patientid = localStorage.getItem("patientid");

	openstu3patmodal = function(){
	    	$('#stu3patresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }
	    openstu3allergymodal  = function(){
	    	$('#stu3allergyresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3conditionmodal = function(){
	    	$('#stu3conditionresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3careplanmodal = function(){
	    	$('#stu3careplanresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3careteammodal = function(){
	    	$('#stu3careteamresmodal').modal({
	    		backdrop:'static',
	    		keyboard:false
	    	})
	    }

	    openstu3medStatementmodal = function(){
	    	$('#stu3medicationStatementresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3medRequestmodal = function(){
	    	$('#stu3medicationRequestresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3devicemodal = function(){
	    	$('#stu3deviceresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	     openstu3docrefmodal = function(){
	    	$('#stu3docrefresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3goalmodal = function(){
	    	$('#stu3goalresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3proceduremodal = function(){
	    	$('#stu3procedureresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3immunizationmodal = function(){
	    	$('#stu3immunizationresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3diagnosticreportmodal = function(){
	    	$('#stu3diagnosticreportresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3obslaboratorymodal = function(){
	    	$('#stu3obslaboratoryresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3smokingstatusmodal = function(){
	    	$('#stu3smokingstatusresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	    openstu3vitalsignsmodal = function(){
	    	$('#stu3vitalsignsresmodal').modal({
				    backdrop: 'static',
				    keyboard: false
				})
	    }

	runSTU3Tests =  function(){
		$('.stu3pass').html('');
		$('.stu3run').html('');
		/*localStorage.setItem("execution",true);*/
		var checkedValues = [];
		checkedValues = $('.stu3table input:checkbox:checked').map(function() {
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
				$('.stu3patientpass').html('');
				$('.stu3patientrun').html('Running...');
				$('.stu3patientrun').parent().removeClass('bg-danger');
				$('.stu3patientremovabletr').remove();
				stu3patientById(strurl);
			}
			if(checkedValues[i] == "AllergyIntolerance"){
				$('.stu3allergypass').html('');
				$('.stu3allergyrun').html('Running...');
				$('.stu3allergyrun').parent().removeClass('bg-danger');
				$('.stu3allergyremovabletr').remove();
				stu3allergyByPatientId(strurl);
			}
			if(checkedValues[i] == "Condition"){
				$('.stu3conditionpass').html('');
				$('.stu3conditionrun').html('Running...');
				$('.stu3conditionrun').parent().removeClass('bg-danger');
				$('.stu3conditionremovabletr').remove();
				stu3conditionByPatient(strurl);
			}
			if(checkedValues[i] == "CarePlan"){
				$('.stu3careteampass').html('');
				$('.stu3careteamrun').html('Running...');
				$('.stu3careteamrun').parent().removeClass('bg-danger');
				$('.stu3careteamremovabletr').remove();
				stu3careteamByPatient(strurl);
			}
			if(checkedValues[i] == "CareTeam"){
				$('.stu3ctpass').html('');
				$('.stu3ctrun').html('Running...');
				$('.stu3ctrun').parent().removeClass('bg-danger');
				$('.stu3ctremovabletr').remove();
				stu3ctByPatient(strurl);
			}
			if(checkedValues[i] == "MedicationStatement"){
				$('.stu3medStatementpass').html('');
				$('.stu3medStatementrun').html('Running...');
				$('.stu3medStatementrun').parent().removeClass('bg-danger');
				$('.stu3medStatementremovabletr').remove();
				stu3medicationStatementByPatientId(strurl);
			}
			if(checkedValues[i] == "Device"){
				$('.stu3devicepass').html('');
				$('.stu3devicerun').html('Running...');
				$('.stu3devicerun').parent().removeClass('bg-danger');
				$('.stu3deviceremovabletr').remove();
				stu3deviceByPatId(strurl);
			}
			if(checkedValues[i] == "DocumentReference"){
				$('.stu3docrefpass').html('');
				$('.stu3docrefrun').html('Running...');
				$('.stu3docrefrun').parent().removeClass('bg-danger');
				$('.stu3deviceremovabletr').remove();
				stu3docrefByPatId(strurl);
			}
			if(checkedValues[i] == "Goal"){
				$('.stu3goalpass').html('');
				$('.stu3goalrun').html('Running...');
				$('.stu3goalrun').parent().removeClass('bg-danger');
				$('.stu3goalremovabletr').remove();
				stu3goalByPatId(strurl);
			}
			if(checkedValues[i] == "Procedure"){
				$('.stu3procedurepass').html('');
				$('.stu3procedurerun').html('Running...');
				$('.stu3procedurerun').parent().removeClass('bg-danger');
				$('.stu3procedureremovabletr').remove();
				stu3procedureByPatId(strurl);
			}
			if(checkedValues[i] == "Immunization"){
				$('.stu3immunizationpass').html('');
				$('.stu3immunizationrun').html('Running...');
				$('.stu3immunizationrun').parent().removeClass('bg-danger');
				$('.stu3immunizationremovabletr').remove();
				stu3immunizationByPatId(strurl);
			}
			if(checkedValues[i] == "DiagnosticReport"){
				$('.stu3diagnosticreportpass').html('');
				$('.stu3diagnosticreportrun').html('Running...');
				$('.stu3diagnosticreportrun').parent().removeClass('bg-danger');
				$('.stu3diagnosticreportremovabletr').remove();
				stu3diagnosticreportByPatId(strurl);
			}
			if(checkedValues[i] == "MedicationRequest"){
				$('.stu3medRequestpass').html('');
				$('.stu3medRequestrun').html('Running...');
				$('.stu3medRequestrun').parent().removeClass('bg-danger');
				$('.stu3medRequestremovabletr').remove();
				stu3medicationRequestByPatientId(strurl);
			}
			if(checkedValues[i] == "Observation(Laboratory)"){
				$('.stu3obslaboratorypass').html('');
				$('.stu3obslaboratoryrun').html('Running...');
				$('.stu3obslaboratoryrun').parent().removeClass('bg-danger');
				$('.stu3obslaboratoryremovabletr').remove();
				stu3obslaboratoryByPatId(strurl);
			}
			if(checkedValues[i] == "Observation(Smoking Status)"){
				$('.stu3smokingstatuspass').html('');
				$('.stu3smokingstatusrun').html('Running...');
				$('.stu3smokingstatusrun').parent().removeClass('bg-danger');
				$('.stu3smokingstatusremovabletr').remove();
				stu3smokingstatusByPatId(strurl);
			}
			if(checkedValues[i] == "Observation(Vital Signs)"){
				$('.stu3vitalsignspass').html('');
				$('.stu3vitalsignsrun').html('Running...');
				$('.stu3vitalsignsrun').parent().removeClass('bg-danger');
				$('.stu3vitalremovabletr').remove();
				stu3vitalsignsByPatId(strurl);
			}
		}
	}


	// Response Validation
	renderstu3ResponseValidation = function(collapsediv, data){
		var stu3validateURL = window.location.protocol + "//" + window.location.host + "/fhirvalidator/stu3/json/validate";
		
		$.ajax({
			type: "POST",
			url: stu3validateURL,
			data: JSON.stringify(data), //pass data to second request
			contentType: "application/json; charset=utf-8",
			traditional: true,
			success: function(valData){
				valbodyDiv = $('<div class="col-md-12"><label class="col-md-3">Response Validation:</label></div>');
				valbodyDivres = $('<div class="col-md-9"><pre class="validation-pre validation more">'+valData+'</pre></div>');
				valbodyDiv.append(valbodyDivres);
				collapsediv.append(valbodyDiv);
				$('.validation-pre').shorten();
			} // handler if second request succeeds
		});
	}

}).call(this);