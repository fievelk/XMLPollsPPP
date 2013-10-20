<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose> 
<c:when test="${empty graphContainerList && empty nonReqGraphContainerList}">
<h2 class="text-info">Non ci sono statistiche per questo sondaggio.</h2>
</c:when>

<c:otherwise>

<h2 class="text-info">Statistiche relative alle singole domande:</h2>
<c:forEach items="${graphContainerList}" var="graphContainer">
<div class="row">
<h4>${graphContainer.question.code}: ${graphContainer.question.content}</h4>
</div>
<div class="row">
	<div class="span3">
		${graphContainer.SVGcode}
	</div>
	<div class="span9" style="margin-top:15px;">
		<c:forEach items="${graphContainer.legendMap}" var="legend">
		<c:set var="option" value="${legend.key}" />
			<div style="display:block">
				<div style="float:left; display:inline; width:50px;height:20px;border:1px solid #000;background-color:${legend.value};"></div>
				<div style="right; display:inline;">&nbsp;<strong>${option.percentValue}&#37; </strong> [${option.count}] - ${option.content} (${option.code})</div>
			</div>
			<br>
		</c:forEach>
	</div>
</div>
</c:forEach>
<hr />
<h2 class="text-info">Statistiche relative alle risposte opzionali nel sondaggio n°${skeletonId}</h2>
<c:forEach items="${nonReqGraphContainerList}" var="nonReqGraphContainer">
<div class="row">
</div>
<div class="row">
	<div class="span3">
		${nonReqGraphContainer.SVGcode}
	</div>
	<div class="span9" style="margin-top:15px;">
		<c:forEach items="${nonReqGraphContainer.legendMap}" var="legend">
		<c:set var="option" value="${legend.key}" />
			<div style="display:block">
				<div style="float:left; display:inline; width:50px;height:20px;border:1px solid #000;background-color:${legend.value};"></div>
				<div style="right; display:inline;">&nbsp;<strong>${option.percentValue}&#37; </strong> [${option.count}] - ${option.content}</div>
			</div>
			<br>
		</c:forEach>
	</div>
</div>
</c:forEach>
</c:otherwise>
</c:choose>


