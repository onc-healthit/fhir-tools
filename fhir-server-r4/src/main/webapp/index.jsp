<html>
<head>
    <meta charset="utf-8">
    <title>FHIR Server</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-40163006-1"></script>
    <script>
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());

        gtag('config', 'UA-40163006-1');
    </script>

    <!-- End Google Analytics -->

</head>

<body>

<%-- header starts here --%>
<site-header ng-version="2.4.7" _nghost-vni-33="">
    <header _ngcontent-vni-33="">
        <div _ngcontent-vni-33="" class="site-header">
            <div _ngcontent-vni-33="" class="container">
                <a _ngcontent-vni-33="" class="site-logo" href="https://www.healthit.gov/" rel="external"
                   title="HealthIT.gov">
                    <img _ngcontent-vni-33="" alt="HealthIT.gov"
                         src="${pageContext.request.contextPath}/view/images/site/healthit.gov.logo.png">
                </a>
            </div>
        </div>
        <!--template bindings={}-->
    </header>
</site-header>
<app-site-navigation>
    <nav>
        <div class="navbar navbar-default">
            <div class="container">
                <div class="navbar-header">
                    <button class="navbar-toggle" data-target=".navbar-collapse"
                            data-toggle="collapse" tabindex="1" type="button"><span
                            class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span> <span
                                class="icon-bar"></span></button>
                </div>
                <nav class="collapse navbar-collapse" id="navigation">
                    <ul class="nav navbar-nav">
                        <li><a routerlink="/" routerlinkactive="active"
                               tabindex="1" title="SITE Home" href="https://site.healthit.gov" class="active">Home</a></li>
                        <li><a href="https://ett.healthit.gov/ett/#/home"
                               tabindex="1" target="_blank" title="ETT">ETT</a></li>
                        <li><a routerlink="test-tools" tabindex="1"
                               title="TestTools" href="https://site.healthit.gov/test-tools">TestTools</a></li>
                        <li><a href="https://www.healthit.gov/techlab/"
                               tabindex="1" target="_blank" title="TechLab">TechLab</a></li>
                    </ul>
                </nav>
                <breadcrumb prefix="SITE">
                    <ul class="breadcrumb">
                        <li class="breadcrumb-item">
                            <a role="button" href="https://site.healthit.gov/home">SITE</a>
                        </li>
                        <li class="breadcrumb-item active">
                            <span>FHIR Server</span>
                        </li>
                    </ul>
                </breadcrumb>
            </div>
        </div>
    </nav>
    <div class="spacer"></div>
    <router-outlet></router-outlet>
</app-site-navigation>

<%-- header Ends here --%>

<!-- Content starts here -->

<div class="container" style="margin-top:10px;">
    <div class="col-md-12" style="margin-top: -40px;">
        <h1 style="margin-left: 13px;  font-size: 34px;text-align: center;" >FHIR Server</h1>
    </div>
    <div class="col-md-12">
        <div class="col-md-12" align="center" style="font-size:16px;">
            <p style="    font-size: 14px;;
    margin-left: 41px;
    margin-top: -11px;
    text-align: center;">This is an implementation of the FHIR standard using the <a href="http://www.hl7.org/fhir/us/core/" target="_blank">US Core Implementation guide</a>. We expect client developers to use the server as part of their development activities to work with different data sets.   </p>

        </div>
    </div>

    <div class="col-lg-12" style="margin-bottom: -42px ">

        <!--////////////////////tabs////////////////////////////////-->
        <div id="exTab2" class="container">
            <div>
                <div class="tab" role="tabpanel">
                    <ul class="nav nav-tabs" role="tablist">
                        <li role="presentation"><a  href="#1"  role="tab" data-toggle="tab">DSTU2</a></li>
                        <li  role="presentation"><a href="#2" role="tab" data-toggle="tab">STU3</a></li>
                        <li  role="presentation" class="active"><a href="#3" role="tab" data-toggle="tab">R4</a></li>
                    </ul>
                    <hr style="margin-top: 1px;"></hr>
                    <div >
                        <div  class="panel-heading">
                            <h3 style="margin-top: -24px;font-size:20px">Instructions and Guidelines to use the FHIR Server:</h3>
                        </div>
                        <div style="    margin-top: -25px">
                            <div class="panel-body">
                                <div class="col-md-12">
                                    The FHIR Server can be used in two modes. An open mode (No OAuth token required) and a secure mode (OAuth tokens required).
                                </div>

                                <div class="row" >

                                </div>
                            </div>
                            <div class="tab-content ">
                                <div role="tabpanel" class="tab-pane fade" id="1">
                                    <div class="row" >
                                        <div class="col-md-6">
                                            <ol>
                                                <li>
                                                    The server URI for DSTU2 open mode is : <%=request.getScheme() + "://" +
                                                        request.getServerName() +
                                                        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                                                %>/open/fhir
                                                </li>
                                                <li>
                                                    The server URI for DSTU2 secure mode is : <%=request.getScheme() + "://" +
                                                        request.getServerName() +
                                                        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                                                %>/secure/fhir
                                                </li>
                                            </ol>
                                        </div>
                                        <div class="col-md-6 ">
                                            <button class="btn btn-primary center-block ladda-button btncolor" style="margin-top: -18px;margin-left: 300px; color:white" ><a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/view/newuser.html" style="color:white" target="_blank">Register</a></button>
                                            <button style="width: 74px; margin-left: 409px; " class="btn btn-primary center-block ladda-button btncolor"  ><a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/view/clients.html" style="color:white" target="_blank">Login</a></button>
                                        </div>
                                    </div>

                                    <div  style="min-height:300px;" >
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="panel panel-default" style="min-height:300px;">
                                                    <div style="background-image:linear-gradient(to bottom, #f5f5f5 0%, #e8e8e8 100%);background-repeat:repeat-x" class="panel-heading">
                                                        Using the Open FHIR Server For DSTU2:
                                                    </div>
                                                    <div class="panel-body">
                                                        A client can use the open server by making a FHIR request using a FHIR READ or SEARCH operation.
                                                        <ol style="margin-top:10px;">
                                                            <li>
                                                                For example a simple READ can be executed as follows:
                                                                <ul style="list-style:outside none disc;margin-bottom:8px;">
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
	                 %>/open/fhir/Patient/1" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/fhir/Patient/1</a> - XML format.</li>
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/fhir/Patient/1?_format=json" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/fhir/Patient/1?_format=json</a> - JSON format.</li>
                                                                </ul>
                                                            </li>
                                                            <li>
                                                                For example a simple SEARCH can be executed as follows:
                                                                <ul style="list-style:outside none disc;margin-bottom:8px;">
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/fhir/Patient/?_id=1" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/fhir/Patient/?_id=1</a> - XML format.</li>
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/fhir/Patient/?_id=1&_format=json" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/fhir/Patient/?_id=1&_format=json</a> -  JSON format.</li>
                                                                </ul>
                                                            </li>
                                                            <li style="margin-bottom:8px;">
                                                                Click here for more details on <a href="http://hl7.org/fhir/search.html" target="_blank">FHIR Search</a>.
                                                            </li>
                                                            <li>
                                                                For a list of Patients <a style="cursor:pointer;" onclick="loadPatients('dstu2');">Click here</a>.
                                                            </li>
                                                        </ol>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="panel panel-default" style="min-height:355px;">
                                                    <div style="background-image:linear-gradient(to bottom, #f5f5f5 0%, #e8e8e8 100%);background-repeat:repeat-x" class="panel-heading">
                                                        Using the Secure FHIR Server For DSTU2:
                                                    </div>
                                                    <div class="panel-body">
                                                        <ol>
                                                            <li>
                                                                The Secure FHIR server follows the implementation of Smart On FHIR specifications.
                                                            </li>
                                                            <li>
                                                                In order to use the FHIR Server with the appropriate tokens the following steps need to be followed
                                                                <ul style="list-style:outside none disc;">
                                                                    <li style="margin-bottom:8px;">
                                                                        Create a user account in the FHIR Authorization Server:
                                                                        Follow instructions  <a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/view/newuser.html" target="_blank">here</a> for user creation.
                                                                    </li>
                                                                    <li style="margin-bottom:8px;">
                                                                        Login and Register a client so that you can get the Client Id and  Client Secret.
                                                                        Follow instructions <a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/view/clients.html" target="_blank">here</a> for client registration.
                                                                    </li>
                                                                    <li style="margin-bottom:8px;">
                                                                        Once you have a Client Id and Client Secret follow the guidelines according to the Smart On FHIR to request resources.
                                                                    </li>
                                                                </ul>
                                                            </li>
                                                        </ol>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div  role="tabpanel" class="tab-pane fade" id="2">
                                    <div class="row" >

                                        <div class="col-md-6">
                                            <ol>
                                                <li>
                                                    The server URI for STU3 open mode is : <%=request.getScheme() + "://" +
                                                        request.getServerName() +
                                                        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                                                %>/open/stu3/fhir
                                                </li>
                                                <li>
                                                    The server URI for STU3 secure mode is : <%=request.getScheme() + "://" +
                                                        request.getServerName() +
                                                        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                                                %>/secure/stu3/fhir
                                                </li>
                                            </ol>
                                        </div>
                                        <div class="col-md-6">
                                            <button class="btn btn-primary center-block ladda-button" style="margin-top: -18px;margin-left: 300px;color:white"><a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/stu3/view/newuser.html" target="_blank" style="color:white">Register</a></button>
                                            <button style="width: 74px; margin-left: 409px;color:white" class="btn btn-primary center-block ladda-button"><a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/stu3/view/clients.html" target="_blank" style="color:white">Login</a></button>
                                        </div>
                                    </div>

                                    <div  style="min-height:300px;">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="panel panel-default" style="min-height:300px;">
                                                    <div style="background-image:linear-gradient(to bottom, #f5f5f5 0%, #e8e8e8 100%);background-repeat:repeat-x" class="panel-heading">
                                                        Using the Open FHIR Server For STU3:
                                                    </div>
                                                    <div class="panel-body">
                                                        A client can use the open server by making a FHIR request using a FHIR READ or SEARCH operation.
                                                        <ol style="margin-top:10px;">
                                                            <li>
                                                                For example a simple READ can be executed as follows:
                                                                <ul style="list-style:outside none disc;margin-bottom:8px;">
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
	                 %>/open/stu3/fhir/Patient/1" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/stu3/fhir/Patient/1</a> - XML format.</li>
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/stu3/fhir/Patient/1?_format=json" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/stu3/fhir/Patient/1?_format=json</a> - JSON format.</li>
                                                                </ul>
                                                            </li>
                                                            <li>
                                                                For example a simple SEARCH can be executed as follows:
                                                                <ul style="list-style:outside none disc;margin-bottom:8px;">
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/stu3/fhir/Patient/?_id=1" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/stu3/fhir/Patient/?_id=1</a> - XML format.</li>
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/stu3/fhir/Patient/?_id=1&_format=json" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/stu3/fhir/Patient/?_id=1_format=json</a> -  JSON format.</li>
                                                                </ul>
                                                            </li>
                                                            <li style="margin-bottom:8px;">
                                                                Click here for more details on <a href="http://hl7.org/fhir/search.html" target="_blank">FHIR Search</a>.
                                                            </li>
                                                            <li>
                                                                For a list of Patients <a style="cursor:pointer;" onclick="loadPatients('stu3');">Click here</a>.
                                                            </li>
                                                        </ol>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="panel panel-default" style="min-height:355px;">
                                                    <div style="background-image:linear-gradient(to bottom, #f5f5f5 0%, #e8e8e8 100%);background-repeat:repeat-x" class="panel-heading">
                                                        Using the Secure FHIR Server For STU3:
                                                    </div>
                                                    <div class="panel-body">
                                                        <ol>
                                                            <li>
                                                                The Secure FHIR server follows the implementation of Smart On FHIR specifications.
                                                            </li>
                                                            <li>
                                                                In order to use the FHIR Server with the appropriate tokens the following steps need to be followed
                                                                <ul style="list-style:outside none disc;">
                                                                    <li style="margin-bottom:8px;">
                                                                        Create a user account in the FHIR Authorization Server:
                                                                        Follow instructions  <a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/stu3/view/newuser.html" target="_blank">here</a> for user creation.
                                                                    </li>
                                                                    <li style="margin-bottom:8px;">
                                                                        Login and Register a client so that you can get the Client Id and  Client Secret.
                                                                        Follow instructions  <a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/stu3/view/clients.html" target="_blank">here</a> for client registration.
                                                                    </li>
                                                                    <li style="margin-bottom:8px;">
                                                                        Once you have a Client Id and Client Secret follow the guidelines according to the Smart On FHIR to request resources.
                                                                    </li>
                                                                </ul>
                                                            </li>
                                                        </ol>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div  role="tabpanel" class="tab-pane fade in active" id="3">
                                    <div class="row" >
                                        <div class="col-md-6">
                                            <ol>
                                                <li>
                                                    The server URI for R4 open mode is : <%=request.getScheme() + "://" +
                                                        request.getServerName() +
                                                        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                                                %>/open/r4/fhir
                                                </li>
                                                <li>
                                                    The server URI for R4 secure mode is : <%=request.getScheme() + "://" +
                                                        request.getServerName() +
                                                        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                                                %>/secure/r4/fhir
                                                </li>
                                            </ol>
                                        </div>
                                        <div class="col-md-6">
                                            <button class="btn btn-primary center-block ladda-button" style="margin-top: -18px;margin-left: 300px;color:white" ><a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/r4/view/newuser.html" target="_blank" style="color:white;">Register</a></button>
                                            <button style="     width: 74px; margin-left: 409px;color:white" class="btn btn-primary center-block ladda-button"><a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/r4/view/clients.html" target="_blank" style="color:white;">Login</a></button>
                                        </div>
                                    </div>

                                    <div  style="min-height:300px;">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="panel panel-default" style="min-height:300px;">
                                                    <div style="background-image:linear-gradient(to bottom, #f5f5f5 0%, #e8e8e8 100%);background-repeat:repeat-x" class="panel-heading">
                                                        Using the Open FHIR Server For R4:
                                                    </div>
                                                    <div class="panel-body">
                                                        A client can use the open server by making a FHIR request using a FHIR READ or SEARCH operation.
                                                        <ol style="margin-top:10px;">
                                                            <li>
                                                                For example a simple READ can be executed as follows:
                                                                <ul style="list-style:outside none disc;margin-bottom:8px;">
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
	                 %>/open/r4/fhir/Patient/1" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/r4/fhir/Patient/1</a> - XML format.</li>
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/r4/fhir/Patient/1?_format=json" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/r4/fhir/Patient/1?_format=json</a> - JSON format.</li>
                                                                </ul>
                                                            </li>
                                                            <li>
                                                                For example a simple SEARCH can be executed as follows:
                                                                <ul style="list-style:outside none disc;margin-bottom:8px;">
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/r4/fhir/Patient/?_id=1" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/r4/fhir/Patient/?_id=1</a> - XML format.</li>
                                                                    <li>GET <a href="<%=request.getScheme() + "://" +
	                request.getServerName() +
	                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/r4/fhir/Patient/?_id=1&_format=json" target="_blank"><%=request.getScheme() + "://" +
                                                                            request.getServerName() +
                                                                            ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) %>/open/r4/fhir/Patient/?_id=1_format=json</a> -  JSON format.</li>
                                                                </ul>
                                                            </li>
                                                            <li style="margin-bottom:8px;">
                                                                Click here for more details on <a href="http://hl7.org/fhir/search.html" target="_blank">FHIR Search</a>.
                                                            </li>
                                                            <li>
                                                                For a list of Patients <a style="cursor:pointer;" onclick="loadPatients('r4');">Click here</a>.
                                                            </li>
                                                        </ol>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="panel panel-default" style="min-height:355px;">
                                                    <div style="background-image:linear-gradient(to bottom, #f5f5f5 0%, #e8e8e8 100%);background-repeat:repeat-x" class="panel-heading">
                                                        Using the Secure FHIR Server For R4:
                                                    </div>
                                                    <div class="panel-body">
                                                        <ol>
                                                            <li>
                                                                The Secure FHIR server follows the implementation of Smart On FHIR specifications.
                                                            </li>
                                                            <li>
                                                                In order to use the FHIR Server with the appropriate tokens the following steps need to be followed
                                                                <ul style="list-style:outside none disc;">
                                                                    <li style="margin-bottom:8px;">
                                                                        Create a user account in the FHIR Authorization Server:
                                                                        Follow instructions <a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/r4/view/newuser.html" target="_blank">here</a> for user creation.
                                                                    </li>
                                                                    <li style="margin-bottom:8px;">
                                                                        Login and Register a client so that you can get the Client Id and  Client Secret.
                                                                        Follow instructions <a href="<%=request.getScheme() + "://" +request.getServerName() + ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )%>/secure/r4/view/clients.html" target="_blank">here</a> for client registration.
                                                                    </li>
                                                                    <li style="margin-bottom:8px;">
                                                                        Once you have a Client Id and Client Secret follow the guidelines according to the Smart On FHIR to request resources.
                                                                    </li>
                                                                </ul>
                                                            </li>
                                                        </ol>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>

</div>



<!-- Content ends here -->

<%-- Footer Starts here --%>

<site-footer ng-version="2.4.7" _nghost-vni-31=""  style="height: 52px;">
    <footer _ngcontent-vni-31="">
        <div _ngcontent-vni-31="" class="container" role="contentinfo">
            <div _ngcontent-vni-31="" class="row">
                <div _ngcontent-vni-31="" class="col-sm-6 text-left">
                    <site-version _ngcontent-vni-31="" _nghost-vni-19=""><p _ngcontent-vni-19="" id="versionandrelease">
                        <!-- <p><strong>Version:</strong> 1.0 | <strong>Released:</strong> 2/28/2017</p>
                        </p> -->
                    </site-version>
                </div>
                <div _ngcontent-vni-31="" class="col-sm-6 text-right">
                    Inquiries or questions, email <a _ngcontent-vni-31="" href="mailto:edge-test-tool@googlegroups.com" tabindex="100">edge-test-tool@googlegroups.com</a>
                </div>
            </div>
            <div _ngcontent-vni-31="" class="row">
                <div _ngcontent-vni-31="" class="col-sm-6 text-left">
                    <site-news-announcements _ngcontent-vni-31="" _nghost-vni-27="">
                        <button _ngcontent-vni-27="" class="btn btn-link footer-link-btn" type="button" onclick="opennewsmodal();">News &amp;
                            Announcements
                        </button>
                    </site-news-announcements>
                    |
                    <site-release-notes _ngcontent-vni-31="" _nghost-vni-29="">
                        <button _ngcontent-vni-29="" class="btn btn-link footer-link-btn" type="button" onclick="openreleasemodal();">Release Notes
                        </button>
                    </site-release-notes>
                </div>
                <div _ngcontent-vni-31="" class="col-sm-6 text-right">
                    <a _ngcontent-vni-31="" href="http://www.hhs.gov/Privacy.html" tabindex="100">Privacy
                        Policy</a> | <a _ngcontent-vni-31="" href="http://www.hhs.gov/Disclaimer.html" tabindex="100">Disclaimers</a>
                </div>
            </div>
        </div>
    </footer>
</site-footer>

<%-- Footer ends here --%>
<script type="text/javascript" src="https://code.jquery.com/jquery-1.12.0.min.js"></script>
<script src="${pageContext.request.contextPath}/view/js/url.js"></script>

<script type="text/javascript" src="https://cdn.datatables.net/1.10.11/js/jquery.dataTables.min.js"></script>
<link href="https://cdn.datatables.net/1.10.11/css/jquery.dataTables.min.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/view/js/remarkable.min.js"></script>

<link href="${pageContext.request.contextPath}/view/css/bootstrap.css" rel="stylesheet" type="text/css" />
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>

<link href="${pageContext.request.contextPath}/view/css/fhir.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/view/css/homepage.css" rel="stylesheet">



<!-- Patient List Modal-->
<div class="modal fade" tabindex="-1" role="dialog" id="patientlist-modal">
    <div class="modal-dialog" style="width:900px">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">List of Patients</h4>
            </div>
            <div class="modal-body" style="overflow:auto;">
                <table id="patientList" class="display" cellspacing="0" width="100%">
                    <thead>
                    <tr>
                        <th>Patient Id</th>
                        <th>Name</th>
                        <th>Date of Birth</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" id="respclosebtn">Close</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- News and Announcements Modal -->
<div class="modal fade" tabindex="-1" role="dialog" id="news-modal">
    <div class="modal-dialog" style="width:600px">
        <div class="modal-content">
            <div class="modal-header">
            </div>
            <div class="modal-body" style="overflow:auto;" id="newscontent">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<!-- News and Announcements Modal End-->

<!-- Release Notes Modal -->
<div class="modal fade" tabindex="-1" role="dialog" id="release-modal">
    <div class="modal-dialog" style="width:90%">
        <div class="modal-content">
            <div class="modal-header">
            </div>
            <div class="modal-body" style="overflow:auto;" id="releasecontent">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<!-- Release Notes Modal End-->


<!-- Patient List Modal-->
<script type="text/javascript">
    $(document).ready(function(){

        // TO display Version and Released date in footer
        $.ajax({
            url:"https://raw.githubusercontent.com/siteadmin/SITE-Content/master/Version.md",
            type:"GET",
            success:function(data,status,xhr){
                var md_content = data;
                var md = new Remarkable();
                var html_content = md.render(md_content);
                $('#versionandrelease').html(html_content);
            },
            error:function(e){
                console.log(e);
            }
        });
    });

    loadPatients = function(val) {
        var url = "/open/authorize/launchpatient";
        if (val == "dstu2"){
            url="/open/authorize/launchpatient";
        }else if (val == "stu3"){
            url="/open/stu3/authorize/launchpatient";
        }else if(val == "r4"){
            url="/open/r4/authorize/launchpatient";
        }

        $('#patientList').dataTable({
            "destroy": true,
            "bLengthChange": false,
            "bPaginate": false,
            "bFilter":false,
            "info":false,
            "ajax":url,
            "sAjaxDataProp": "",
            "columns": [{
                "data": function (data, type, row) {
                    var firstColumn = JSON.parse(data.data);
                    var userpoc = "<div onclick='patientId(\""+firstColumn.id+"\")' class='col-md-12' style='cursor:pointer'>"+firstColumn.id+"</div>";
                    return userpoc;
                }
            },{
                "data": function (data, type, row) {
                    var secondColumn = JSON.parse(data.data);
                    if(secondColumn.name) {
                        var family = secondColumn.name[0].family;
                        var given = secondColumn.name[0].given;
                        var fullName = family+" "+given;
                        var userpoc = "<div onclick='patientId(\""+secondColumn.id+"\")' class='col-md-12' style='cursor:pointer'>"+fullName+"</div>";
                        return userpoc;
                    }

                }
            },{
                "data": function (data, type, row) {
                    var thirdColumn = JSON.parse(data.data);
                    var userpoc = "<div onclick='patientId(\""+thirdColumn.id+"\")' class='col-md-12' style='cursor:pointer'>"+thirdColumn.birthDate+"</div>";
                    return userpoc;
                }
            }
            ]
        });

        $("#patientlist-modal").modal({
            backdrop: 'static',
            keyboard: false
        });
    }

    opennewsmodal = function(){
        $.ajax({
            url:"https://raw.githubusercontent.com/siteadmin/SITE-Content/master/NewsAndAnnouncements.md",
            type:"GET",
            success:function(data,status,xhr){
                var md_content = data;
                var md = new Remarkable();
                var html_content = md.render(md_content);
                $('#newscontent').html(html_content);
                $('#news-modal').modal('show');
            },
            error:function(e){
                console.log(e);
            }
        });
    }

    openreleasemodal = function(){
        $.ajax({
            url:"https://raw.githubusercontent.com/siteadmin/SITE-Content/master/ReleaseNotes.md",
            type:"GET",
            success:function(data,status,xhr){
                var md_content = data;
                var md = new Remarkable();
                var html_content = md.render(md_content);
                $('#releasecontent').html(html_content);
                $('#release-modal').modal('show');
            },
            error:function(e){
                console.log(e);
            }
        });
    }
</script>

</body>
</html>