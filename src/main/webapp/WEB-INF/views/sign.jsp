<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="ui one column middle aligned center aligned grid">
    <div class="center-tile column left aligned">
        <form:form id="signForm" action="sign" method="POST" data-controller="sign">
            <div class="ui top attached center aligned segment">
                <div class="ui green header">
                    <h2 class="ui">Notes7</h2>
                </div>
            </div>

            <div class="ui attached fluid form segment">
                <c:if test="${not empty user}">
                    <div class="ui info message">
                        <label><spring:message code="sign.resign" arguments="${user.name}"/></label>
                    </div>
                </c:if>

                <div class="ui field">
                    <label><spring:message code="sign.username"/></label>
                    <div class="ui left icon input">
                        <input name="username" placeholder="<spring:message code="sign.username.placeholder"/>" type="text" autofocus data-ajax>
                        <i class="user icon"></i>
                    </div>
                </div>

                <div class="ui field">
                    <label><spring:message code="sign.password"/></label>
                    <div class="ui left icon input">
                        <input name="password" placeholder="<spring:message code="sign.password.placeholder"/>" type="password" data-ajax>
                        <i class="lock icon"></i>
                    </div>
                </div>
            </div>

            <div class="ui bottom attached right aligned segment">
                <div class="ui field">
                    <div class="ui positive right labeled icon submit button">
                        <i class="sign in icon"></i>
                        <spring:message code="sign.sign_in"/>
                    </div>
                </div>
            </div>
        </form:form>
    </div>
</div>
