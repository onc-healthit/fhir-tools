(function () {
	 var main_url = geturl()+"/r4/";
	// var main_url = geturl();
	 var emailregex = /^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$/;
	 $(document).ready(function () {
	        $('#changepassword').formValidation({
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

	            	changeoldpassword: {
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

	                changenpassword: {
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

	                changecpassword: {
	                    validators: {
	                        regexp: {
	                            regexp: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#`~_%^&+=.,-?';:{}!()])(?=\S+$).{8,}$/,
	                            message: 'Please re-enter password following password rules.'
	                        },
	                        notEmpty: {
	                            message: 'Password is required'
	                        },
	                        identical: {
	                            field: 'changenpassword',
	                            message: 'Password entered and confirmed do not match.'
	                        }
	                    }
	                }
	            },
	        }).on('success.form.fv', function (e) {
	            // Prevent form submission
	            e.preventDefault();

	            var oldpassword = $('#changeoldpassword').val();
	            var newpassword = $('#changenpassword').val();
	            var username = getCookie("username");
	            var data = {"oldPassword": oldpassword, "password": btoa(newpassword), "userName":username};
	            $.ajax({
	                url: main_url + "/user/update/password",
	                type: "PUT",
	                headers: {
	                    "Content-Type": "application/json"
	                },
	                data: JSON.stringify(data),
	                success: function (data) {
	                	console.log(data);
	                	$('#oldpassworderror').html('');
	                    if (data == "Password updated successfully. Please Login with updated Password.") {
	                    	deleteCookie("username");
	                    	bootbox.alert(data, function () {
	                            window.location.replace('userlogin.html');
	                        });
	                    } else{
	                       // $('input[type="text"],input[type="password"]').val('');
	                    	$('#oldpassworderror').html(data);
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
	}).call(this);