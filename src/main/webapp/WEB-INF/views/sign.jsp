<%@ page import="com.asmx.controllers.data.GenericResponse" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="STATUS_SUCCESS" value="<%= GenericResponse.STATUS_SUCCESS %>"/>
<c:set var="STATUS_UNEXPECTED" value="<%= GenericResponse.STATUS_UNEXPECTED %>"/>
<c:set var="STATUS_UNAUTHORISED" value="<%= GenericResponse.STATUS_UNAUTHORISED %>"/>
<c:set var="STATUS_INVALID_FORM" value="<%= GenericResponse.STATUS_INVALID_FORM %>"/>

<div class="ui one column stackable center aligned page grid">
    <div class="column six wide left aligned">
        <form:form id="signForm" action="sign" method="POST">
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
                        <input name="username" placeholder="<spring:message code="sign.username.placeholder"/>" type="text" data-ajax>
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

<script type="text/javascript">
    $('#signForm').form({
        username: {
            identifier: 'username',
            rules: [{
                type: 'empty',
                prompt: '<spring:message code="sign.username.missing"/>'
            }]
        },
        password: {
            identifier: 'password',
            rules: [{
                type: 'empty',
                prompt: '<spring:message code="sign.password.missing"/>'
            }]
        }
    }, {
        inline: true,
        onSuccess: function() {
            var form = $(this);
            var formUi = form.find('.ui.form').first();
            var submitButton = form.find('.submit.button').first();
            var requestData = {};

            var fieldsToSubmit = form.find('input[data-ajax]');

            // Collect form data
            fieldsToSubmit.each(function() {
                var input = $(this);
                var name = input.attr('name');
                if (name !== undefined) {
                    requestData[name] = input.val();
                }
            });

            $.ajax({
                url: '<spring:url value="/sign"/>',
                type: 'POST',
                dataType: 'json',
                data: requestData,
                beforeSend: function() {
                    formUi.addClass('disabled');
                    submitButton.addClass('loading');
                    fieldsToSubmit.each(function(index) {
                        var field = $(this);
                        if (field.prop('disabled')) {
                            // If a field is already disabled then it wont be enabled when the request finishes
                            delete fieldsToSubmit[index];
                        } else {
                            field.prop('disabled', true);
                        }
                    });
                },
                complete: function() {
                    formUi.removeClass('disabled');
                    submitButton.removeClass('loading');
                    fieldsToSubmit.each(function() {
                        var field = $(this);
                        field.prop('disabled', false);
                    });
                },
                success: function(data) {
                    if (data) {
                        switch (data.statusCode) {
                            case ${STATUS_SUCCESS}:
                                var redirection = '<spring:url value="/"/>' + data.redirection;
                                location.href = redirection.replace("//", "/");
                                break;

                            case ${STATUS_UNAUTHORISED}:
                                // Clean password input
                                form.find('input[data-ajax][name="password"]').val("");
                                // Fall-through
                            case ${STATUS_UNEXPECTED}:
                            case ${STATUS_INVALID_FORM}:
                                if (data.messages) {
                                    $(data.messages).each(function(index, message) {
                                        console.log(message);
                                        // TODO: show an error popup message
                                    });
                                } else {
                                    // TODO: show an error popup message
                                }
                                break;
                        }
                    } else {
                        // TODO: show an error popup message
                    }
                },
                error: function() {
                    console.log(arguments);
                    // TODO: show an error popup message
                }
            });
            return false;
        }
    });
</script>
