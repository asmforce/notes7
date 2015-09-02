<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="ui segment">
    <div class="header">
        <h4>
            <i class="icon quote left"></i>
            <a href="#">${note.ideaTime}</a>
        </h4>
    </div>
    <div class="description">
        <p>${note.text}</p>
    </div>
</div>
