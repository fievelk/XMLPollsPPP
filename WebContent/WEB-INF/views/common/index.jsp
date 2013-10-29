<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>

<div class="row hero-unit">
	<div class="span12">
		<h1 id="introduzione">XML Polls</h1>
		<h2>PPP</h2>
	<div class="row">
		  <div class="span12">
		    <h2 class="text-info">Lista dei sondaggi disponibili:</h2>
		      <p>
				<c:forEach items="${codeTitles}" var="codeTitle">
		      	<span class="badge">${codeTitle.key}</span> <a href="${pageContext.request.contextPath}/polls/${codeTitle.key}">${codeTitle.value}</a><br/>
		      	</c:forEach>
		      </p>
	    	<h2 class="text-info">Statistiche dei sondaggi:</h2>
		      <p>
				<c:forEach items="${codeTitles}" var="codeTitle">
		      	<span class="badge">${codeTitle.key}</span> <a href="${pageContext.request.contextPath}/polls/${codeTitle.key}/stats.do">${codeTitle.value}</a><br/>
		      	</c:forEach>
		      </p>
   	    	<h2 class="text-info">Path dei file per il submit:</h2>
		      <p>
		      	<span class="badge">1</span> <a href="${pageContext.request.contextPath}/resources/submittedPollprova.xml">submittedPollprova.xml</a><br/>
		      </p>
		 </div>
	</div>
	</div>

</div>