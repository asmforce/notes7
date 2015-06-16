<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="message" value="?"/>
<c:if test="${not empty code}">
    <c:set var="message" value="${code}"/>
</c:if>

<div class="ui one column stackable center aligned page grid">
    <h1 class="ui center aligned icon red header">
        <i class="warning sign icon"></i>(${message})
    </h1>
</div>
