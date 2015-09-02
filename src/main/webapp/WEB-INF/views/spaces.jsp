<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="ui segment">
    <c:forEach var="space" items="${spaces}">
        <div class="ui segment">
            <div class="header">
                <h4>
                    <i class="icon cube"></i>
                    <a href="<spring:url value="/space/${space.id}" htmlEscape="true"/>">${space.name}</a>
                </h4>
            </div>
            <div class="description">
                <p><strong>${space.creationTime}</strong></p>
                <p>${space.description}</p>
            </div>
        </div>
    </c:forEach>
</div>
