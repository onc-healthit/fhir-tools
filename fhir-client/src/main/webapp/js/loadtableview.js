(function(){
	loadtableview =  function(resourcename,data,tab){
    	$("#tablediv").children().remove();
    	results = $('#tablediv');
    	if(resourcename == 'Patient'){
    		table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
		   	thead = $('<thead></thead>');
		   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Identifier</th><th>Name</th><th>Gender</th><th>BirthDate</th><th>Communication</th><th>Race</th><th>Ethnicity</th><th>Birth Sex</th></tr>');
		   	thead.append(tr);
		   	table.append(thead);
		   	results.append(table);
		   	var noofresults = [];
		   	console.log(data);
		   	if(tab == '#search'){
		   		//noofresults = data.entry;
		   		if(data.entry){
		   			noofresults = data.entry;
		   		}
		   	}else{
		   		if(data.entry){
		   			noofresults = data.entry	
		   		}else{
		   			noofresults.push(data);
		   		}
		   	}
		   	console.log(noofresults);
		   	for(var i=0;i<noofresults.length;i++){
		   		var jsondata = {};
		   		if(tab == '#search'){
			   		/*jsondata = noofresults[i].resource;*/
			   		if(data.entry){
			   			jsondata = noofresults[i].resource;
			   		}
			   	}else{
			   		if(data.entry){
			   			jsondata = noofresults[i].resource;
			   		}else{
			   			jsondata = noofresults[i];
			   		}
			   	}
		   		var tr=$("<tr/>");
		   		var td1 = $('<td></td>');
		   		if(jsondata.id){
		   			td1.append(jsondata.id);
		   		}
		        /*var td1=$("<td>"+jsondata.id+"</td>");*/
		        var td2 = $('<td></td>');
		        if(jsondata.identifier){
		        	if(typeof jsondata.identifier == 'object'){
			        	var identifier = jsondata.identifier;
			        	var identifierol = $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        	var identifiertd1 = "<li><b>System</b>: "+jsondata.identifier[0].system+"</li>";
			        	var identifiertd2 = "<li><b>Value</b>:"+jsondata.identifier[0].value+"</li>";
			        	identifierol.append(identifiertd1,identifiertd2);
			        	td2.append(identifierol);
			        }
		        }
		        var td3 = $('<td></td>');
		        if(jsondata.name){
		        	if(typeof jsondata.name == 'object'){
			        	var name = jsondata.name[0];
			        	var nameol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        	var nametd1 = "<li><b>Family</b>: "+jsondata.name[0].family+"</li>";
			        	var nametd2 = "<li><b>Given</b>: "+jsondata.name[0].given+"</li>";
			        	nameol.append(nametd1,nametd2);
			        	td3.append(nameol);
			        }
		        }
		        var td4=$("<td></td>");
		        if(jsondata.gender){
		        	td4.append(jsondata.gender);
		        }
		        var td5 = $("<td></td>");
		        if(jsondata.birthDate){
		        	td5.append(jsondata.birthDate);	
		        }
		        var td6 = $('<td></td>');
		        if(jsondata.communication){
		        	if(typeof jsondata.communication == 'object'){
			        	var comm = jsondata.communication[0];
			        	var commol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        	var commtd1 = "<li><b>Language</b>: "+jsondata.communication[0].language.coding[0].display+"</li>";
			        	commol.append(commtd1);
			        	td6.append(commol);
			        }
		        }
		        var td7 = $('<td></td>');
		        if(jsondata.extension){
		        	if(typeof jsondata.extension == 'object'){
			        	var extcount1 =  jsondata.extension;
			        	var raceol = null;
			        	for(var m=0; m<extcount1.length; m++){
			        		if(extcount1[m].url == 'http://hl7.org/fhir/StructureDefinition/us-core-race'){
			        			raceol = $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        			var racetd1 = $("<li></li>");
								if(extcount1[m].valueCodeableConcept&&extcount1[m].valueCodeableConcept.coding){
									if(extcount1[m].valueCodeableConcept.coding[0].system){
										racetd1.append("<b>System</b>: "+extcount1[m].valueCodeableConcept.coding[0].system);
									}
									var racetd2 = $("<li></li>");
									if(extcount1[m].valueCodeableConcept.coding[0].code){
										racetd2.append("<b>Code</b>: "+extcount1[m].valueCodeableConcept.coding[0].code);
									}
									var racetd3 = $("<li></li>");
									if(extcount1[m].valueCodeableConcept.coding[0].display){
										racetd3.append("<b>Display</b>: "+extcount1[m].valueCodeableConcept.coding[0].display);
									}
									raceol.append(racetd1,racetd2,racetd3);	  
								}
			        			      			
			        		}
			        	}
			        	td7.append(raceol);
			        }
		        }
		        /*var td7=$("<td>"+noofresults[i].resource.race+"</td>");*/
		        var td8 = $('<td></td>');
		        if(jsondata.extension){
		        	if(typeof jsondata.extension == 'object'){
			        	var extcount2 =  jsondata.extension;
			        	var ethnicityol = null;
			        	for(var k=0; k<extcount2.length; k++){
			        		if(extcount2[k].url == 'http://hl7.org/fhir/StructureDefinition/us-core-ethnicity'){
			        			ethnicityol = $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        			var ethnicitytd1 = $("<li></li>");
								if(extcount2[k].valueCodeableConcept&&extcount2[k].valueCodeableConcept.coding){
									if(extcount2[k].valueCodeableConcept.coding[0].system){
										ethnicitytd1.append("<b>System</b>: "+extcount2[k].valueCodeableConcept.coding[0].system);
									}
									var ethnicitytd2 = $("<li></li>");
									if(extcount2[k].valueCodeableConcept.coding[0].code){
										ethnicitytd2.append("<b>Code</b>: "+extcount2[k].valueCodeableConcept.coding[0].code);
									}
									var ethnicitytd3 = $("<li></li>");
									if(extcount2[k].valueCodeableConcept.coding[0].display){
										ethnicitytd3.append("<b>Display</b>: "+extcount2[k].valueCodeableConcept.coding[0].display);
									}
									ethnicityol.append(ethnicitytd1,ethnicitytd2,ethnicitytd3);
								}
			        			
			        		}
			        	}
			        	td8.append(ethnicityol);
			        }
		        }
		        var td9 = $('<td></td>');
		        tr.append(td1,td2,td3,td4,td5,td6,td7,td8,td9);
		       $("#JSONtable").append(tr);
		   	}
    	}
    	

    	if(resourcename == 'Condition'){

    		table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
		   	thead = $('<thead></thead>');
		   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Clinical Status</th><th>Verification Status</th><th>Code</th><th>Subject</th></tr>');
		   	thead.append(tr);
		   	table.append(thead);
		   	results.append(table);
		   	var noofresults = [];
		   	if(tab == '#search'){
		   		/*noofresults = data.entry;*/
		   		if(data.entry){
		   			noofresults = data.entry;
		   		}
		   	}else{
		   		if(data.entry){
		   			noofresults = data.entry	
		   		}else{
		   			noofresults.push(data);
		   		}
		   	}
		   	for(var i=0;i<noofresults.length;i++){
		   		var jsondata = {};
		   		if(tab == '#search'){
			   		jsondata = noofresults[i].resource;
			   	}else{
			   		if(data.entry){
			   			jsondata = noofresults[i].resource;
			   		}else{
			   			jsondata = noofresults[i];
			   		}
			   	}
		   		var tr=$("<tr/>");
		   		var td1 = $("<td></td>");
		   		if(jsondata.id){
		   			td1.append(jsondata.id);
		   		}
		   		var td2 = $("<td></td>");
		   		if(jsondata.clinicalStatus){
		   			td2.append(jsondata.clinicalStatus);
		   		}
		   		var td3 = $("<td></td>");
		   		if(jsondata.verificationStatus){
		   			td3.append(jsondata.verificationStatus);
		   		}
		        var td4=$("<td></td>");
		        if(jsondata.code){
		        	if(typeof jsondata.code == 'object'){
			        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        	var codetd1 = $("<li></li>");
						if(jsondata.code.coding){
							if(jsondata.code.coding[0].system){
								codetd1.append("<b>System</b>: "+jsondata.code.coding[0].system);
							}
							var codetd2 = $("<li></li>");
							if(jsondata.code.coding[0].code){
								codetd2.append("<b>Code</b>: "+jsondata.code.coding[0].code);
							}
							var codetd3 = $("<li></li>");
							if(jsondata.code.coding[0].display){
								codetd3.append("<b>Display</b>: "+jsondata.code.coding[0].display);
							}
							codeol.append(codetd1,codetd2,codetd3);
							td4.append(codeol);
						}
			        	
			        }
		        }
		        var td5 = $("<td></td>");
		        if(jsondata.patient){
		        	td5.append(jsondata.patient.reference);
		        }
		        tr.append(td1,td2,td3,td4,td5);
		       $("#JSONtable").append(tr);
		   	}
    	}

    	if(resourcename == 'Procedure'){

    		table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
		   	thead = $('<thead></thead>');
		   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Subject</th><th>Status</th><th>Code</th><th>Performed Date Time</th></tr>');
		   	thead.append(tr);
		   	table.append(thead);
		   	results.append(table);
		   	var noofresults = [];
		   	if(tab == '#search'){
		   		/*noofresults = data.entry;*/
		   		if(data.entry){
		   			noofresults = data.entry;
		   		}
		   	}else{
		   		if(data.entry){
		   			noofresults = data.entry	
		   		}else{
		   			noofresults.push(data);
		   		}
		   	}
		   	for(var i=0;i<noofresults.length;i++){
		   		var jsondata = {};
		   		if(tab == '#search'){
			   		jsondata = noofresults[i].resource;
			   	}else{
			   		if(data.entry){
			   			jsondata = noofresults[i].resource;
			   		}else{
			   			jsondata = noofresults[i];
			   		}
			   	}
		   		var tr=$("<tr/>");
		   		var td1 = $("<td></td>");
		   		if(jsondata.id){
		   			td1.append(jsondata.id);
		   		}
		   		var td2 = $("<td></td>");
		   		if(jsondata.subject){
		   			td2.append(jsondata.subject.reference);
		   		}
		   		var td3 = $("<td></td>");
		   		if(jsondata.status){
		   			td3.append(jsondata.status);
		   		}
		        var td4=$("<td></td>");
		        if(jsondata.code){
		        	if(typeof jsondata.code == 'object'){
			        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        	var codetd1 = $("<li></li>");
						if(jsondata.code.coding){
							if(jsondata.code.coding[0].system){
								codetd1.append("<b>System</b>: "+jsondata.code.coding[0].system);
							}
							var codetd2 = $("<li></li>");
							if(jsondata.code.coding[0].code){
								codetd2.append("<b>Code</b>: "+jsondata.code.coding[0].code);
							}
							var codetd3 = $("<li></li>");
							if(jsondata.code.coding[0].display){
								codetd3.append("<b>Display</b>: "+jsondata.code.coding[0].display);
							}
							codeol.append(codetd1,codetd2,codetd3);
							td4.append(codeol);
						}
			        	
			        }
		        }
		        var td5 = $("<td></td>");
		        if(jsondata.performedDateTime){
		        	td5.append(jsondata.performedDateTime)
		        }
		        tr.append(td1,td2,td3,td4,td5);
		       $("#JSONtable").append(tr);
		   	}
    	}

    	if(resourcename == 'AllergyIntolerance'){

    		table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
		   	thead = $('<thead></thead>');
		   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Status</th><th>Code</th><th>Patient</th></tr>');
		   	thead.append(tr);
		   	table.append(thead);
		   	results.append(table);
		   	var noofresults = [];
		   	if(tab == '#search'){
		   		if(data.entry){
		   			noofresults = data.entry;
		   		}
		   	}else{
		   		if(data.entry){
		   			noofresults = data.entry	
		   		}else{
		   			noofresults.push(data);
		   		}
		   	}
		   	for(var i=0;i<noofresults.length;i++){
		   		var jsondata = {};
		   		if(tab == '#search'){
			   		jsondata = noofresults[i].resource;
			   	}else{
			   		if(data.entry){
			   			jsondata = noofresults[i].resource;
			   		}else{
			   			jsondata = noofresults[i];
			   		}
			   	}
		   		var tr=$("<tr/>");
		        var td1 = $("<td></td>");
		   		if(jsondata.id){
		   			td1.append(jsondata.id);
		   		}
		   		var td2 = $("<td></td>");
		   		if(jsondata.status){
		   			td2.append(jsondata.status);
		   		}
		        var td3=$("<td></td>");
		        if(jsondata.substance){
		        	if(typeof jsondata.substance == 'object'){
			        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        	var codetd1 = $("<li></li>");
						if(jsondata.substance.coding){
							if(jsondata.substance.coding[0].system){
								codetd1.append("<b>System</b>: "+jsondata.substance.coding[0].system);
							}
							var codetd2 = $("<li></li>");
							if(jsondata.substance.coding[0].code){
								codetd2.append("<b>Code</b>: "+jsondata.substance.coding[0].code);
							}
							var codetd3 = $("<li></li>");
							if(jsondata.substance.coding[0].display){
								codetd3.append("<b>Display</b>: "+jsondata.substance.coding[0].display);
							}
							codeol.append(codetd1,codetd2,codetd3);
							td3.append(codeol);
						}
			        	
			        }
		        }
		        var td4 = $("<td></td>");
		        if(jsondata.patient){
		        	td4.append(jsondata.patient.reference);
		        }
		        tr.append(td1,td2,td3,td4);
		       $("#JSONtable").append(tr);
		   	}
    	}


    	if(resourcename == 'Immunization'){

    		table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
		   	thead = $('<thead></thead>');
		   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Status</th><th>Date</th><th>Vaccine Code</th><th>Patient</th><th>Was not given</th><th>Reported</th></tr>');
		   	thead.append(tr);
		   	table.append(thead);
		   	results.append(table);
		   	var noofresults = [];
		   	if(tab == '#search'){
		   		/*noofresults = data.entry;*/
		   		if(data.entry){
		   			noofresults = data.entry;
		   		}
		   	}else{
		   		if(data.entry){
		   			noofresults = data.entry	
		   		}else{
		   			noofresults.push(data);
		   		}
		   	}
		   	for(var i=0;i<noofresults.length;i++){
		   		var jsondata = {};
		   		if(tab == '#search'){
			   		jsondata = noofresults[i].resource;
			   	}else{
			   		if(data.entry){
			   			jsondata = noofresults[i].resource;
			   		}else{
			   			jsondata = noofresults[i];
			   		}
			   	}
		   		var tr=$("<tr/>");
		        var td1 = $("<td></td>");
		   		if(jsondata.id){
		   			td1.append(jsondata.id);
		   		}
		        var td2 = $("<td></td>");
		        if(jsondata.status){
		        	td2.append(jsondata.status);
		        }
		        var td3 = $("<td></td>");
		        if(jsondata.date){
		        	td3.append(jsondata.date);
		        }
		        var td4=$("<td></td>");
		        if(jsondata.vaccineCode){
		        	if(typeof jsondata.vaccineCode == 'object'){
			        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        	var codetd1 = $("<li></li>");
						if(jsondata.vaccineCode.coding){
							if(jsondata.vaccineCode.coding[0].system){
								codetd1.append("<b>System</b>: "+jsondata.vaccineCode.coding[0].system);
							}
							var codetd2 = $("<li></li>");
							if(jsondata.vaccineCode.coding[0].code){
								codetd2.append("<b>Code</b>: "+jsondata.vaccineCode.coding[0].code);
							}
							var codetd3 = $("<li></li>");
							if(jsondata.vaccineCode.coding[0].display){
								codetd3.append("<b>Display</b>: "+jsondata.vaccineCode.coding[0].display);
							}
							codeol.append(codetd1,codetd2,codetd3);
							td4.append(codeol);
						}
			        	
			        }
		        }
		        var td5= $("<td></td>");
		        if(jsondata.patient){
		        	td5.append(jsondata.patient.reference);
		        }
		        var td6 = $("<td></td>");
		        if("wasNotGiven" in jsondata){
		        	td6.append(jsondata.wasNotGiven.toString());
		        }
		        var td7 = $("<td></td>");
		        if("reported" in jsondata){
		        	td7.append(jsondata.reported.toString());
		        }
		        tr.append(td1,td2,td3,td4,td5,td6,td7);
		       $("#JSONtable").append(tr);
		   	}
    	}


    	if(resourcename == 'MedicationStatement'){

    		table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
		   	thead = $('<thead></thead>');
		   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Status</th><th>Medication</th><th>Patient</th><th>Effective Date/Period</th></tr>');
		   	thead.append(tr);
		   	table.append(thead);
		   	results.append(table);
		   	var noofresults = [];
		   	if(tab == '#search'){
		   		/*noofresults = data.entry;*/
		   		if(data.entry){
		   			noofresults = data.entry;
		   		}
		   	}else{
		   		if(data.entry){
		   			noofresults = data.entry	
		   		}else{
		   			noofresults.push(data);
		   		}
		   	}
		   	for(var i=0;i<noofresults.length;i++){
		   		var jsondata = {};
		   		if(tab == '#search'){
			   		jsondata = noofresults[i].resource;
			   	}else{
			   		if(data.entry){
			   			jsondata = noofresults[i].resource;
			   		}else{
			   			jsondata = noofresults[i];
			   		}
			   	}
		   		var tr=$("<tr/>");
		        var td1 = $("<td></td>");
		   		if(jsondata.id){
		   			td1.append(jsondata.id);
		   		}
		        var td2 = $("<td></td>");
		        if(jsondata.status){
		        	td2.append(jsondata.status);
		        }
		        var td3=$("<td></td>");
		        if(jsondata.medicationCodeableConcept){
		        	if(typeof jsondata.medicationCodeableConcept == 'object'){
			        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
			        	var codetd1 = $("<li></li>");
						if(jsondata.medicationCodeableConcept.coding){
							if(jsondata.medicationCodeableConcept.coding[0].system){
								codetd1.append("<b>System</b>: "+jsondata.medicationCodeableConcept.coding[0].system);
							}
							var codetd2 = $("<li></li>");
							if(jsondata.medicationCodeableConcept.coding[0].code){
								codetd2.append("<b>Code</b>: "+jsondata.medicationCodeableConcept.coding[0].code);
							}
							var codetd3 = $("<li></li>");
							if(jsondata.medicationCodeableConcept.coding[0].display){
								codetd3.append("<b>Display</b>: "+jsondata.medicationCodeableConcept.coding[0].display);
							}
							codeol.append(codetd1,codetd2,codetd3);
							td3.append(codeol);
						}
			        	
			        	
			        }
		        }
		        var td4=$("<td></td>");
		        if(jsondata.patient){
		        	td4.append(jsondata.patient.reference);
		        }
		        var td5=$("<td></td>");
		        if(jsondata.effectivePeriod){
		        	td5.append(jsondata.effectivePeriod.start);
		        }
		        tr.append(td1,td2,td3,td4,td5);
		       $("#JSONtable").append(tr);
		   	}
    	}


    	if(resourcename == 'Observation'){

    		localStorage.getItem("observCategory");

    		if(localStorage.getItem("observCategory") == 'laboratory'){
    			table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
			   	thead = $('<thead></thead>');
			   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Status</th><th>Category</th><th>Code</th><th>Subject</th><th>Effective Date/Period</th><th>Value Quantity</th></tr>');
			   	thead.append(tr);
			   	table.append(thead);
			   	results.append(table);
			   	var noofresults = [];
			   	if(tab == '#search'){
			   		/*noofresults = data.entry;*/
			   		if(data.entry){
			   			noofresults = data.entry;
			   		}
			   	}else{
			   		if(data.entry){
			   			noofresults = data.entry	
			   		}else{
			   			noofresults.push(data);
			   		}
			   	}
			   	for(var i=0;i<noofresults.length;i++){
			   		var jsondata = {};
			   		if(tab == '#search'){
				   		jsondata = noofresults[i].resource;
				   	}else{
				   		if(data.entry){
				   			jsondata = noofresults[i].resource;
				   		}else{
				   			jsondata = noofresults[i];
				   		}
				   	}
			   		var tr=$("<tr/>");
			   		var td1 = $("<td></td>");
			        if(jsondata.id){
		   				td1.append(jsondata.id);
			   		}
			        var td2 = $("<td></td>");
			        if(jsondata.status){
			        	td2.append(jsondata.status);
			        }
			        var td3=$("<td></td>");
			        if(jsondata.category){
			        	if(typeof jsondata.category == 'object'){
				        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
				        	var codetd1 = $("<li></li>");
							if(jsondata.category.coding){
								if(jsondata.category.coding[0].system){
									codetd1.append("<b>System</b>: "+jsondata.category.coding[0].system);
								}
								var codetd2 = $("<li></li>");
								if(jsondata.category.coding[0].code){
									codetd2.append("<b>Code</b>: "+jsondata.category.coding[0].code);
								}
								var codetd3 = $("<li></li>");
								if(jsondata.category.coding[0].display){
									codetd3.append("<b>Display</b>: "+jsondata.category.coding[0].display);
								}
								codeol.append(codetd1,codetd2,codetd3);
								td3.append(codeol);
							}
				        	
				        }
				    }
			        var td4=$("<td></td>");
			        if(jsondata.code){
			        	if(typeof jsondata.code == 'object'){
				        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
				        	var codetd1 = $("<li></li>");
							if(jsondata.code.coding){
								if(jsondata.code.coding[0].system){
									codetd1.append("<b>System</b>: "+jsondata.code.coding[0].system);
								}
								var codetd2 = $("<li></li>");
								if(jsondata.code.coding[0].code){
									codetd2.append("<b>Code</b>: "+jsondata.code.coding[0].code);
								}
								var codetd3 = $("<li></li>");
								if(jsondata.code.coding[0].display){
									codetd3.append("<b>Display</b>: "+jsondata.code.coding[0].display);
								}
								codeol.append(codetd1,codetd2,codetd3);
								td4.append(codeol);
							}
				        	
				        }
			        }
			        var td5 = $("<td></td>");
			        if(jsondata.subject){
			        	td5.append(jsondata.subject.reference);
			        }
			        var td6 = $("<td></td>");
			        if(jsondata.effectiveDateTime){
			        	td6.append(jsondata.effectiveDateTime);
			        }
			        var td7=$("<td></td>");
			        if(jsondata.valueQuantity){
				        if(typeof jsondata.valueQuantity == 'object'){
				        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
				        	var codetd1 = $("<li></li>");
				        	if(jsondata.valueQuantity.system){
				        		codetd1.append("<b>System</b>: "+jsondata.valueQuantity.system);
				        	}
				        	var codetd2 = $("<li></li>");
				        	if(jsondata.valueQuantity.unit){
				        		codetd2.append("<b>Unit</b>: "+jsondata.valueQuantity.unit);
				        	}
				        	var codetd3 = $("<li></li>");
				        	if(jsondata.valueQuantity.value){
				        		codetd3.append("<b>Value</b>: "+jsondata.valueQuantity.value);
				        	}
				        	codeol.append(codetd1,codetd2,codetd3);
				        	td7.append(codeol);
				        }
			        }
			        tr.append(td1,td2,td3,td4,td5,td6,td7);
			       $("#JSONtable").append(tr);
			   	}
    		}else if(localStorage.getItem("observCategory") == 'social-history'){
    			table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
			   	thead = $('<thead></thead>');
			   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Status</th><th>Code</th><th>Subject</th><th>Issued</th><th>Value Codeable Concept</th></tr>');
			   	thead.append(tr);
			   	table.append(thead);
			   	results.append(table);
			   	var noofresults = [];
			   	if(tab == '#search'){
			   		//noofresults = data.entry;
			   		if(data.entry){
			   			noofresults = data.entry;
			   		}
			   	}else{
			   		if(data.entry){
			   			noofresults = data.entry	
			   		}else{
			   			noofresults.push(data);
			   		}
			   	}
			   	for(var i=0;i<noofresults.length;i++){
			   		var jsondata = {};
			   		if(tab == '#search'){
				   		jsondata = noofresults[i].resource;
				   	}else{
				   		if(data.entry){
				   			jsondata = noofresults[i].resource;
				   		}else{
				   			jsondata = noofresults[i];
				   		}
				   	}
			   		var tr=$("<tr/>");
			        var td1 = $("<td></td>");
			        if(jsondata.id){
		   				td1.append(jsondata.id);
			   		}
			        var td2 = $("<td></td>");
			        if(jsondata.status){
			        	td2.append(jsondata.status);
			        }
			        var td3=$("<td></td>");
			        if(jsondata.code){
			        	if(typeof jsondata.code == 'object'){
				        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
				        	var codetd1 = $("<li></li>");
							if(jsondata.code.coding){
								if(jsondata.code.coding[0].system){
									codetd1.append("<b>System</b>: "+jsondata.code.coding[0].system);
								}
								var codetd2 = $("<li></li>");
								if(jsondata.code.coding[0].code){
									codetd2.append("<b>Code</b>: "+jsondata.code.coding[0].code);
								}
								var codetd3 = $("<li></li>");
								if(jsondata.code.coding[0].display){
									codetd3.append("<b>Display</b>: "+jsondata.code.coding[0].display);
								}
								codeol.append(codetd1,codetd2,codetd3);
								td3.append(codeol);
							}
				        	
				        }
			        }
			        var td4 =$("<td></td>");
			        if(jsondata.subject){
			        	td4.append(jsondata.subject.reference);
			        }
			        var td5 = $("<td></td>");
			        if(jsondata.issued){
			        	td5.append(jsondata.issued);
			        }
			        var td6=$("<td></td>");
			        if(jsondata.valueCodeableConcept){
			        	if(typeof jsondata.valueCodeableConcept == 'object'){
				        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
				        	var codetd1 = $("<li></li>");
							if(jsondata.valueCodeableConcept.coding){
								if(jsondata.valueCodeableConcept.coding[0].system){
									codetd1.append("<b>System</b>: "+jsondata.valueCodeableConcept.coding[0].system);
								}
								var codetd2 = $("<li></li>");
								if(jsondata.valueCodeableConcept.coding[0].code){
									codetd2.append("<b>Code</b>: "+jsondata.valueCodeableConcept.coding[0].code);
								}
								var codetd3 = $("<li></li>");
								if(jsondata.valueCodeableConcept.coding[0].display){
									codetd3.append("<b>Display</b>: "+jsondata.valueCodeableConcept.coding[0].display);
								}
								codeol.append(codetd1,codetd2,codetd3);
								td6.append(codeol);
							}
				        	
				        }
			        }	
			        tr.append(td1,td2,td3,td4,td5,td6);
			       $("#JSONtable").append(tr);
			   	}
    		}else if(localStorage.getItem("observCategory") == 'vital-signs'){
    			table = $('<table class="table table-bordered table-striped" id="JSONtable" ></table>');
			   	thead = $('<thead></thead>');
			   	tr = $('<tr class="info" text-align="center"><th>Id</th><th>Status</th><th>Category</th><th>Code</th><th>Subject</th><th>Effective Date Time</th><th>Component</th></tr>');
			   	thead.append(tr);
			   	table.append(thead);
			   	results.append(table);
			   	var noofresults = [];
			   	if(tab == '#search'){
			   		//noofresults = data.entry;
			   		if(data.entry){
			   			noofresults = data.entry;
			   		}
			   	}else{
			   		if(data.entry){
			   			noofresults = data.entry	
			   		}else{
			   			noofresults.push(data);
			   		}
			   	}
			   	for(var i=0;i<noofresults.length;i++){
			   		var jsondata = {};
			   		if(tab == '#search'){
				   		jsondata = noofresults[i].resource;
				   	}else{
				   		if(data.entry){
				   			jsondata = noofresults[i].resource;
				   		}else{
				   			jsondata = noofresults[i];
				   		}
				   	}
			   		var tr=$("<tr/>");
			        var td1 = $("<td></td>");
			        if(jsondata.id){
		   				td1.append(jsondata.id);
			   		}
			        var td2 = $("<td></td>");
			        if(jsondata.status){
			        	td2.append(jsondata.status);
			        }
			        var td3=$("<td></td>");
			        if(jsondata.category){
			        	if(typeof jsondata.category == 'object'){
				        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
				        	var codetd1 = $("<li></li>");
							if(jsondata.category.coding){
								if(jsondata.category.coding[0].system){
				        		codetd1.append("<b>System</b>: "+jsondata.category.coding[0].system);
				        	}
				        	var codetd2 = $("<li></li>");
				        	if(jsondata.category.coding[0].code){
				        		codetd2.append("<b>Code</b>: "+jsondata.category.coding[0].code);
				        	}
				        	var codetd3 = $("<li></li>");
				        	if(jsondata.category.coding[0].display){
				        		codetd3.append("<b>Display</b>: "+jsondata.category.coding[0].display);
				        	}
				        	codeol.append(codetd1,codetd2,codetd3);
				        	td3.append(codeol);
							}
				        	
				        }
				    }
			        var td4=$("<td></td>");
			        if(jsondata.code){
			        	if(typeof jsondata.code == 'object'){
				        	var codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
				        	var codetd1 = $("<li></li>");
							if(jsondata.code.coding){
								if(jsondata.code.coding[0].system){
									codetd1.append("<b>System</b>: "+jsondata.code.coding[0].system);
								}
								var codetd2 = $("<li></li>");
								if(jsondata.code.coding[0].code){
									codetd2.append("<b>Code</b>: "+jsondata.code.coding[0].code);
								}
								var codetd3 = $("<li></li>");
								if(jsondata.code.coding[0].display){
									codetd3.append("<b>Display</b>: "+jsondata.code.coding[0].display);
								}
								codeol.append(codetd1,codetd2,codetd3);
								td4.append(codeol);
							}
				        	
				        }
			        }
			        var td5 = $("<td></td>");
			        if(jsondata.subject){
			        	td5.append(jsondata.subject.reference);
			        }
			        var td6 = $("<td></td>");
			        if(jsondata.effectiveDateTime){
			        	td6.append(jsondata.effectiveDateTime);
			        }
			        var td7=$("<td></td>");
			        if(jsondata.component){
			        	if(typeof jsondata.component == 'object'){
				        	var componentcount = jsondata.component;
				        	var codeol = null;
				        	for(var h=0; h< componentcount.length; h++){
				        		var codeh5 = $('<h5 style="text-decoration:underline;">Code</h5>')
				        		codeol =  $('<ol style="list-style-type:none;padding:0px;"></ol>');
				        		var codetd1 = $("<li></li>");
				        		if(componentcount[h].code&&componentcount[h].code.coding){
				        			if(componentcount[h].code.coding[0].system){
					        			codetd1.append("<b>System</b>: "+componentcount[h].code.coding[0].system);
					        		}
						        	var codetd2 = $("<li></li>");
						        	if(componentcount[h].code.coding[0].code){
						        		codetd2.append("<b>Code</b>: "+componentcount[h].code.coding[0].code);
						        	}
						        	var codetd3 = $("<li></li>");
						        	if(componentcount[h].code.coding[0].display){
						        		codetd3.append("<b>Display</b>: "+componentcount[h].code.coding[0].display);
						        	}
				        		}
					        	var valquanh5 = "<h5 style='text-decoration:underline;'>Value Quantity</h5>";
					        	var valquan1 = $("<li></li>");
					        	if(componentcount[h].valueQuantity){
					        		if(componentcount[h].valueQuantity.code){
						        		valquan1.append("<b>Code</b>: "+componentcount[h].valueQuantity.code);
						        	}
						        	var valquan2 = $("<li></li>");
						        	if(componentcount[h].valueQuantity.system){
						        		valquan2.append("<b>System</b>: "+componentcount[h].valueQuantity.system);
						        	}
						        	var valquan3 = $("<li></li>");
						        	if(componentcount[h].valueQuantity.unit){
						        		valquan3.append("<b>Unit</b>: "+componentcount[h].valueQuantity.unit);
						        	}
						        	var valquan4 =  $("<li></li>");
						        	if(componentcount[h].valueQuantity.value){
						        		valquan4.append("<b>Value</b>: "+componentcount[h].valueQuantity.value);
						        	}
					        	}
					        	codeol.append(codetd1,codetd2,codetd3,valquanh5,valquan1,valquan2,valquan3,valquan4);
					        	td7.append(codeh5,codeol);
				        	}
				        }
			        }
			        tr.append(td1,td2,td3,td4,td5,td6,td7);
			       $("#JSONtable").append(tr);
			   	}
    		}
    	}
    }
}).call(this);