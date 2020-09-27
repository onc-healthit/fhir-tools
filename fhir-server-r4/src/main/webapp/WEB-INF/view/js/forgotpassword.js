(function () {
	 var main_url = geturl()+"/r4/";
	// var main_url = geturl();
	 var emailregex = /^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
	$(document).ready(function () {
        $('#resetpassword').formValidation({
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
                email: {
                    validators: {
                        emailAddress: {
                            message: 'The value is not a valid email address'
                        },
                        notEmpty: {
                            message: 'Email Address is required'
                        }
                    }
                }   
            },
        }).on('success.form.fv', function (e) {
            // Prevent form submission
            e.preventDefault();

            var email = $('#resetpassword_emailaddress').val();
            var urlReq = main_url + "/user/reset/password?email="+email;											 //api
            console.log(urlReq);
            $.ajax({
                url: urlReq,
                type: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                //data: JSON.stringify(data),
                success: function (data) {
                    if (data) {
                    	var msg = "Temporary password is sent to your email address.";
						bootbox.alert(msg, function () {
                            window.location.replace('userlogin.html');
                        });
                    } else{
                        bootbox.alert("Email Address doesn't exist");
						return false;
                    }
                },
                error: function (e) {
                    console.log(e);
                    bootbox.alert("Email Address not found. Please contact Admin.");
                }
            });
        });
	});
	}).call(this);