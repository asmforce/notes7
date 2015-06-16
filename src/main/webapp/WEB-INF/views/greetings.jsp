<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:choose>
    <c:when test="${not empty greetings}">
        <ul>
            <c:forEach var="greeting" items="${greetings}">
                <li>
                    <a href="<spring:url value="/greeting/${greeting.id}"/>">${greeting.name}&nbsp;(${greeting.value})</a>
                </li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <strong>Nothing to show</strong>
    </c:otherwise>
</c:choose>
