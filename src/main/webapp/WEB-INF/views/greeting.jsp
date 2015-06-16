<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
    <c:when test="${greeting ne null}">
        <span>${greeting.name}&nbsp;(${greeting.value})&nbsp;<strong>#${greeting.id}</strong></span>
    </c:when>
    <c:otherwise>
        <strong>Nothing to show</strong>
    </c:otherwise>
</c:choose>
