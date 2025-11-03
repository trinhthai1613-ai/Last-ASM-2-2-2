<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="greeting">
    <c:choose>
        <c:when test="${sessionScope.auth ne null}">
            <p>
                Session of: <strong><c:out value="${sessionScope.auth.displayname}"/></strong><br/>
                Employee: <c:out value="${sessionScope.auth.employee.id}"/> - <c:out value="${sessionScope.auth.employee.name}"/>
            </p>
            <p>
                <a href="${pageContext.request.contextPath}/home">Home</a> |
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </p>
        </c:when>
        <c:otherwise>
            <p>You are not logged in yet!</p>
        </c:otherwise>
    </c:choose>
</div>
