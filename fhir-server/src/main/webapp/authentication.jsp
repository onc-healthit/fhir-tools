<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import = "java.util.Map" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>FHIR Authentication</title>
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.min.css">
</head>
<body>
<div class="jumbotron" style="margin-top:100px;">
    <div class="container">
        <p>Hi user!</p>
        <p><b>Admin</b> is requesting <b>full access</b> to your account.</p>
        <p>Do you approve?</p>
        
        <div> Requesting access with the following scopes:
            
            
              <ul>
                <li><%= request.getParameter("scope") %></li>
              </ul>
            
            
        </div>
        

        <!-- <form action="https://fhirtest.sitenv.org/hapi/authorize" method="post">-->
        <!-- <form action="https://fhirprod.sitenv.org/hapi/authorize" method="post"> -->
        <form action="<%=request.getScheme() + "://" +
	                request.getServerName() + 
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )+
	                request.getContextPath() %>/authorize" method="post">
        <!-- <form action="https://fhirtest.direct.sitenv.org/hapi/authorize" method="post"> -->
            <input name="transaction_id" type="hidden" value=<%= request.getParameter("transaction_id") %>>
            <input name="scope" type="hidden" value="*">
            <div>
                <input class="btn btn-primary btn-lg" type="submit" value="Allow" name="Allow" id="allow">
                <input class="btn btn-primary btn-lg" type="submit" value="Deny" name="cancel" id="deny">
            </div>
        </form>
    </div>

</div>
</body>
</html>