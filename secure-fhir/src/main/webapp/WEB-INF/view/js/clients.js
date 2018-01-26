(function(){
	 var main_url = geturl();
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
			                	message: 'Redirect URI is required'
			                }
			            }
			        },
			            
			        'editscopes[]': {
			        	validators: {
			            	choice: {
			        	    	min: 1,
			                	max:14,
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
				var id = $('#editclientid').val();
		        var val = [];
		        $('.editregscopes :checkbox:checked').each(function(i){
		          val[i] = $(this).val();
		        });
		        console.log(val);
		        var clientscope = val.join(",");
				var data = {"id":id,"userId":userId,"client_id":clientID,"client_secret":csecret,"register_token":edittoken,"contact_name" : contactname , "contact_mail" : contactemail , "org_name" : orgName , "name" : clientname , "redirect_uri" : redirecturi , "scope" : clientscope}
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
			    props = ["name", "client_id", "client_secret","id"];
			    if(data.length>0){
			    	for(var i=0; i< data.length; i++){
			    		if(!data[i].isBackendClient){
							var tr = $('<tr>');
							for(var m=0; m<props.length; m++){
								if(props[m] == 'name'){
						  			$('<td style="width:20%">').html(data[i].name).appendTo(tr); 
						  		}else if(props[m] == 'client_id'){
						  			$('<td style="width:30%">').html(data[i].client_id).appendTo(tr); 
						  		} else if(props[m] == 'client_secret'){
						  			$('<td style="width:40%">').html(data[i].client_secret).appendTo(tr); 
						  		}else if(props[m] == 'id'){
						  			var str = 
						  			$("<td style='width:10%;text-align:center;'>").html("<a class='editanchor' onclick=\"getClientDetails('"+data[i].client_id+"','"+data[i].register_token+"')\">Edit</a>").appendTo(tr);
						  		}
							}
							tbody.append(tr);
			    		}
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
	    $('#viewregclients').show();
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

		$('#regbackendclient').click(function(){
			$('#backendclientregform').html('');
			$('#clientregform').hide('');
			$('#homecontent').hide();
			$('#backendclientregform').show();
			$('#editclientform').hide();
			$('#profilecontent').hide();
			$('#viewregclients').hide();
			$('footer').css('position', 'relative');
			$('#backendclientregform').load('backendclient.html',function(){
				$('#registerbackendclientdetails').formValidation({
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
		                clientappname:{
		                    validators:{
		                        regexp:{
		                            regexp: /^[a-z\s]+$/i,
		                            message: 'The Client name can consist of alphabetical characters and spaces only'
		                        },
		                        notEmpty: {
		                            message: 'Client App Name is required'
		                        }
		                    }
		                },

		                organization:{
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

		                issuerurl: {
		                    validators: {
		                        notEmpty: {
		                            message: 'Client Issuer URL is required'
		                        }
		                    }
		                },
		            
		               	'scopes[]': {
		                	validators: {
		                    	choice: {
		                        	min: 1,
		                        	max:14,
		                        	message: 'Please choose atleast one scope'
		                    	}
		                	}
		            	}
		            },
		        }).on('success.form.fv', function(e) {
		            // Prevent form submission
		            console.log("On success");
		            console.log(e);
		            e.preventDefault();

		            var formData = new FormData();
		            var clientappname = $('#clientappname').val();
					var organization = $('#organization').val();
					var issuerurl = $('#issuerurl').val();
					formData.append('file', $('input[type=file]')[0].files[0]); 
					var val = [];
			        $('.regbackendscopes :checkbox:checked').each(function(i){
			          val[i] = $(this).val();
			        });
			        var clientscope = val.join(",");

			        formData.append('userId',userId);
			        formData.append('name',clientappname);
					formData.append('org_name',organization);
					formData.append('issuer',issuerurl);
					formData.append('scope',clientscope);
					for (var key of formData.entries()) {
				        console.log(key[0] + ', ' + key[1]);
				    }
				    $.ajax({
				      url:main_url+"/client/backendclient/",
				      type:"POST",
				      data:formData,
				      processData: false,
        				contentType: false,
				      success:function(data){
				      	$('input[type="text"],input[type="file"]').val('');
				      	$('.regbackendscopes :checkbox:checked').each(function(i){
						    $(this).prop('checked', false);
						});
						console.log(data);
						$('#registerbackendcli').attr('disabled',false);
						$('#registerbackendcli').removeClass('disabled');
				      	$('#regbackendclientid').html(data.client_id);
				      	$('#backendclienttokenurl').html(data.token_url);
				        $("#backendRegModal").modal({
						    backdrop: 'static',
						    keyboard: false
						});
				      },
				      error:function(e){
				        console.log(e);
				        bootbox.alert("Error in registering Backend Client. Please contact Admininstrator.");
				      }
				    })
		        });
			})
		});

		$( "#shownewclient" ).click(function(){
			$('#clientregform').html('');
			$('#homecontent').hide();
			$('#clientregform').show();
			$('#editclientform').hide();
			$('#profilecontent').hide();
			$('#viewregclients').hide();
			$('#backendclientregform').hide();
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
		                            message: 'Redirect URI is required'
		                        }
		                    }
		                },
		            
		               	'scopes[]': {
		                	validators: {
		                    	choice: {
		                        	min: 1,
		                        	max:14,
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
					var val = [];
			        $('.regscopes :checkbox:checked').each(function(i){
			          val[i] = $(this).val();
			        });
			        console.log(val);
			        var clientscope = val.join(",");
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
			$('#viewregclients').hide();
			$('#backendclientregform').hide();
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
		removeerror = function(){
			var val = [];
			var backendval = [];
	        $('.regscopes :checkbox:checked').each(function(i){
	          	val[i] = $(this).val();
	        });
	        $('.regbackendscopes :checkbox:checked').each(function(j){
	          	backendval[j] = $(this).val();
	        });
	        if(val.length <= 0){
	        	$('#scopeserror').html('Please select atleast one scope');
	        }else{
	        	$('#scopeserror').html('');
	        }

	        if(backendval.length <= 0){
	        	$('#backendscopeserror').html('Please select atleast one scope');
	        }else{
	        	$('#backendscopeserror').html('');
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