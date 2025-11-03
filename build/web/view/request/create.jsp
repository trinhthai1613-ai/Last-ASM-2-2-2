<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create leave request</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 24px; }
            form { max-width: 420px; }
            label { display: block; margin-top: 12px; }
            input[type="date"], textarea { width: 100%; padding: 6px; }
            textarea { height: 120px; }
            .errors { color: #b71c1c; margin-bottom: 12px; }
            .actions { margin-top: 16px; }
            .actions input { padding: 8px 16px; }
        </style>
    </head>
    <body>
        <jsp:include page="../util/greeting.jsp" />
        <h1>Create leave request</h1>
        <c:if test="${not empty errors}">
            <div class="errors">
                <ul>
                    <c:forEach items="${errors}" var="error">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <form method="post" action="create">
            <label>From date</label>
            <input type="date" name="from" value="${rfl.from}" />

            <label>To date</label>
            <input type="date" name="to" value="${rfl.to}" />

            <label>Reason</label>
            <textarea name="reason"><c:out value="${rfl.reason}"/></textarea>

            <div class="actions">
                <input type="submit" value="Submit" />
                <a href="list">Back to list</a>
            </div>
        </form>
    </body>
</html>
