<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
    <c:when test="${not empty greetings}">
        <ul>
            <c:forEach var="greeting" items="${greetings}">
                <li>${greeting.name}&nbsp;(${greeting.value})&nbsp;<strong>#${greeting.id}</strong></li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <strong>Nothing to show</strong>
    </c:otherwise>
</c:choose>
