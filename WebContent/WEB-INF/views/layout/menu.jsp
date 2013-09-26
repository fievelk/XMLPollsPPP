<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="navbar navbar-inverse navbar-fixed-top">
   <div class="navbar-inner">
     <div class="container">
       <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
         <span class="icon-bar"></span>
       </a>
       <a class="brand" href="${pageContext.request.contextPath}">XML Polls</a>
<%--       <div class="nav-collapse collapse">
		<ul class="nav">
			    <li class="dropdown">
			    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Home<b class="caret"></b></a>
 				<ul class="dropdown-menu">
					<li><a href="${pageContext.request.contextPath}/common/browse.do">BROWSE</a></li>
					<li><a href="${pageContext.request.contextPath}/common/search.do">SEARCH</a></li>
				</ul>
				</li>
		</ul>
       </div> --%>
     </div>
   </div>
</div>