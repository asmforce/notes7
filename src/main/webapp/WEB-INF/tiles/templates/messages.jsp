<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<div class="messages-wrapper">
    <%-- A template for new popup messages --%>
    <script type="application/x-template" data-message>
        <div class="ui careless <@= this.classes @> message">
            <i class="close icon" data-dismiss></i>
            <div class="header"><@= this.title @></div>
            <p><@= this.message @></p>
        </div>
    </script>
</div>
