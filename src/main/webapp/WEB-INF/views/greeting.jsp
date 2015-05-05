<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML>
<html>
    <head>
        <title>Getting Started: Serving Web Content</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>
    <body>
        <c:choose>
            <c:when test="${greeting ne null}">
                <span>${greeting.name}&nbsp;(${greeting.value})&nbsp;<strong>#${greeting.id}</strong></span>
            </c:when>
            <c:otherwise>
                <strong>Nothing to show</strong>
            </c:otherwise>
        </c:choose>
    </body>
</html>
