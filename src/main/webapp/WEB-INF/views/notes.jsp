<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div style="display: flex; flex-direction: column; width: 100%; height: 100%;" data-controller="notes">
    <script type="application/x-template">
        <div class="ui segment" id="<@= this.id @>">
            <div class="ui items">
                <div class="item">
                    <div class="content">
                        <div class="header">
                            <h4>
                                <i class="icon quote left"></i>
                                <a href="<spring:url value="/note/" htmlEscape="true"/><@= this.id @>"><@= ASMX.Fn.timestamp(this.ideaTime) @></a>
                            </h4>
                        </div>
                        <div class="description">
                            <p><@= this.text @></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </script>

    <div style="flex: 0 0 auto;">
        <div class="ui top attached segment">
            <div class="ui fluid icon input">
                <input type="text" placeholder="<spring:message code="form.search.placeholder"/>">
                <i class="search icon"></i>
            </div>
        </div>
    </div>

    <div style="flex: 1 1 auto; overflow-x: hidden; overflow-y: scroll;" class="js-pagination-container">
        <div class="ui bottom attached segment l-notes-wrapper"></div>
    </div>
</div>
