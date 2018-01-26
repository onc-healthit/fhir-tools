(function(){
    //var main_url = "http://localhost:8000/hapi";
	//var main_url = "https://fhirtest.direct.sitenv.org/hapi";
	//var main_url = "https://fhirtest.sitenv.org/hapi-resprint";
	 var main_url = geturl();
	var emailregex = /^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
	var regex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#`~_%^&+=.,-?';:{}!()])(?=\S+$).{8,}$/;
    var usernameregex = /^[A-Za-z0-9_]+$/i;
	/*registeruser = function(){
		var username = $('#username').val();
		var email = $('#emailaddress').val();
		var fname = $('#fullname').val();
		var password = $('#password').val();
		var cpassword = $('#cpassword').val();
		if(username == '' || username == undefined){
            bootbox.alert("Please enter Username");
            return false;
        }
        if(!usernameregex.test(username)){
            bootbox.alert("Please Enter Valid Username");
            return false;
        }
        if(!emailregex.test(email)){
            bootbox.alert("Please Enter Valid Email");
            return false;
        }
        if(fname == '' || fname == undefined){
            bootbox.alert("Please enter Full name");
            return false;
        }
        if(password == '' || password == undefined){
            bootbox.alert("Please enter Password");
            return false;
        }
        if(password != cpassword){
            bootbox.alert("Passwords do not match");
            return false;
        }
        var data = {"user_name":username,"user_email":email,"user_full_name":fname,"user_password":password};
        $.ajax({
          url:main_url+"/user/",
          type:"POST",
          headers:{
            "Content-Type":"application/json"
          },
          data:JSON.stringify(data),
          success:function(data){
            if(data == "Username already exists. Please use a different Username."){
                bootbox.alert(data);
                return false;
            }else{
                $('input[type="text"],input[type="password"]').val('');
                
                bootbox.alert(data,function(){
                    window.location.replace('userlogin.html');
                });
            }
          },
          error:function(e){
            console.log(e);
            bootbox.alert("User Registration failed. Please contact Admin.");
          }
        })
	}*/

    userlogin = function(){
        var uname = $('#regname').val();
        var pass = $('#userpass').val();
        if(uname != '' && pass != ''){
            $.ajax({
                url:main_url+"/user/details?userName="+encodeURIComponent(uname)+"&password="+encodeURIComponent(pass),
                type:"GET",
                headers:{
                    "Content-Type":"application/json",
                    "Accept":"application/json"
                },
                success: function(data) {
                    if(data != ""){
                        deleteCookie("userdetails");
                        var cname = "userdetails";
                        var cvalue = data.user_full_name+","+data.user_id;
                        setCookie("userdetails", cvalue, 1);
                        window.location.replace("clients.html");
                    }
                },
                error: function(e){
                	 console.log(e);
                     //bootbox.alert("User Login failed. Invalid UserName or Password.");
                     $('#loginerror').html('Invalid Username or Password');

                }                
            });
        }else{
            bootbox.alert("Please enter Username and Password");
        }
    }

    setCookie = function(cname,cvalue,exdays) {
        var d = new Date();
        //d.setTime(d.getTime() + (exdays*24*60*60*1000));
        d.setTime(d.getTime() + (15*60*1000));
        var expires = "expires=" + d.toGMTString();
        document.cookie = cname+"="+cvalue+"; "+expires;
    }

    $(document).ready(function() {
        $('#userregistration').formValidation({
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
                username:{
                    validators:{
                        regexp:{
                            regexp: /^[A-Za-z0-9_]+$/i,
                            message: 'The Username can consist of alphanumeric characters only.'
                        },
                        notEmpty: {
                            message: 'Username is required'
                        }
                    }
                },

                fullname:{
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

                email: {
                    validators: {
                        emailAddress: {
                            message: 'The value is not a valid email address'
                        },
                        notEmpty: {
                            message: 'Email is required'
                        }
                    }
                },
            
                npassword: {
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
           
                cpassword: {
                    validators: {
                        regexp: {
                            regexp: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#`~_%^&+=.,-?';:{}!()])(?=\S+$).{8,}$/,
                            message: 'Please re-enter password following password rules.'
                        },
                        notEmpty: {
                            message: 'Password is required'
                        },
                        identical: {
                         field: 'npassword',
                         message: 'Password entered and confirmed do not match.'
                     }
                    }
                }
            },
        }).on('success.form.fv', function(e) {
            // Prevent form submission
            e.preventDefault();

            var username = $('#username').val();
            var email = $('#emailaddress').val();
            var fname = $('#fullname').val();
            var password = $('#password').val();
            var cpassword = $('#cpassword').val();
            var data = {"user_name":username,"user_email":email,"user_full_name":fname,"user_password":password};
            $.ajax({
              url:main_url+"/user/",
              type:"POST",
              headers:{
                "Content-Type":"application/json"
              },
              data:JSON.stringify(data),
              success:function(data){
                if(data == "Username already exists. Please use a different Username."){
                    bootbox.alert(data);
                    return false;
                }else{
                    $('input[type="text"],input[type="password"]').val('');
                    
                    bootbox.alert(data,function(){
                        window.location.replace('userlogin.html');
                    });
                }
              },
              error:function(e){
                console.log(e);
                bootbox.alert("User Registration failed. Please contact Admin.");
              }
            });
        });

        $('#loginform').formValidation({
            framework: 'bootstrap',
            icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                username:{
                    validators:{
                        regexp:{
                            regexp: /^[A-Za-z0-9_]+$/i,
                            message: 'The Username can consist of alphanumeric characters only.'
                        },
                        notEmpty: {
                            message: 'Username is required'
                        }
                    }
                },
            
                password: {
                    validators: {
                        regexp: {
                            regexp: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#`~_%^&+=.,-?';:{}!()])(?=\S+$).{8,}$/,
                            message: 'Please enter password following password rules.'
                        },
                        notEmpty: {
                            message: 'Password is required'
                        }
                    }
                }
            },
        });
    });
}).call(this);