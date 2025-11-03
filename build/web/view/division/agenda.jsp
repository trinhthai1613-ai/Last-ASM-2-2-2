<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Division agenda</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 24px; }
            table { border-collapse: collapse; width: 100%; margin-top: 12px; }
            th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
            th { background: #f0f0f0; }
            .status-approved { background: #c8f7c5; }
            .status-pending { background: #fff8c4; }
            .status-rejected { background: #ffd6d6; }
            form.filters { margin-top: 12px; display: flex; gap: 12px; align-items: flex-end; }
            form.filters label { display: block; font-weight: bold; }
            form.filters input { padding: 4px 8px; }
            .error { color: #b71c1c; margin-top: 12px; }
        </style>
    </head>
    <body>
        <jsp:include page="../util/greeting.jsp" />
        <h1>Division agenda</h1>
        <form method="get" class="filters">
            <div>
                <label>From date</label>
                <input type="date" name="from" value="${fromDate}" />
            </div>
            <div>
                <label>Number of days</label>
                <input type="number" min="1" max="31" name="days" value="${days}" />
            </div>
            <div>
                <input type="submit" value="Apply" />
            </div>
        </form>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        <c:if test="${empty error}">
            <table>
                <thead>
                    <tr>
                        <th>Employee</th>
                        <c:forEach items="${timeline}" var="date">
                            <th><fmt:formatDate value="${date}" pattern="dd/MM" /></th>
                        </c:forEach>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${rows}" var="row">
                        <tr>
                            <td style="text-align: left;"><c:out value="${row.employee.name}"/></td>
                            <c:forEach items="${row.statuses}" var="status">
                                <c:set var="css" value="" />
                                <c:set var="text" value="" />
                                <c:choose>
                                    <c:when test="${status eq 1}">
                                        <c:set var="css" value="status-approved" />
                                        <c:set var="text" value="Approved" />
                                    </c:when>
                                    <c:when test="${status eq 0}">
                                        <c:set var="css" value="status-pending" />
                                        <c:set var="text" value="Pending" />
                                    </c:when>
                                    <c:when test="${status eq 2}">
                                        <c:set var="css" value="status-rejected" />
                                        <c:set var="text" value="Rejected" />
                                    </c:when>
                                </c:choose>
                                <td class="${css}">
                                    <c:choose>
                                        <c:when test="${empty text}">-</c:when>
                                        <c:otherwise>${text}</c:otherwise>
                                    </c:choose>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </body>
</html>
