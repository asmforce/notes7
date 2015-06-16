<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertAttribute name="setup" ignore="true"/>

<!DOCTYPE HTML>
<html>
    <head>
        <tiles:insertAttribute name="head"/>
    </head>
    <body>
        <div style="display: table; width: 100%; height: 100%;">
            <div style="display: table-cell; vertical-align: middle;">
                <tiles:insertAttribute name="page"/>
            </div>
        </div>
    </body>
</html>
