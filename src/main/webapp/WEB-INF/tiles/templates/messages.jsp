<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="messages-wrapper">
    <%-- A template for new popup messages --%>
    <script type="application/x-template" data-message>
        <div class="ui careless <@= this.classes @> message">
            <i class="close icon" data-dismiss></i>
            <div class="header"><@= this.title @></div>
            <p><@= this.message @></p>
        </div>
    </script>

    <c:if test="${not empty messages}">
        <c:forEach var="message" items="${messages}">
            <script type="text/javascript" data-message>
                (function() {
                    ASMX.Messages.show({
                        message: '<spring:escapeBody javaScriptEscape="true">${message.message}</spring:escapeBody>',
                        title: '<spring:escapeBody javaScriptEscape="true">${message.title}</spring:escapeBody>',
                        classes: '<spring:escapeBody javaScriptEscape="true">${message.classes}</spring:escapeBody>'
                        <c:if test="${not empty message.id}">
                        ,
                        id: '<spring:escapeBody javaScriptEscape="true">${message.id}</spring:escapeBody>'
                        </c:if>
                    });
                })();
            </script>
        </c:forEach>
    </c:if>
</div>
