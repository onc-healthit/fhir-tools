(function(){
	publicserverauth =  function(){
		localStorage.removeItem('access_token');
		localStorage.removeItem('patientId');
	    localStorage.removeItem('acctoken');
	    localStorage.removeItem('strurl');
		var state = Math.round(Math.random()*100000000).toString();
		var strurl = baseurl = $('#publicserverurl').val().replace(/\/$/, '');
		
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
		var publicscopes = [];
        $.each($("#publicclientscope option:selected"), function(){            
            publicscopes.push($(this).val());
        });
        var publicclientscope = publicscopes.join(",");

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
	                clientscope: publicclientscope
	            });
          		fhiroauth(publicclientid,publicclientscope,redirecturi,authurl,baseurl,state);
			},
			error:function(e){
				console.log(e);
				//l.ladda( 'stop' );
				if(e.responseText == ''){
					bootbox.alert("No Response from Server");
					return false;
				}
				var errordata = JSON.parse(e.responseText);
				//var errormessage = JSON.stringify(errordata.text.div);
				var errormessage = errordata.issue[0].details;
				bootbox.alert(errormessage);
			}
		});
	}
}).call(this);