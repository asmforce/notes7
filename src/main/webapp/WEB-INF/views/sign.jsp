<%@ page import="com.asmx.controllers.data.entities.GenericResponseJson" %>
<%@ page import="com.asmx.controllers.data.entities.MessageJson" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="STATUS_SUCCESS" value="<%= GenericResponseJson.STATUS_SUCCESS %>"/>
<c:set var="STATUS_UNEXPECTED" value="<%= GenericResponseJson.STATUS_UNEXPECTED %>"/>
<c:set var="STATUS_UNAUTHORISED" value="<%= GenericResponseJson.STATUS_UNAUTHORISED %>"/>
<c:set var="STATUS_INVALID_FORM" value="<%= GenericResponseJson.STATUS_FORGED_REQUEST %>"/>

<c:set var="ERROR_ID_CLIENT_SERVER" value="<%= MessageJson.ERROR_ID_CLIENT_SERVER %>"/>

<c:set var="CLASS_INFO" value="<%= MessageJson.CLASS_INFO %>"/>
<c:set var="CLASS_WARNING" value="<%= MessageJson.CLASS_WARNING %>"/>
<c:set var="CLASS_ERROR" value="<%= MessageJson.CLASS_ERROR %>"/>

<c:set var="STATUS_AJAX_ERROR" value="error"/>
<c:set var="STATUS_AJAX_NO_CONTENT" value="nocontent"/>
<c:set var="STATUS_AJAX_PARSER_ERROR" value="parsererror"/>
<c:set var="STATUS_AJAX_TIMEOUT" value="timeout"/>
<c:set var="STATUS_AJAX_ABORT" value="abort"/>

<div class="ui one column middle aligned center aligned grid">
    <div class="center-tile column left aligned">
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
    (function () {
        var ERROR_ID_CLIENT_SERVER = '${ERROR_ID_CLIENT_SERVER}';

        var DATA_PROCESSING_ERROR = {
            title: '<spring:message javaScriptEscape="true" code="error"/>',
            message: '<spring:message javaScriptEscape="true" code="error.data"/>',
            classes: '${CLASS_ERROR}',
            id: ERROR_ID_CLIENT_SERVER
        };

        $('#signForm').form({
            fields: {
                username: {
                    identifier: 'username',
                    rules: [{
                        type: 'empty',
                        prompt: '<spring:message javaScriptEscape="true" code="error.form.field_required"/>'
                    }]
                },
                password: {
                    identifier: 'password',
                    rules: [{
                        type: 'empty',
                        prompt: '<spring:message javaScriptEscape="true" code="error.form.field_required"/>'
                    }]
                }
            },
            inline: true,
            onSuccess: function() {
                var form = $(this);
                var formUi = form.find('#signForm .ui.form').first();
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
                    url: '<spring:url javaScriptEscape="true" value="/sign"/>',
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
                                    var redirection = '<spring:url javaScriptEscape="true" value="/"/>' + data.redirection;
                                    location.href = redirection.replace("//", "/");
                                    break;

                                case ${STATUS_UNAUTHORISED}:
                                    // Clean password input
                                    form.find('input[data-ajax][name="password"]').val("");
                                    // Fall-through
                                case ${STATUS_UNEXPECTED}:
                                case ${STATUS_INVALID_FORM}:
                                default:
                                    if (data.messages) {
                                        ASMX.Messages.showAll(data.messages);
                                    } else {
                                        ASMX.Messages.show(DATA_PROCESSING_ERROR);
                                    }
                                    break;
                            }
                        } else {
                            ASMX.Messages.show(DATA_PROCESSING_ERROR);
                        }
                    },
                    error: function($xhr, textStatus, errorThrown) {
                        var errorDetailsRequired = false;
                        var msg = {
                            title: '<spring:message javaScriptEscape="true" code="error"/>',
                            classes: '${CLASS_ERROR}',
                            id: ERROR_ID_CLIENT_SERVER
                        };

                        switch (textStatus) {
                            case '${STATUS_AJAX_ERROR}':
                            case '${STATUS_AJAX_TIMEOUT}':
                            case '${STATUS_AJAX_ABORT}':
                                msg.message = '<spring:message javaScriptEscape="true" code="error.network"/>';
                                errorDetailsRequired = true;
                                break;

                            case '${STATUS_AJAX_PARSER_ERROR}':
                                msg = DATA_PROCESSING_ERROR;
                                break;

                            default:
                                msg.message = '<spring:message javaScriptEscape="true" code="error.unknown"/>';
                                errorDetailsRequired = true;
                                break;
                        }
                        if (errorDetailsRequired && errorThrown) {
                            msg.message += ' (' + errorThrown + ')';
                        }
                        ASMX.Messages.show(msg);
                    }
                });
                return false;
            }
        });
    })();
</script>
