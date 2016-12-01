(function(){
	publicserverauth =  function(){
		localStorage.removeItem('access_token');
		localStorage.removeItem('patientId');
	    localStorage.removeItem('acctoken');
	    localStorage.setItem("acctoken",false);
	    localStorage.removeItem('strurl');
	    var typeofauth = 'Public';
		var state = Math.round(Math.random()*100000000).toString();
		var strurl = baseurl = $('#publicserverurl').val().replace(/\/$/, '');
		strurl = baseurl = strurl.trim();
		if(baseurl == '' || baseurl == undefined){
			bootbox.alert("Please enter FHIR Server URL");
			return false;
		}
		var publicclientid = $('#publicclientid').val();
		if(publicclientid == '' || publicclientid == undefined){
			bootbox.alert("Please enter Client ID");
			return false;
		}
		console.log(publicclientid);
		var publicclientsecret = '';
		/*var publicscopes = [];
        $.each($("#publicclientscope option:selected"), function(){            
            publicscopes.push($(this).val());
        });*/
        var val = [];
        $('.publicscopes :checkbox:checked').each(function(i){
          val[i] = $(this).val();
        });
        console.log(val);
        if(val.length <= 0){
        	bootbox.alert("Please select atleast one Scope");
        	return false;
        }
        var publicclientscope = val.join(",");
        console.log(publicclientscope);
        var redirecturi = location.protocol + '//' + location.host + location.pathname;
		$.ajax({
			url:baseurl+"/metadata?_format=json",
			type:"GET",
			headers:{
	          "Content-Type":"application/json+fhir"
	        },
			success:function(data){
				console.log(data);
				//var data = JSON.parse(data);
				if(typeof data == "string"){
		            console.log("in if");
		            var data = JSON.parse(data);
		        }
		        console.log(data);
				var arg = data.rest[0].security.extension[0].extension;
		        for(var i=0;i<arg.length;i++){
		      	    if (arg[i].url === "register") {
		      	    	regurl = arg[i].valueUri;
		            } else if (arg[i].url === "authorize") {
		              	authurl = arg[i].valueUri;
		            } else if (arg[i].url === "token") {
		              	tokenurl = arg[i].valueUri;
		            }
		        }
		        sessionStorage[state] = JSON.stringify({
	                clientid: publicclientid,
	                clientsecret : publicclientsecret,
	                redirecturi: redirecturi,
	                tokenurl: tokenurl,
	                strurl: strurl,
	                clientscope: publicclientscope,
	                typeofauth: typeofauth
	            });
          		fhiroauth(publicclientid,publicclientscope,redirecturi,authurl,baseurl,state);
			},
			error:function(e){
				console.log()
				if(e.responseText == ''){
					bootbox.alert("Invalid Authentication - Please provide valid details");
					return false;
				}else if(e.status == '404'){
					bootbox.alert("Invalid Authentication - Please provide valid details");
					return false;
				}else{
					bootbox.alert("Invalid Authentication - Please provide valid details");
					return false;
				}
			}
		});
	}
}).call(this);