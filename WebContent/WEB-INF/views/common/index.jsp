<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>

<div class="row hero-unit">
	<div class="span12">
		<h1 id="introduzione">XML Polls</h1>
		<h2>Pierpaolo Pantone</h2>
	<div class="row">
		  <div class="span12">
		    <h2 class="text-info">Lista dei sondaggi disponibili:</h2>
		      <p>
				<c:forEach items="${codeTitles}" var="codeTitle">
		      	<span class="badge">${codeTitle.key}</span> <a href="${pageContext.request.contextPath}/polls/${codeTitle.key}">${codeTitle.value}</a><br/>
		      	</c:forEach>
		      </p>
		 </div>
	</div>
	</div>

</div>