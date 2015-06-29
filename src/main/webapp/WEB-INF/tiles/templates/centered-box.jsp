<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<tiles:insertAttribute name="setup" ignore="true"/>

<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8">
        <tiles:insertAttribute name="head"/>
    </head>
    <body>
        <div style="display: table; width: 100%; height: 100%;">
            <div style="display: table-cell; vertical-align: middle;">
                <tiles:insertAttribute name="page"/>
            </div>
        </div>

        <tiles:insertTemplate template="messages.jsp"/>

        <link rel="stylesheet" href="<c:url value="/assets/application.css"/>">
        <script src="<c:url value="/assets/application.js"/>"></script>
    </body>
</html>
