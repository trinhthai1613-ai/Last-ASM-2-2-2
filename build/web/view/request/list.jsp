<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Leave requests</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 24px; }
            table { border-collapse: collapse; width: 100%; margin-top: 12px; }
            th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
            th { background: #f0f0f0; }
            .message { background: #e0f7e9; border: 1px solid #4caf50; padding: 10px; margin-top: 12px; }
            .actions a { margin-right: 8px; }
        </style>
    </head>
    <body>
        <jsp:include page="../util/greeting.jsp" />
        <h1>Leave requests</h1>
        <c:if test="${not empty message}">
            <div class="message">${message}</div>
        </c:if>
        <p>
            <a href="create">Create new request</a>
        </p>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Created by</th>
                    <th>Reason</th>
                    <th>From</th>
                    <th>To</th>
                    <th>Status</th>
                    <th>Processed by</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty requestScope.rfls}">
                    <tr>
                        <td colspan="8">No leave requests found.</td>
                    </tr>
                </c:if>
                <c:forEach items="${requestScope.rfls}" var="r">
                    <tr>
                        <td><c:out value="${r.id}"/></td>
                        <td><c:out value="${r.created_by.name}"/></td>
                        <td><c:out value="${r.reason}"/></td>
                        <td><fmt:formatDate value="${r.from}" pattern="yyyy-MM-dd" /></td>
                        <td><fmt:formatDate value="${r.to}" pattern="yyyy-MM-dd" /></td>
                        <td>${r.statusLabel}</td>
                        <td>
                            <c:choose>
                                <c:when test="${r.processed_by ne null}">
                                    <c:out value="${r.processed_by.name}"/>
                                </c:when>
                                <c:otherwise>
                                    -
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="actions">
                            <c:choose>
                                <c:when test="${r.canProcess}">
                                    <a href="review?rid=${r.id}">Review</a>
                                </c:when>
                                <c:when test="${r.processed_by ne null}">
                                    <span>Reviewed</span>
                                </c:when>
                                <c:otherwise>
                                    <span>Waiting</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>
