<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="en">
  	<head>
    	<meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    	<meta name="description" content="">
    	<meta name="author" content="">

		<link href="view/css/bootstrap.css" rel="stylesheet" type="text/css" />
		<link href="view/css/fhir.css" rel="stylesheet" type="text/css" />
		<link href="view/css/ladda.min.css" rel="stylesheet" type="text/css" />
	
    	<title>FHIR Client</title>
	</head>
	<body>
		<!-- <nav class="navbar navbar-default navbar-fixed-top" style="height:70px;">
	      	<div class="container" style="color:#777777;font-size:22px;padding-top:20px;">
	        	SITE FHIR Server
	      	</div>
	    </nav> -->
	    <header> 
			<div class="jumbotron"> 
				<div class="container"></div> 
			</div> 
		</header>
		<div class="container" style="margin-top:30px;">
			<h2 class="form-signin-heading" style="text-align:center">Login Using Username and Password</h2>
		      <!-- <form class="form-signin" action="http://localhost:8089/hapi-resprint/authorize/userValidate" method="POST"> -->
		      <form class="form-signin" action="<%=request.getScheme() + "://" +
	                request.getServerName() + 
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )+
	                request.getContextPath() %>/authorize/userValidate" method="POST">
		      <input name="transaction_id" type="hidden" value=<%= request.getParameter("transaction_id") %>>	
		      <%-- <input name="transaction_id" type="hidden" value=<%= request.getScheme() %>>	 --%>	        
		        <label class="sr-only" for="inputEmail">Username</label>
		        <input type="text" placeholder="Username" class="form-control" id="userName" name="userName">
		        <label class="sr-only" for="inputPassword">Password</label>
		        <input type="password" required="" placeholder="Password" class="form-control" id="password" name="password">
		        <!-- <div class="checkbox">
		          <label>
		            <input type="checkbox" value="remember-me"> Remember me
		          </label>
		        </div> -->
		        <button type="submit" class="btn btn-lg btn-primary btn-block">Login</button>
		      </form>
		      <h5 class="form-signin-heading" style="text-align:center"><a href="newuser.html"> Click here </a> to register a new account</h5>

	    </div>
		<footer style="background-color: #eee;position:fixed;bottom: 0;width: 100%;"> <div role="contentinfo" class="container"> <div class="row"> <div> <p>This project was funded by a contract from the <a tabindex="100" href="http://www.healthit.gov">Office of the National Coordinator for Health Information Technology (ONC)</a></p> <p><a tabindex="100" href="http://www.hhs.gov/Privacy.html">Privacy Policy</a> | <a tabindex="100" href="http://www.hhs.gov/Disclaimer.html">Disclaimer</a></p> </div> </div> </div> </footer>

		<script src="view/js/jquery/jquery-1.11.1.js"></script>
		<script src="view/js/bootstrap.js"></script>
		<script src="view/js/bootbox.js"></script>
		<script src="view/js/FHIRresources.js"></script>
		<script src="view/js/searchparameters.js"></script>
		<script src="view/js/users.js"></script>
		<script src="view/js/formValidation.js"></script>
		<script type="text/javascript" src="view/js/framework/bootstrap.js"></script>
		<script src="view/js/spin.js"></script>
		<script src="view/js/ladda.js"></script>
		<script src="view/js/ladda.jquery.js"></script>
		<script src="view/js/clients.js"></script>
	</body>
</html>
