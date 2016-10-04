(function(){
	var vitalsignssuccesscount = 0;
	var vitaltrigger = 0;
	var vitalsignsByPatientCategory = "vitalsignsByPatientCategory";
	var vitalsignsCollapseByPatientCategory = "vitalsignsCollapseByPatientCategory";
	var vitalsignsByPatientCode = "vitalsignsByPatientCode";
	var vitalsignsCollapseByPatientCode = "vitalsignsCollapseByPatientCode";
	var vitalsignsByPatientCategoryDate = "vitalsignsByPatientCategoryDate";
	var vitalsignsCollapseByPatientCategoryDate = "vitalsignsCollapseByPatientCategoryDate";
	var vitalsignsByPatientCategoryCodeDate = "vitalsignsByPatientCategoryCodeDate";
	var vitalsignsCollapseByPatientCategoryCodeDate = "vitalsignsCollapseByPatientCategoryCodeDate";
	var l = $( '#executebtn' ).ladda();
	vitalsignsByPatId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		vitalsignssuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/Observation?patient="+patientid + "&category=vital-signs&_format=json";
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	vitalsignssuccesscount++;
	          	$('.vitalsignsrunByPatcategory').html('Passed');
	          	if(vitaltrigger == 0){
	          		$('.vitalsignsrun').html('Passed');
	          	}
	          	$('.vitalsignspass').html(vitalsignssuccesscount);
	          	vitalsignsByPatCode(strurl,data,vitalsignsByPatientCode,vitalsignsCollapseByPatientCode);
	          	vitalsignsByPatCategoryDate(strurl,data,vitalsignsByPatientCategoryDate,vitalsignsCollapseByPatientCategoryDate);
	          	vitalsignsByPatCategoryCodeDate(strurl,data,vitalsignsByPatientCategoryCodeDate,vitalsignsCollapseByPatientCategoryCodeDate);
	          	renderresults(data,strurl,xhr,vitalsignsByPatientCategory,vitalsignsCollapseByPatientCategory,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	vitaltrigger =1;
	        	l.ladda( 'stop' );
	        	$('.vitalsignsrunByPatcategory').html('Failed');
	        	$('.vitalsignsrun').html('Failed');
	        	$('.vitalsignsrun').parent().addClass('bg-danger');
	        	rendererror(e,vitalsignsByPatientCategory);
	   		}
		})
	}

	vitalsignsByPatCode = function(strurl,data,vitalsignsByPatientCode,vitalsignsCollapseByPatientCode){
		var codearr=[];
			if(data.entry){
				for(var i=0; i< data.entry.length; i++){
					codearr.push(data.entry[i].resource.code.coding[0].code);
				}
			}else{
				codearr.push(data.code.coding[0].code);
			}
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Observation?patient="+patientid+"&category=vital-signs&code="+codearr.join(',')+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	vitalsignssuccesscount++;
	          	$('.vitalsignsrunByPatCode').html('Passed');
	          	if(vitaltrigger == 0){
	          		$('.vitalsignsrun').html('Passed');
	          	}
	          	$('.vitalsignspass').html(vitalsignssuccesscount);
	          	renderresults(data,strurl,xhr,vitalsignsByPatientCode,vitalsignsCollapseByPatientCode,resType);
	        },
	        error:function(e){
	        	vitaltrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.vitalsignsrunByPatCode').html('Failed');
	        	$('.vitalsignsrun').html('Failed');
	        	$('.vitalsignsrun').parent().addClass('bg-danger');
	        	rendererror(e,vitalsignsByPatientCode);
	   		}
		});
	}

	vitalsignsByPatCategoryDate = function(strurl,data,vitalsignsByPatientCategoryDate,vitalsignsCollapseByPatientCategoryDate){
		var datearr;
			if(data.entry){
				datearr = data.entry[0].resource.effectiveDateTime;
			}else{
				datearr = data.effectiveDateTime;
			}
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Observation?patient="+patientid+"&category=vital-signs&date="+datearr+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	vitalsignssuccesscount++;
	          	$('.vitalsignsrunByPatCategoryDate').html('Passed');
	          	if(vitaltrigger == 0){
	          		$('.vitalsignsrun').html('Passed');
	          	}
	          	$('.vitalsignspass').html(vitalsignssuccesscount);
	          	renderresults(data,strurl,xhr,vitalsignsByPatientCategoryDate,vitalsignsCollapseByPatientCategoryDate,resType);
	        },
	        error:function(e){
	        	vitaltrigger =1;
	        	l.ladda( 'stop' );
	        	$('.vitalsignsrunByPatCategoryDate').html('Failed');
	        	$('.vitalsignsrun').html('Failed');
	        	$('.vitalsignsrun').parent().addClass('bg-danger');
	        	rendererror(e,vitalsignsByPatientCategoryDate);
	   		}
		});
	}

	vitalsignsByPatCategoryCodeDate = function(strurl,data,vitalsignsByPatientCategoryCodeDate,vitalsignsCollapseByPatientCategoryCodeDate){
		var codearr= [];
		var datearr;
			if(data.entry){
				datearr = data.entry[0].resource.effectiveDateTime;
				for(var i=0; i< data.entry.length; i++){
					codearr.push(data.entry[i].resource.code.coding[0].code);
				}
			}else{
				datearr = data.effectiveDateTime;
				codearr.push(data.code.coding[0].code);
			}
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Observation?patient="+patientid+"&code="+codearr.join(',')+"&date="+datearr+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	vitalsignssuccesscount++;
	          	$('.vitalsignsrunByPatCategoryCodeDate').html('Passed');
	          	if(vitaltrigger == 0){
	          		$('.vitalsignsrun').html('Passed');
	          	}
	          	$('.vitalsignspass').html(vitalsignssuccesscount);
	          	renderresults(data,strurl,xhr,vitalsignsByPatientCategoryCodeDate,vitalsignsCollapseByPatientCategoryCodeDate,resType);
	        },
	        error:function(e){
	        	vitaltrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.vitalsignsrunByPatCategoryCodeDate').html('Failed');
	        	$('.vitalsignsrun').html('Failed');
	        	$('.vitalsignsrun').parent().addClass('bg-danger');
	        	rendererror(e,vitalsignsByPatientCategoryCodeDate);
	   		}
		});
	}
}).call(this);