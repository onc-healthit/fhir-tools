<%@page import="com.sun.xml.txw2.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<!-- Global site tag (gtag.js) - Google Analytics -->
<script async
	src="https://www.googletagmanager.com/gtag/js?id=UA-40163006-1"></script>
<script>
	window.dataLayer = window.dataLayer || [];
	function gtag() {
		dataLayer.push(arguments);
	}
	gtag('js', new Date());
	gtag('config', 'UA-40163006-1');
</script>
<!-- End Google Analytics -->

</head>
<body>
	<%-- header starts here --%>
	<site-header ng-version="2.4.7" _nghost-vni-33=""> <header
		_ngcontent-vni-33="">
	<div _ngcontent-vni-33="" class="site-header">
		<div _ngcontent-vni-33="" class="container">
			<a _ngcontent-vni-33="" class="site-logo"
				href="https://www.healthit.gov/" rel="external" title="HealthIT.gov">
				<img _ngcontent-vni-33="" alt="HealthIT.gov"
				src="${pageContext.request.contextPath}/view/images/site/healthit.gov.logo.png">
			</a>
		</div>
	</div>
	<!--template bindings={}--> </header> </site-header>
	<app-site-navigation> <nav>
	<div class="navbar navbar-default">
		<div class="container">
			<div class="navbar-header">
				<button class="navbar-toggle" data-target=".navbar-collapse"
					data-toggle="collapse" tabindex="1" type="button">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
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
				<li class="breadcrumb-item active"><span>Authentication
						Page</span></li>
			</ul>
			</breadcrumb>
		</div>
	</div>
	</nav>
	<div class="spacer"></div>
	<router-outlet></router-outlet> </app-site-navigation>

	<%-- header Ends here --%>

	<div class="col-md-12" style="margin-top: -40px;">
		<h1 style="margin-left: 13px; text-align: center;">FHIR Server -
			R4</h1>
	</div>
	<div class="container">

		<form
			action="<%=request.getScheme() + "://" + request.getServerName()
		+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
				|| "https".equals(request.getScheme()) && request.getServerPort() == 443
						? ""
						: ":" + request.getServerPort())
		+ request.getContextPath()%>/authorize"
			method="post">
			<input name="transaction_id" type="hidden"
				value=<%=request.getParameter("transaction_id")%>>
				
						<div class="col-md-12"
				style="margin-top: 10px; height: 1100px; width: 100%;">
				<h1>
					Hi
					<%=request.getParameter("name")%>!
				</h1>
				<h3>
					<b> <%=request.getParameter("cName")%>
					</b> is requesting access to the following resources :
				</h3>
				<h3>Do you approve?</h3>

				<div>
					<!-- <h4>Requesting access with the following scopes:</h4> -->

					<h4>Requested scopes are :</h4>

					<%
						String[] array = request.getParameter("scope").split(",");
					%>


					<%
						for (int i = 0; i < array.length; i++) {
					%>
					<span> <input class="messageCheckbox" type="checkbox"
						name="scopeList" value="<%=array[i]%>" checked> <label
						for="<%=array[i]%>" class="col-sm-3 control-label"> "<%=array[i]%>"
					</label> <br> <br /></span>
					<%
						}
					%>

				</div>
			</div>
	
			<div class="col-md-12"
				style="margin-top: 10px; height: 100px; width: 100%;">
				<div class="col-md-1">
					<input class="btn btn-primary" type="submit"  value="Allow"
						name="submit" id="allow">
				</div>
				<div class="col-md-1">
					<input class="btn btn-primary" type="submit" value="Deny"
						name="cancel" id="deny">
				</div>

			</div>
		</form>
	</div>
	
	

	<%-- Footer Starts here --%>


	<site-footer ng-version="2.4.7" _nghost-vni-31="" style="height: 52px;">
	<footer _ngcontent-vni-31=""
		style="position:fixed;bottom:0px; left: 0%; width:100%">
	<div _ngcontent-vni-31="" class="container" role="contentinfo">
		<div _ngcontent-vni-31="" class="row">
			<div _ngcontent-vni-31="" class="col-sm-6 text-left">
				<site-version _ngcontent-vni-31="" _nghost-vni-19="">
				<p _ngcontent-vni-19="" id="versionandrelease">
					<!-- <p><strong>Version:</strong> 1.0 | <strong>Released:</strong> 2/28/2017</p>
                        </p> -->
				</site-version>
			</div>
			<div _ngcontent-vni-31="" class="col-sm-6 text-right">
				Inquiries or questions, email <a _ngcontent-vni-31=""
					href="mailto:edge-test-tool@googlegroups.com" tabindex="100">edge-test-tool@googlegroups.com</a>
			</div>
		</div>
		<div _ngcontent-vni-31="" class="row">
			<div _ngcontent-vni-31="" class="col-sm-6 text-left">
				<site-news-announcements _ngcontent-vni-31="" _nghost-vni-27="">
				<button _ngcontent-vni-27="" class="btn btn-link footer-link-btn"
					type="button" onclick="opennewsmodal();">News &amp;
					Announcements</button>
				</site-news-announcements>
				|
				<site-release-notes _ngcontent-vni-31="" _nghost-vni-29="">
				<button _ngcontent-vni-29="" class="btn btn-link footer-link-btn"
					type="button" onclick="openreleasemodal();">Release Notes
				</button>
				</site-release-notes>
			</div>
			<div _ngcontent-vni-31="" class="col-sm-6 text-right">
				<a _ngcontent-vni-31="" href="http://www.hhs.gov/Privacy.html"
					tabindex="100">Privacy Policy</a> | <a _ngcontent-vni-31=""
					href="http://www.hhs.gov/Disclaimer.html" tabindex="100">Disclaimers</a>
			</div>
		</div>
	</div>
	</footer> </site-footer>

	<%-- Footer ends here --%>

	<script
		src="${pageContext.request.contextPath}/view/js/jquery-1.11.1.js"></script>

	<link href="${pageContext.request.contextPath}/view/css/bootstrap.css"
		rel="stylesheet" type="text/css" />
	<script src="${pageContext.request.contextPath}/view/js/bootstrap.js"></script>
	<link href="${pageContext.request.contextPath}/view/css/fhir.css"
		rel="stylesheet">
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/view/js/remarkable.min.js"></script>
	<!-- <script src="view/js/hoverMenu.js"></script> -->

	<!-- News and Announcements Modal -->
	<div class="modal fade" tabindex="-1" role="dialog" id="news-modal">
		<div class="modal-dialog" style="width: 600px">
			<div class="modal-content">
				<div class="modal-header"></div>
				<div class="modal-body" style="overflow: auto;" id="newscontent">

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	<!-- News and Announcements Modal End-->

	<!-- Release Notes Modal -->
	<div class="modal fade" tabindex="-1" role="dialog" id="release-modal">
		<div class="modal-dialog" style="width: 90%">
			<div class="modal-content">
				<div class="modal-header"></div>
				<div class="modal-body" style="overflow: auto;" id="releasecontent">

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	<!-- Release Notes Modal End-->

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							// TO display Version and Released date in footer
							$
									.ajax({
										url : "https://raw.githubusercontent.com/siteadmin/SITE-Content/master/Version.md",
										type : "GET",
										success : function(data, status, xhr) {
											var md_content = data;
											var md = new Remarkable();
											var html_content = md
													.render(md_content);
											$('#versionandrelease').html(
													html_content);
										},
										error : function(e) {
											console.log(e);
										}
									});
						});
		opennewsmodal = function() {
			$
					.ajax({
						url : "https://raw.githubusercontent.com/siteadmin/SITE-Content/master/NewsAndAnnouncements.md",
						type : "GET",
						success : function(data, status, xhr) {
							var md_content = data;
							var md = new Remarkable();
							var html_content = md.render(md_content);
							$('#newscontent').html(html_content);
							$('#news-modal').modal('show');
						},
						error : function(e) {
							console.log(e);
						}
					});
		}
		openreleasemodal = function() {
			$
					.ajax({
						url : "https://raw.githubusercontent.com/siteadmin/SITE-Content/master/ReleaseNotes.md",
						type : "GET",
						success : function(data, status, xhr) {
							var md_content = data;
							var md = new Remarkable();
							var html_content = md.render(md_content);
							$('#releasecontent').html(html_content);
							$('#release-modal').modal('show');
						},
						error : function(e) {
							console.log(e);
						}
					});
		}
	</script>

</body>
</html>