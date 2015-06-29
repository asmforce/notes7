<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="http://code.jquery.com/jquery-1.11.2.min.js"></script>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/1.11.6/semantic.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/1.11.6/semantic.js"></script>

<script src="<c:url value="/assets/nprogress.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/assets/nprogress.css"/>">

<link rel="shortcut icon" href="<c:url value="/assets/favicon.png"/>">

<c:choose>
    <c:when test="${not empty title and not empty subtitle}">
        <title>${subtitle}&nbsp;&#x2014;&nbsp;${title}</title>
    </c:when>
    <c:when test="${not empty title}">
        <title>${title}</title>
    </c:when>
    <c:when test="${not empty subtitle}">
        <title>${subtitle}&nbsp;&#x2014;&nbsp;<spring:message code="appname"/></title>
    </c:when>
    <c:otherwise>
        <title><spring:message code="appname"/></title>
    </c:otherwise>
</c:choose>
