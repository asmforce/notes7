<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="message" value="${code}"/>
<c:if test="${empty code}">
    <c:choose>
        <c:when test="${pageContext.errorData.statusCode > 0}">
            <c:set var="message" value="${pageContext.errorData.statusCode}"/>
        </c:when>
        <c:otherwise>
            <c:set var="message" value="?"/>
        </c:otherwise>
    </c:choose>
</c:if>

<div class="ui one column stackable center aligned page grid">
    <h1 class="ui center aligned icon red header">
        <i class="warning sign icon"></i>(${message})
    </h1>
</div>
