(function(){
    //var main_url = "http://localhost:8000/hapi";
	//var main_url = "https://fhirtest.direct.sitenv.org/hapi";
	//var main_url = "https://fhirtest.sitenv.org/hapi-resprint";
	var main_url = geturl();
	var emailregex = /^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
	var regex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#`~_%^&+=.,-?';:{}!()])(?=\S+$).{8,}$/;
	registeruser = function(){
		var username = $('#username').val();
		var email = $('#emailaddress').val();
		var fname = $('#fullname').val();
		var password = $('#password').val();
		var cpassword = $('#cpassword').val();
		if(password != cpassword){
			bootbox.alert("Passwords do not match");
            return false;
		}
		if(!emailregex.test(email)){
			bootbox.alert("Please Enter Valid Email");
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
            if(data){
                $('input[type="text"],input[type="password"]').val('');
                bootbox.alert("User Registerd Successfully. Please Login to register Clients.");
            }
          },
          error:function(e){
            console.log(e);
            bootbox.alert("User Registration failed. No response from server.");
          }
        })
	}

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

                }                
            });
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
        icon: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
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
    });
});
}).call(this);