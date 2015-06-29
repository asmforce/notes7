<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="ui segment">
    <c:forEach var="space" items="${spaces}">
        <div class="ui segment">
            <strong>${space.name}&nbsp;${space.creationTime}</strong>
            <br/>
            ${space.description}
        </div>
    </c:forEach>
</div>
