<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:insertAttribute name="setup" ignore="true"/>

<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8">
        <tiles:insertAttribute name="head"/>
    </head>
    <body>
        <tiles:insertAttribute name="page"/>
    </body>
</html>
