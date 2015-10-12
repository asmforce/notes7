<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="asmx" uri="http://asmx.com/jsp" %>

<div class="messages-wrapper">
    <%-- A template for new popup messages --%>
    <script type="application/x-template" data-message>
        <div class="ui careless <@= this.classes @> message" data-message>
            <i class="close icon" data-dismiss></i>
            <div class="header"><@= this.title @></div>
            <p><@= this.message @></p>
        </div>
    </script>

    <c:if test="${not empty messages}">
        <c:set var="randomMessageId" value="${asmx:randomId(15)}"/>
        <script type="text/javascript" id="message-${randomMessageId}" data-message>
            (function() {
                <c:forEach var="msg" items="${messages}">
                ASMX.Messages.show({
                    message: '${asmx:escapeJs(msg.message)}',
                    title: '${asmx:escapeJs(msg.title)}',
                    classes: '${asmx:escapeJs(msg.classes)}'
                    <c:if test="${not empty msg.id}">
                    , id: '${asmx:escapeJs(msg.id)}'
                    </c:if>
                });
                </c:forEach>
                $('#message-${randomMessageId}').remove();
            })();
        </script>
    </c:if>
</div>
