<%@ page import="com.asmx.controllers.data.entities.MessageJson" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url var="src" value="/assets/vendor/jquery-2.1.4.js"/>
<script src="${src}"></script>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.0.8/semantic.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.0.8/semantic.js"></script>

<c:url var="src" value="/assets/vendor/jquery-dateFormat.js"/>
<script src="${src}"></script>

<c:url var="src" value="/assets/vendor/nprogress.js"/>
<script src="${src}"></script>

<c:url var="src" value="/assets/vendor/nprogress.css"/>
<link rel="stylesheet" href="${src}">

<c:url var="src" value="/assets/vendor/jquery.jqote2.js"/>
<script src="${src}"></script>

<c:url var="src" value="/assets/favicon.png"/>
<link rel="shortcut icon" href="${src}">

<c:url var="src" value="/assets/application.css"/>
<link rel="stylesheet" href="${src}">

<c:url var="src" value="/assets/application.js"/>
<script src="${src}"></script>

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
