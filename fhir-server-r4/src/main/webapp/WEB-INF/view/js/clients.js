(function(){
	var main_url = geturl()+"/r4/";
	//var main_url = geturl();
	
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
			/*getuserdetails(userId);*/
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
	
	 setCookie = function(cname,cvalue,exdays) {
	        var d = new Date();
	        //d.setTime(d.getTime() + (exdays*24*60*60*1000));
	        d.setTime(d.getTime() + (15*60*1000));
	        var expires = "expires=" + d.toGMTString();
	        document.cookie = cname+"="+cvalue+"; "+expires;
	    }

	deleteCookie = function(name) {
	    document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	};

	launchEHR = function(launchId, launchURI){
		var getUrl = window.location;
		var iss= getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1]+"/r4/fhir";
		var ehrURL = launchURI+"?launch="+launchId+"&iss="+iss;		
		window.open (ehrURL,"_blank"); //Open EHR window 
		window.focus();
	};
	
	deleteClientDetails = function(clientId, clientName, clientSecret, userId) {
		$('#deleteclientmodal').load('deleteclientmodal.html',function(){
						
			if(clientId != '' && clientSecret != '' && clientName != ''){
				$('#deleteclientname').html(clientName);
			}
			$('#deleteclientmodal').modal({backdrop: 'static', keyboard: false});
			$('#deleteclientformhtml').on('submit.form',function(e){
	        	e.preventDefault();
				var data = {"clientId":clientId, "clientSecret":clientSecret}
			    $.ajax({
			      	url:main_url+"/client/delete/",
			      	type:"DELETE",
			      	headers:{
			      		"Content-Type":"application/json"
			      	},
			      	data:JSON.stringify(data),
			      	success:function(data){
			      		$('#deleteclientmodal').modal('hide');
			      		
			      		if(data == true){
			      			bootbox.alert("Client deleted successfully",function(){
				    			loadclients(userId);
				    		});	
			      		}else{
			      			bootbox.alert("Client not found.",function(){
				    			loadclients(userId);
				    		});	
	                    }
			    		
			      	},
			      	error:function(e){
			      		
			      		$('#deleteclientmodal').modal('hide');
			      		bootbox.alert(e.responseText,function(){
			    			loadclients(userId);
			    		});	
			        	console.log(e);
			      	}
			    })
	        });
		});
	};
	
	getClientDetails = function(clientId,regtoken){
		$('#editclientmodal').load('editclientmodal.html',function(){
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
	                    $('#editlaunchUri').val(data.launchUri);
	                    $('#editclientid').val(data.id);
	                    $('#editclientIds').val(data.client_id);
	                    $('#editclientsecret').val(data.client_secret);
	                    $('#editregtoken').val(data.register_token);
	                    var scopearr = data.scope.split(',');
				        for(var i=0; i< scopearr.length; i++){
				        	$('input:checkbox.editregscopesclass').each(function () {
						      	if($(this).val() == scopearr[i]){
						      		$(this).prop('checked',true);
						      	}
						  	});
				        }
	                    $('#editclientmodal').modal({backdrop: 'static', keyboard: false});
	                },
	                error: function(e){
	                	
	                	console.log(e);
	                	bootbox.alert("Invalid Client Id or Registration Token.");

	                } 
				})
			}

			$('#editclientformhtml').formValidation('destroy').formValidation({
	        	framework: 'bootstrap',
			    icon: {
			        valid: 'glyphicon glyphicon-ok',
			        invalid: 'glyphicon glyphicon-remove',
			        validating: 'glyphicon glyphicon-refresh'
			    },
			    fields: {
			        editcontactname:{
			            validators:{
			                regexp:{
			                    regexp: /^[a-z\s]+$/i,
			                    message: 'The Contact name can consist of alphabetical characters and spaces only'
			                },
			                notEmpty: {
			                    message: 'Contact name is required'
			                }
			            }
			        },

			        editorgName:{
			            validators:{
			                regexp:{
			                    regexp: /^[a-z\s]+$/i,
			                    message: 'The Organization name can consist of alphabetical characters and spaces only'
			                },
			            	notEmpty: {
			                	message: 'Organization name is required'
			            	}
			            }
			        },

			        editcontactemail: {
			            validators: {
			        	    emailAddress: {
			             	    message: 'The value is not a valid email address'
			                },
			                notEmpty: {
			                    message: 'Email is required'
			                }
			            }
			        },

			        editclientname:{
			            validators:{
			                regexp:{
			        	        regexp: /^[a-z\s]+$/i,
			            	        message: 'The Client name can consist of alphabetical characters and spaces only'
			                },
			                notEmpty: {
			            	    message: 'Client name is required'
			                }
			        	}
			        },

			        editredirecturi:{
			        	validators:{
			            	notEmpty: {
			                	message: 'Redirect URL is required'
			                }
			            }
			        },
			        
			        editlaunchUri: {
                        validators: {
                            notEmpty: {
                                message: 'Launch URL is required'
                            }
                        }
                    },
			        'editscopes[]': {
			        	validators: {
			            	choice: {
			        	    	min: 1,
			                	max:53,
			                	message: 'Please choose atleast one scope'
			                }
			            }
			        }
			    }
	        }).on('success.form.fv',function(e){
	        	e.preventDefault();

	        	var clientID = $('#editclientIds').val();
				var csecret = $('#editclientsecret').val();
				var edittoken = $('#editregtoken').val();
				var contactname = $('#editcontactname').val();
				var contactemail = $('#editcontactemail').val();
				var orgName = $('#editorgName').val();
				var clientname = $('#editclientname').val();
				var redirecturi = $('#editredirecturi').val().trim();
				var launchUri = $('#editlaunchUri').val().trim();
				var id = $('#editclientid').val();
		        var val = [];
		        $('.editregscopes :checkbox:checked').each(function(i){
		          val[i] = $(this).val();
		        });
		        console.log(val);
		        var clientscope = val.join(",");
				var data = {"id":id,"userId":userId,"client_id":clientID,"client_secret":csecret,"register_token":edittoken,"contact_name" : contactname , "contact_mail" : contactemail , "org_name" : orgName , "name" : clientname , "redirect_uri" : redirecturi ,"launchUri": launchUri, "scope" : clientscope}
			    $.ajax({
			      	url:main_url+"/client/",
			      	type:"PUT",
			      	headers:{
			      		"Content-Type":"application/json"
			      	},
			      	data:JSON.stringify(data),
			      	success:function(data){
			    		bootbox.alert("Client Details Updated",function(){
			    			loadclients(userId);
			    			$('#editclientmodal').modal('hide');
			    			$('input[type="text"],input[type="password"]').val('');
			    			$('.editregscopes :checkbox:checked').each(function(i){
					          $(this).prop('checked', false);
					        });
			    		});
			      	},
			      	error:function(e){
			        	console.log(e);
			      	}
			    })
	        });

	        if($('.editregscopes i')){
			    $('.editregscopes i').remove();
			}
		})
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
                $('#userfullname').val(data.user_full_name);
                $('#username').val(data.user_name);
                $('#useremail').val(data.user_email);
                $('#userpassword').val(data.user_password);
                $('#clientregform').hide();
				$('#homecontent').hide();
				$('#editclientform').hide();
				$('#profilecontent').show();
				$('#viewregclients').hide();
				$('footer').css('position', 'fixed');
            },
            error: function(e){

            } 
		})
	}
	
	updateuserprofile = function(){
		var fullname = $('#userfullname').val();
		var username = $('#username').val();
		var email = $('#useremail').val();
		var password = $('#userpassword').val();
		var data = {"user_id":userId,"user_name":username,"user_full_name":fullname,"user_email":email,"user_password" : password};
		 $.ajax({
		      	url:main_url+"/user/",
		      	type:"PUT",
		      	headers:{
		      		"Content-Type":"application/json"
		      	},
		      	data:JSON.stringify(data),
		      	success:function(data){
		    		bootbox.alert("User Details Updated",function(){
		    			 $('#userfullname').val(fullname);
		                 $('#useremail').val(email);
		                 $('#userButton').html("<i class='icon-user icon-white'></i>"+fullname+"<span class='caret'></span>");
		                 var cvalue = fullname+","+userId;
	                     setCookie("userdetails", cvalue, 1);
		    		});
		      	},
		      	error:function(e){
		        	console.log(e);
		      	}
		    })
	}

	loadclients = function(userId){
		$.ajax({
			url:main_url+"/client/list/"+userId,
			type:"GET",
			headers:{
                "Content-Type":"application/json",
                "Accept":"application/json"
            },
            success: function(data) {
                var tbody = $('#clientsregtable tbody');
                $('#clientsregtable tbody').html('');
			    props = ["name", "client_id", "client_secret","id","launchid","launchUri"];
			    if(data.length>0){
			    	for(var i=0; i< data.length; i++){
						var tr = $('<tr>');
						for(var m=0; m<props.length; m++){
							if(props[m] == 'name'){
					  			$('<td style="width:20%">').html(data[i].name).appendTo(tr); 
					  		}else if(props[m] == 'client_id'){
					  			$('<td style="width:25%">').html(data[i].client_id).appendTo(tr); 
					  		} else if(props[m] == 'client_secret'){
					  			$('<td style="width:30%">').html(data[i].client_secret).appendTo(tr); 
					  		}else if(props[m] == 'id'){
					  			var str = 
					  			$("<td style='width:10%;text-align:center;'>").html("<a class='editanchor' onclick=\"getClientDetails('"+data[i].client_id+"','"+data[i].register_token+"')\">Edit</a> | <a class='editanchor' onclick=\"deleteClientDetails('"+data[i].client_id+"','"+data[i].name+"','"+data[i].client_secret+"', '"+userId+"')\">Delete</a>").appendTo(tr);
					  		}else if(props[m] == 'launchUri'){
					  			var str = $("<td style='width:15%;text-align:center;'>").html("<button class=\"btn but-large\" type=\"button\" onclick=\"launchEHR('"+data[i].launchId+"','"+data[i].launchUri+"')\";>Launch App</button>").appendTo(tr);
						  	}
						}
						tbody.append(tr);
					}
			    }else{
			    	console.log("in else");
			    	var tr = $('<tr><td colspan="4"><h4 style="text-align:center;">No registered clients available.</h4></td></tr>');
			    	tbody.append(tr);
			    }
				if ($("body").height() > $(window).height()) {
			        $('footer').css('position', 'relative');
			    }
            },
            error: function(e){

            } 
		})
	    $('#homecontent').hide();
		$('#resetcontent').hide();
	    $('#viewregclients').show();
	    $('footer').css('position', 'fixed');
	}

	$(document).ready(function(){

		$(document).on("click","#registercli",function(){
	        var val = [];
	        $('.regscopes :checkbox:checked').each(function(i){
	          	val[i] = $(this).val();
	        });
	        if(val.length <= 0){
	        	$('#scopeserror').html('Please select atleast one scope');
	        }else{
	        	$('#scopeserror').html('');
	        }
		})

		$( "#shownewclient" ).click(function(){
			$('#clientregform').html('');
			$('#homecontent').hide();
			$('#clientregform').show();
			$('#editclientform').hide();
			$('#profilecontent').hide();
			$('#viewregclients').hide();
			$('footer').css('position', 'relative');
			$('.regscopes :checkbox:checked').each(function(i){
				$(this).prop('checked', false);
			});
			$('#clientregform').load('clientregform.html',function(){
				$('#registerclientdetails').formValidation({
		            framework: 'bootstrap',
		            /*err: {
		                container: 'popover'
		            },*/
		            icon: {
		                valid: 'glyphicon glyphicon-ok',
		                invalid: 'glyphicon glyphicon-remove',
		                validating: 'glyphicon glyphicon-refresh'
		            },
		            fields: {
		                contactname:{
		                    validators:{
		                        regexp:{
		                            regexp: /^[a-z\s]+$/i,
		                            message: 'The Contact name can consist of alphabetical characters and spaces only'
		                        },
		                        notEmpty: {
		                            message: 'Contact name is required'
		                        }
		                    }
		                },

		                orgName:{
		                    validators:{
		                        regexp:{
		                            regexp: /^[a-z\s]+$/i,
		                            message: 'The Organization name can consist of alphabetical characters and spaces only'
		                        },
		                        notEmpty: {
		                            message: 'Organization name is required'
		                        }
		                    }
		                },

		                contactemail: {
		                    validators: {
		                        emailAddress: {
		                            message: 'The value is not a valid email address'
		                        },
		                        notEmpty: {
		                            message: 'Email is required'
		                        }
		                    }
		                },

		                clientname:{
		                    validators:{
		                        regexp:{
		                            regexp: /^[a-z\s]+$/i,
		                            message: 'The Client name can consist of alphabetical characters and spaces only'
		                        },
		                        notEmpty: {
		                            message: 'Client name is required'
		                        }
		                    }
		                },

		                redirecturi:{
		                    validators:{
		                        notEmpty: {
		                            message: 'Redirect URL is required'
		                        }
		                    }
		                },
                        launchUri: {
                            validators: {
                                notEmpty: {
                                    message: 'Launch URL is required'
                                }
                            }
                        },
		                
		               	'scopes[]': {
		                	validators: {
		                    	choice: {
		                        	min: 1,
		                        	max:53,
		                        	message: 'Please choose atleast one scope'
		                    	}
		                	}
		            	}
		            },
		        }).on('success.form.fv', function(e) {
		            // Prevent form submission
		            console.log(e);
		            e.preventDefault();

		            var contactname = $('#contactname').val();
					var contactemail = $('#contactemail').val();
					var orgName = $('#orgName').val();
					var clientname = $('#clientname').val();
					var redirecturi = $('#redirecturi').val().trim();
					var launchUri = $('#launchUri').val().trim();

					var val = [];
			        $('.regscopes :checkbox:checked').each(function(i){
			          val[i] = $(this).val();
			        });
			        console.log(val);
			        var clientscope = val.join(",");
					var data = {"userId":userId,"contact_name" : contactname , "contact_mail" : contactemail , "org_name" : orgName , "name" : clientname , "redirect_uri" : redirecturi ,"launchUri": launchUri, "scope" : clientscope}
				    $.ajax({
				      url:main_url+"/client/",
				      type:"POST",
				      headers:{
				      	"Content-Type":"application/json"
				      },
				      data:JSON.stringify(data),
				      success:function(data){
				      	$('input[type="text"],input[type="password"]').val('');
				      	$('.regscopes :checkbox:checked').each(function(i){
						    $(this).prop('checked', false);
						});
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
				        bootbox.alert("Client Name already exists. Please use a different Client Name.");
				      }
				    })
		        });

		        if($('.regscopes i')){
		        	$('.regscopes i').remove();
		        }
			});
		});

		$(document).on('click','#profileinfo',function(){
			$('#clientregform').hide();
			$('#homecontent').hide();
			$('#editclientform').hide();
			$('#profilecontent').show();
			$('#resetcontent').hide();
			$('#viewregclients').hide();
			$('footer').css('position', 'fixed');
			$('#content').load('profileinfo.html',function(){
				getuserdetails(userId);
				$('#profileinformation').formValidation('destroy').formValidation({
		            framework: 'bootstrap',
		            icon: {
		                valid: 'glyphicon glyphicon-ok',
		                invalid: 'glyphicon glyphicon-remove',
		                validating: 'glyphicon glyphicon-refresh'
		            },
		            fields: {
		                userfullname:{
		                    validators:{
		                        regexp:{
		                            regexp: /^[a-z\s]+$/i,
		                            message: 'The Full name can consist of alphabetical characters and spaces only'
		                        },
		                        notEmpty: {
		                            message: 'Full name is required'
		                        }
		                    }
		                },

		                useremail: {
		                    validators: {
		                        emailAddress: {
		                            message: 'The value is not a valid email address'
		                        },
		                        notEmpty: {
		                            message: 'Email is required'
		                        }
		                    }
		                },
		            },
		        }).on('success.form.fv', function(e) {
		            // Prevent form submission
		            console.log(e);
		            e.preventDefault();

		            var fullname = $('#userfullname').val();
					var username = $('#username').val();
					var email = $('#useremail').val();
					var password = $('#userpassword').val();
					var data = {"user_id":userId,"user_name":username,"user_full_name":fullname,"user_email":email,"user_password" : password};
				 	$.ajax({
				      	url:main_url+"/user/",
				      	type:"PUT",
				      	headers:{
				      		"Content-Type":"application/json"
				      	},
				      	data:JSON.stringify(data),
				      	success:function(data){
				    		bootbox.alert("User Details Updated",function(){
				    			 $('#userfullname').val(fullname);
				                 $('#useremail').val(email);
				                 $('#dropname').html(fullname);
				                 $('#userButton').html("<i class='icon-user icon-white'></i>"+fullname+"<span class='caret'></span>");
				                 var cvalue = fullname+","+userId;
			                     setCookie("userdetails", cvalue, 1);
				    		});
				      	},
				      	error:function(e){
				        	console.log(e);
				      	}
				    })
		        });
			});
		});
		
		
		//Change password
        $(document).on('click', '#resetpass', function () {
            $('#clientregform').hide();
            $('#homecontent').hide();
            $('#editclientform').hide();
            $('#resetcontent').show();
            $('#profilecontent').hide();
            $('#viewregclients').hide();
            $('footer').css('position', 'fixed');
            $('#resetpasscontent').load('resetpass.html', function () {
                //getuserdetails(userId);
               // $('#profilecontent').hide();
                $('#resetpassword').formValidation('destroy').formValidation({
                    framework: 'bootstrap',
                    icon: {
                        valid: 'glyphicon glyphicon-ok',
                        invalid: 'glyphicon glyphicon-remove',
                        validating: 'glyphicon glyphicon-refresh'
                    },
                    fields: {

                    	resetoldpassword: {
    	                    validators: {
    	                    	/*regexp: {
    	                            regexp: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#`~_%^&+=.,-?';:{}!()])(?=\S+$).{8,}$/,
    	                            message: 'Please re-enter password following password rules.'
    	                        },*/
    	                        notEmpty: {
    	                            message: 'Old password is required'
    	                        }
    	                    }
    	                },

    	                resetnpassword: {
    	                    validators: {
    	                        regexp: {
    	                            regexp: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#`~_%^&+=.,-?';:{}!()])(?=\S+$).{8,}$/,
    	                            message: 'Please re-enter password following password rules.'
    	                        },
    	                        notEmpty: {
    	                            message: 'Password is required'
    	                        }
    	                    }
    	                },

    	                resetcpassword: {
    	                    validators: {
    	                        regexp: {
    	                            regexp: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#`~_%^&+=.,-?';:{}!()])(?=\S+$).{8,}$/,
    	                            message: 'Please re-enter password following password rules.'
    	                        },
    	                        notEmpty: {
    	                            message: 'Password is required'
    	                        },
    	                        identical: {
    	                            field: 'resetnpassword',
    	                            message: 'Password entered and confirmed do not match.'
    	                        }
    	                    }
    	                }
    	            },
                }).on('success.form.fv', function (e) {
    	            // Prevent form submission
    	            e.preventDefault();

    	            var oldpassword = $('#resetoldpassword').val();
    	            var newpassword = $('#resetnpassword').val();
    	            var username = getCookie("username");
    	            var data = {"oldPassword": btoa(oldpassword), "password": btoa(newpassword), "userName":username};
    	            $.ajax({
    	                url: main_url + "/user/change/password",
    	                type: "PUT",
    	                headers: {
    	                    "Content-Type": "application/json"
    	                },
    	                data: JSON.stringify(data),
    	                success: function (data) {
    	                	console.log(data);
    	                	$('#resetoldpassworderror').html('');
    	                    if (data == "Password changed successfully.") {
    	                    	//deleteCookie("username");
    	                    	bootbox.alert(data,function(){
    	                    		$('#resetcontent').hide();
        	                        $('#homecontent').show();
    	                    	});
    	                    } else{
    	                       // $('input[type="text"],input[type="password"]').val('');
    	                    	$('#resetoldpassworderror').html(data);
    	                        return false;
    	                    }
    	                },
    	                error: function (e) {
    	                    console.log(e);
    	                    bootbox.alert("User password update failed. Please contact Admin.");
    	                }
    	            });
    	        });
    	 });
    	});

		
		removeerror = function(){
			var val = [];
	        $('.regscopes :checkbox:checked').each(function(i){
	          	val[i] = $(this).val();
	        });
	        if(val.length <= 0){
	        	$('#scopeserror').html('Please select atleast one scope');
	        }else{
	        	$('#scopeserror').html('');
	        }

	        var editval = [];
	        $('.editregscopes :checkbox:checked').each(function(m){
	          	editval[m] = $(this).val();
	        });

	        if(editval.length <= 0){
	        	$('#editscopeserror').html('Please select atleast one scope');
	        }else{
	        	$('#editscopeserror').html('');
	        }
		}
        
	});
}).call(this);