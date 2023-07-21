<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.apache.commons.text.CaseUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<jsp:useBean id="group" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Group"/>
<jsp:useBean id="owner" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Group</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/group.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Group info</h3>
    <div class="head">
        <div class="head-cont">
            <table class="table table-sm" id="tbl_group">
                <caption></caption>
                <tr>
                    <td><i>Title</i></td>
                    <td>${group.title}</td>
                </tr>
                <tr>
                    <td><i>About</i></td>
                    <td>${StringUtils.isEmpty(group.metaTitle)?"---" : group.metaTitle}</td>
                </tr>
                <tr>
                    <td><i>Owner</i></td>
                    <td>${owner.firstName} ${owner.lastName}</td>
                </tr>
                <tr>
                    <td><i>Date of creation</i></td>
                    <fmt:parseDate value="${group.createdAt}" pattern="yyyy-MM-dd" var="createdAt" type="date"/>
                    <td><fmt:formatDate pattern="dd.MM.yyyy" value="${createdAt}"/></td>
                </tr>
                <c:if test="${!group.members.isEmpty()}">
                    <tr>
                        <td><i>Members</i></td>
                        <td><c:forEach items="${group.members}" var="member">
                            <span>${member.account.firstName} ${member.account.lastName}
                                (${CaseUtils.toCamelCase(member.role.toString(), true, ' ')})</span>
                        </c:forEach></td>
                    </tr>
                </c:if>
            </table>
        </div>
        <div class="head-logo">
            <img src="${root}/group/${group.id}/image" alt="Group image" width="150px">
            <c:if test="${username.equals(owner.userName)}">
                <form action="${root}/upload" method="get">
                    <div class="btn-group d-block mx-auto">
                        <button class="btn btn-outline-info btn-sm" name="option" id="change" value="Change">Change
                        </button>
                        <button class="btn btn-outline-info btn-sm" name="option" id="delete"
                                onclick="return confirm('Delete?')" value="Delete">Delete
                        </button>
                    </div>
                    <p style="color: darkgreen">${requestScope.message}</p>
                    <input type="hidden" name="path" value="group">
                    <input type="hidden" name="id" value="${group.id}">
                </form>
            </c:if>
        </div>
    </div>
    <c:if test="${username.equals(owner.userName)}">
        <ul class="nav justify-content-center">
            <li class="nav-item">
                <a class="nav-link" href="${root}/group/${group.id}/edit">Edit</a>
            </li>
        </ul>
    </c:if>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
