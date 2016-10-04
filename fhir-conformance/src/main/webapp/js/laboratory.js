(function(){
	var obslaboratorysuccesscount = 0;
	var labtrigger= 0;
	var obslaboratoryByPatientCategory = "obslaboratoryByPatientCategory";
	var obslaboratoryCollapseByPatientCategory = "obslaboratoryCollapseByPatientCategory";
	var obslaboratoryByPatientCode = "obslaboratoryByPatientCode";
	var obslaboratoryCollapseByPatientCode = "obslaboratoryCollapseByPatientCode";
	var obslaboratoryByPatientCategoryDate = "obslaboratoryByPatientCategoryDate";
	var obslaboratoryCollapseByPatientCategoryDate = "obslaboratoryCollapseByPatientCategoryDate";
	var obslaboratoryByPatientCategoryCodeDate = "obslaboratoryByPatientCategoryCodeDate";
	var obslaboratoryCollapseByPatientCategoryCodeDate = "obslaboratoryCollapseByPatientCategoryCodeDate";
	var l = $( '#executebtn' ).ladda();
	obslaboratoryByPatId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		obslaboratorysuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/Observation?patient="+patientid + "&category=laboratory&_format=json";
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
	          	obslaboratorysuccesscount++;
	          	$('.obslaboratoryrunByPatcategory').html('Passed');
	          	if(labtrigger == 0){
	          		$('.obslaboratoryrun').html('Passed');
	          	}
	          	$('.obslaboratorypass').html(obslaboratorysuccesscount);
	          	obslaboratoryByPatCode(strurl,data,obslaboratoryByPatientCode,obslaboratoryCollapseByPatientCode);
	          	obslaboratoryByPatCategoryDate(strurl,data,obslaboratoryByPatientCategoryDate,obslaboratoryCollapseByPatientCategoryDate);
	          	obslaboratoryByPatCategoryCodeDate(strurl,data,obslaboratoryByPatientCategoryCodeDate,obslaboratoryCollapseByPatientCategoryCodeDate);
	          	renderresults(data,strurl,xhr,obslaboratoryByPatientCategory,obslaboratoryCollapseByPatientCategory,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	labtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.obslaboratoryrunByPatcategory').html('Failed');
	        	$('.obslaboratoryrun').html('Failed');
	        	$('.obslaboratoryrun').parent().addClass('bg-danger');
	        	rendererror(e,obslaboratoryByPatientCategory);
	   		}
		})
	}

	obslaboratoryByPatCode = function(strurl,data,obslaboratoryByPatientCode,obslaboratoryCollapseByPatientCode){
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
		strurl = strurl + "/Observation?patient="+patientid+"&category=laboratory&code="+codearr.join(',')+"&_format=json";
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
	          	obslaboratorysuccesscount++;
	          	$('.obslaboratoryrunByPatCode').html('Passed');
	          	if(labtrigger == 0){
	          		$('.obslaboratoryrun').html('Passed');
	          	}
	          	$('.obslaboratorypass').html(obslaboratorysuccesscount);
	          	renderresults(data,strurl,xhr,obslaboratoryByPatientCode,obslaboratoryCollapseByPatientCode,resType);
	        },
	        error:function(e){
	        	labtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.obslaboratoryrunByPatCode').html('Failed');
	        	$('.obslaboratoryrun').html('Failed');
	        	$('.obslaboratoryrun').parent().addClass('bg-danger');
	        	rendererror(e,obslaboratoryByPatientCode);
	   		}
		});
	}

	obslaboratoryByPatCategoryDate = function(strurl,data,obslaboratoryByPatientCategoryDate,obslaboratoryCollapseByPatientCategoryDate){
		var datearr;
			if(data.entry){
				datearr = data.entry[0].resource.effectiveDateTime;
			}else{
				datearr = data.effectiveDateTime;
			}
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Observation?patient="+patientid+"&category=laboratory&date="+datearr+"&_format=json";
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
	          	obslaboratorysuccesscount++;
	          	$('.obslaboratoryrunByPatCategoryDate').html('Passed');
	          	if(labtrigger == 0){
	          		$('.obslaboratoryrun').html('Passed');
	          	}
	          	$('.obslaboratorypass').html(obslaboratorysuccesscount);
	          	renderresults(data,strurl,xhr,obslaboratoryByPatientCategoryDate,obslaboratoryCollapseByPatientCategoryDate,resType);
	        },
	        error:function(e){
	        	labtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.obslaboratoryrunByPatCategoryDate').html('Failed');
	        	$('.obslaboratoryrun').html('Failed');
	        	$('.obslaboratoryrun').parent().addClass('bg-danger');
	        	rendererror(e,obslaboratoryByPatientCategoryDate);
	   		}
		});
	}

	obslaboratoryByPatCategoryCodeDate = function(strurl,data,obslaboratoryByPatientCategoryCodeDate,obslaboratoryCollapseByPatientCategoryCodeDate){
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
		strurl = strurl + "/Observation?patient="+patientid+"&category=laboratory&code="+codearr.join(',')+"&date="+datearr+"&_format=json";
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
	          	obslaboratorysuccesscount++;
	          	$('.obslaboratoryrunByPatCategoryCodeDate').html('Passed');
	          	if(labtrigger == 0){
	          		$('.obslaboratoryrun').html('Passed');
	          	}
	          	$('.obslaboratorypass').html(obslaboratorysuccesscount);
	          	renderresults(data,strurl,xhr,obslaboratoryByPatientCategoryCodeDate,obslaboratoryCollapseByPatientCategoryCodeDate,resType);
	        },
	        error:function(e){
	        	labtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.obslaboratoryrunByPatCategoryCodeDate').html('Failed');
	        	$('.obslaboratoryrun').html('Failed');
	        	$('.obslaboratoryrun').parent().addClass('bg-danger');
	        	rendererror(e,obslaboratoryByPatientCategoryCodeDate);
	   		}
		});
	}
}).call(this);