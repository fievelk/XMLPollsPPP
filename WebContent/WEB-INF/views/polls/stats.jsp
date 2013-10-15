<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h2 class="text-info">Lista dei sondaggi disponibili:</h2>
<%-- <div class="row">
	<div class="span3">
		${graphContainer.SVGcode}
	</div>
	<div class="span9">
		<c:forEach items="${graphContainer.legendMap}" var="legend">
			<div style="width:50px;height:20px;border:1px solid #000;background-color:${legend.value};"></div>${legend.key.code} - ${legend.key.content}
			<br>			
		</c:forEach>
	</div>
</div>
<hr />
<div class="row">
	<div class="span3">
		${graphContainer.SVGcode}
	</div>
	<div class="span9">
		<c:forEach items="${graphContainer.legendMap}" var="legend">
			<div style="width:50px;height:20px;border:1px solid #000;background-color:${legend.value};"></div>${legend.key.code} - ${legend.key.content}
			<br>			
		</c:forEach>
	</div>
</div> --%>

<c:forEach items="${graphContainerList}" var="graphContainer">
<div class="row">
	<div class="span3">
		${graphContainer.SVGcode}
	</div>
	<div class="span9">
		<c:forEach items="${graphContainer.legendMap}" var="legend">
			<div style="width:50px;height:20px;border:1px solid #000;background-color:${legend.value};"></div>${legend.key.code} - ${legend.key.content}
			<br>			
		</c:forEach>
	</div>
</div>
<hr />
</c:forEach>
