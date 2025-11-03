<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Review leave request</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 24px; }
            .card { max-width: 480px; border: 1px solid #ccc; padding: 16px; border-radius: 4px; }
            dl { display: grid; grid-template-columns: 140px 1fr; row-gap: 8px; }
            dt { font-weight: bold; }
            .errors { color: #b71c1c; margin-bottom: 12px; }
            .actions { margin-top: 16px; }
            .actions button { padding: 8px 16px; margin-right: 8px; }
        </style>
    </head>
    <body>
        <jsp:include page="../util/greeting.jsp" />
        <h1>Review leave request</h1>
        <div class="card">
            <dl>
                <dt>ID</dt>
                <dd><c:out value="${rfl.id}"/></dd>
                <dt>Created by</dt>
                <dd><c:out value="${rfl.created_by.name}"/></dd>
                <dt>From</dt>
                <dd><fmt:formatDate value="${rfl.from}" pattern="yyyy-MM-dd" /></dd>
                <dt>To</dt>
                <dd><fmt:formatDate value="${rfl.to}" pattern="yyyy-MM-dd" /></dd>
                <dt>Reason</dt>
                <dd><c:out value="${rfl.reason}"/></dd>
                <dt>Status</dt>
                <dd>${rfl.statusLabel}</dd>
                <dt>Processed by</dt>
                <dd>
                    <c:choose>
                        <c:when test="${rfl.processed_by ne null}"><c:out value="${rfl.processed_by.name}"/></c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </dd>
            </dl>
            <c:if test="${not empty errors}">
                <div class="errors">
                    <ul>
                        <c:forEach items="${errors}" var="error">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            <c:choose>
                <c:when test="${rfl.canProcess}">
                    <form method="post" action="review" class="actions">
                        <input type="hidden" name="rid" value="${rfl.id}" />
                        <button type="submit" name="decision" value="approve">Approve</button>
                        <button type="submit" name="decision" value="reject">Reject</button>
                        <a href="list">Back to list</a>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="actions">
                        <p>No further action can be taken for this request.</p>
                        <a href="list">Back to list</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </body>
</html>
