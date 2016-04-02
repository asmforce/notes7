<%@ page import="com.asmx.controllers.data.entities.MessageJson" %>
<%@ page import="com.asmx.controllers.data.entities.GenericResponseJson" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="useMinLibs" value="false"/>

<c:choose>
    <c:when test="${useMinLibs}">
        <c:url var="src" value="/assets/vendor/jquery-2.1.4.min.js"/>
    </c:when>
    <c:otherwise>
        <c:url var="src" value="/assets/vendor/jquery-2.1.4.js"/>
    </c:otherwise>
</c:choose>
<script src="${src}"></script>

<c:choose>
    <c:when test="${useMinLibs}">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.1.8/semantic.min.css">
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.1.8/semantic.css">
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${useMinLibs}">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.1.8/semantic.min.js"></script>
    </c:when>
    <c:otherwise>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.1.8/semantic.js"></script>
    </c:otherwise>
</c:choose>

<c:url var="src" value="/assets/vendor/jquery-dateFormat.js"/>
<script src="${src}"></script>

<c:url var="src" value="/assets/vendor/nprogress.js"/>
<script src="${src}"></script>

<c:url var="src" value="/assets/vendor/nprogress.css"/>
<link rel="stylesheet" href="${src}">

<c:choose>
    <c:when test="${useMinLibs}">
        <c:url var="src" value="/assets/vendor/jquery.jqote2.min.js"/>
    </c:when>
    <c:otherwise>
        <c:url var="src" value="/assets/vendor/jquery.jqote2.js"/>
    </c:otherwise>
</c:choose>
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

<script type="text/javascript">
    declaration('RESPONSE:SUCCESS', <%= GenericResponseJson.STATUS_SUCCESS %>);
    declaration('RESPONSE:UNEXPECTED', <%= GenericResponseJson.STATUS_UNEXPECTED %>);
    declaration('RESPONSE:UNAUTHORISED', <%= GenericResponseJson.STATUS_UNAUTHORISED %>);
    declaration('RESPONSE:FORGED_REQUEST', <%= GenericResponseJson.STATUS_FORGED_REQUEST %>);

    declaration('MESSAGE_CLASS:INFO', '<%= MessageJson.CLASS_INFO %>');
    declaration('MESSAGE_CLASS:WARNING', '<%= MessageJson.CLASS_WARNING %>');
    declaration('MESSAGE_CLASS:ERROR', '<%= MessageJson.CLASS_ERROR %>');

    declaration('address:sign', '<spring:url javaScriptEscape="true" value="/sign"/>');
    declaration('address:notes', '<spring:url javaScriptEscape="true" value="/notes"/>');

    tr('error', '<spring:message code="error" javaScriptEscape="true"/>');
    tr('error.unknown', '<spring:message code="error.unknown" javaScriptEscape="true"/>');
    tr('error.unexpected', '<spring:message code="error.unexpected" javaScriptEscape="true"/>');
    tr('error.network', '<spring:message code="error.network" javaScriptEscape="true"/>');
    tr('error.data', '<spring:message code="error.data" javaScriptEscape="true"/>');
    tr('error.forged_request', '<spring:message code="error.forged_request" javaScriptEscape="true"/>');
    tr('error.form.field_required', '<spring:message code="error.form.field_required" javaScriptEscape="true"/>');

    tr('sign.unauthorized.title', '<spring:message code="sign.unauthorized.title" javaScriptEscape="true"/>');
    tr('sign.unauthorized', '<spring:message code="sign.unauthorized" javaScriptEscape="true"/>');
</script>
