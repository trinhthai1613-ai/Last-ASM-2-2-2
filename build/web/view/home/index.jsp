<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 24px; }
            ul { list-style: disc inside; }
            li { margin: 6px 0; }
        </style>
    </head>
    <body>
        <jsp:include page="../util/greeting.jsp" />
        <h1>Welcome to the leave management portal</h1>
        <c:if test="${empty features}">
            <p>No features are assigned to your account. Please contact the administrator.</p>
        </c:if>
        <c:if test="${not empty features}">
            <p>Select one of the available features below:</p>
            <ul>
                <c:forEach items="${features}" var="entry">
                    <li><a href="${pageContext.request.contextPath}${entry.key}"><c:out value="${entry.value}"/></a></li>
                </c:forEach>
            </ul>
        </c:if>
    </body>
</html>
