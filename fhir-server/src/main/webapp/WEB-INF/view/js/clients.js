(function(){
	//var main_url ="http://localhost:8000/hapi";
	//var main_url = "https://fhirtest.direct.sitenv.org/hapi";
	//var main_url = "https://fhirtest.sitenv.org/hapi-resprint";
	var main_url = geturl();
	registerclient = function(){
		var contactname = $('#contactname').val();
		if(contactname == ''){
        	bootbox.alert("Please Enter Contact Name");
        	return false;
        }
		var contactemail = $('#contactemail').val();
		if(contactemail == ''){
        	bootbox.alert("Please Enter Contact Email");
        	return false;
        }
		var orgName = $('#orgName').val();
		if(orgName == ''){
        	bootbox.alert("Please Enter Organization Name");
        	return false;
        }
		var clientname = $('#clientname').val();
		if(clientname == ''){
        	bootbox.alert("Please Enter Client Name");
        	return false;
        }
		var redirecturi = $('#redirecturi').val();
		if(redirecturi == ''){
        	bootbox.alert("Please Enter Redirect Uri");
        	return false;
        }
		var scopes = [];
        $.each($("#clientscope option:selected"), function(){            
            scopes.push($(this).val());
        });
        if(scopes.length <= 0){
        	bootbox.alert("Please Select Scopes");
        	return false;
        }
		var clientscope = scopes.join(",");
		var data = {"userId":userId,"contact_name" : contactname , "contact_mail" : contactemail , "org_name" : orgName , "name" : clientname , "redirect_uri" : redirecturi , "scope" : clientscope}
	    $.ajax({
	      url:main_url+"/client/",
	      type:"POST",
	      headers:{
	      	"Content-Type":"application/json"
	      },
	      data:JSON.stringify(data),
	      success:function(data){
	      	$('input[type="text"],input[type="password"]').val('');
	      	document.getElementById("clientscope").selectedIndex = -1;
	      	$('#regclientid').html(data.client_id);
	      	$('#regclientsecret').html(data.client_secret);
	      	$('#regregtoken').html(data.register_token);
	        $("#myModal").modal({
			    backdrop: 'static',
			    keyboard: false
			});
	      },
	      error:function(e){
	        console.log(e);
	        bootbox.alert("Client registration failed. Contact the Admin.");
	      }
	    })
	};

	editclient = function(){
		var clientID = $('#editclientIds').val();
		var csecret = $('#editclientsecret').val();
		var edittoken = $('#editregtoken').val();
		var contactname = $('#editcontactname').val();
		if(contactname == ''){
        	bootbox.alert("Please Enter Contact Name");
        	return false;
        }
		var contactemail = $('#editcontactemail').val();
		if(contactemail == ''){
        	bootbox.alert("Please Enter Contact Name");
        	return false;
        }
		var orgName = $('#editorgName').val();
		if(orgName == ''){
        	bootbox.alert("Please Enter Contact Name");
        	return false;
        }
		var clientname = $('#editclientname').val();
		if(clientname == ''){
        	bootbox.alert("Please Enter Contact Name");
        	return false;
        }
		var redirecturi = $('#editredirecturi').val();
		if(redirecturi == ''){
        	bootbox.alert("Please Enter Contact Name");
        	return false;
        }
		var id = $('#editclientid').val();
		var scopes = [];
        $.each($("#editclientscope option:selected"), function(){            
            scopes.push($(this).val());
        });
        if(scopes.length <= 0){
        	bootbox.alert("Please Select Scopes");
        	return false;
        }
		var clientscope = scopes.join(",");
		var data = {"id":id,"userId":userId,"client_id":clientID,"client_secret":csecret,"register_token":edittoken,"contact_name" : contactname , "contact_mail" : contactemail , "org_name" : orgName , "name" : clientname , "redirect_uri" : redirecturi , "scope" : clientscope}
	    $.ajax({
	      url:main_url+"/client/",
	      type:"POST",
	      headers:{
	      	"Content-Type":"application/json"
	      },
	      data:JSON.stringify(data),
	      success:function(data){
	      	$('input[type="text"],input[type="password"]').val('');
	      	document.getElementById("editclientscope").selectedIndex = -1;
	      	/*$('#regclientid').html(data.client_id);
	      	$('#regclientsecret').html(data.client_secret);
	      	$('#regregtoken').html(data.register_token);*/
	      	$('#homecontent').show();
            $('#clientregform').hide();
            $('#editclientform').hide();
            $('footer').css('position', 'fixed');
	        /*$("#myModal").modal({
			    backdrop: 'static',
			    keyboard: false
			});*/
	    	bootbox.alert("Client Details Updated");
	      },
	      error:function(e){
	        console.log(e);
	      }
	    })
	}

	checkCookie = function() {
	    var user=getCookie("userdetails");
	    var split = user.split(',');
		var fullname = split[0];
		userId = split[1];
		if(user == ''){
			window.location.replace("userlogin.html");
		}else{
			$('#userButton').html("<i class='icon-user icon-white'></i>"+fullname+"<span class='caret'></span>");
			$('#dropname').text(fullname);
			getuserdetails(userId);
		}
	};

	getCookie = function(cname) {
	    var name = cname + "=";
	    var ca = document.cookie.split(';');
	    for(var i=0; i<ca.length; i++) {
	        var c = ca[i];
	        while (c.charAt(0)==' ') c = c.substring(1);
	        if (c.indexOf(name) != -1) {
	            return c.substring(name.length, c.length);
	        }
	    }
	    return "";
	}

	deleteCookie = function(name) {
	    document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	};

	getClientDetails = function(){
		var clientId = $('#clientId').val();
		var regtoken =  $('#regtoken').val();
		if(clientId != '' && regtoken != ''){
			$.ajax({
				url:main_url+"/client/details?clientId="+encodeURIComponent(clientId)+"&regtoken="+encodeURIComponent(regtoken),
				type:'GET',
				headers:{
                    "Content-Type":"application/json",
                    "Accept":"application/json"
                },
                success: function(data) {
                    $('#editcontactname').val(data.contact_name);
                    $('#editcontactemail').val(data.contact_mail);
                    $('#editorgName').val(data.org_name);
                    $('#editclientname').val(data.name);
                    $('#editredirecturi').val(data.redirect_uri);
                    /*var scopes = data.scope.split(",");
                    console.log(scopes);
                    for(var i=0;i<scopes.length;i++){
                    	$('#editclientscope').val(scopes[i]);
                    }*/
                    $('#editclientid').val(data.id);
                    $('#editclientIds').val(data.client_id);
                    $('#editclientsecret').val(data.client_secret);
                    $('#editregtoken').val(data.register_token);
                    $('#homecontent').hide();
                    $('#clientregform').hide();
                    $('#editclientform').show();
                    $('footer').css('position', 'relative');
                },
                error: function(e){

                } 
			})
		}
	}

	logoutuser = function(){
		deletCookies("userdetails");
		window.location.replace("userlogin.html");
	}

	getuserdetails = function(userId){
		$.ajax({
			url:main_url+"/user/"+userId,
			type:"GET",
			headers:{
                "Content-Type":"application/json",
                "Accept":"application/json"
            },
            success: function(data) {
                $('#userfullname').html(data.user_full_name);
                $('#username').html(data.user_name);
                $('#useremail').html(data.user_email);
            },
            error: function(e){

            } 
		})
	}
}).call(this);