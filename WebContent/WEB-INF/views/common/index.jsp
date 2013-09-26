<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="row hero-unit">
	<div class="span12">
		<h1 id="introduzione">XML Polls</h1>
		<h2>Pierpaolo Pantone ${prova}</h2>
	<div class="row">
		  <div class="span12">
		    <h2 class="text-info">Lista dei sondaggi disponibili:</h2>
		      <p>
		      	<c:forEach items="${pollTitles}" var="title">
		      	<span class="badge">1</span> <a href="${pageContext.request.contextPath}/polls/">${title}</a><br/>
		      	</c:forEach>
		      </p>
		 </div>
	</div>
	</div>

</div>