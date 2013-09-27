<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>

<div class="row hero-unit">
	<div class="span12">
		<h1 id="introduzione">XML Polls</h1>
		<h2>Pierpaolo Pantone ${prova}</h2>
	<div class="row">
		  <div class="span12">
		    <h2 class="text-info">Lista dei sondaggi disponibili:</h2>
		      <p>
<%--  		      	<c:forEach items="${pollTitles}" var="title"> --%>
<%-- 		      	<span class="badge">1</span> <a href="${pageContext.request.contextPath}/polls/">${title}</a><br/> --%>
<%-- 		      	</c:forEach> --%>
				<c:forEach items="${codeTitles}" var="codeTitle">
		      	<span class="badge">${codeTitle.key}</span> <a href="${pageContext.request.contextPath}/polls/">${codeTitle.value}</a><br/>
		      	</c:forEach>





     	
<%--  		      	<c:import url="${pageContext.request.contextPath}/resources/poll2.xml" var="xmlDoc"/> --%>
<%-- 		      	<x:parse doc="${xmlDoc}" var="output1"/> --%>
<%-- 				<x:out select="$output1" /> --%>
			    
			    
<%-- 			    <c:forEach items="${polls}" var="poll"> --%>
<!-- 			      <span class="badge">1</span> -->
<%-- 			      <x:parse doc="${poll}" var="output"/> --%>
<%-- 			      <b>Titolo:</b> <x:out select="$output/pollHead"/><br/> --%>
<%-- 			    </c:forEach>		      	 --%>
		      </p>
		 </div>
	</div>
	</div>

</div>