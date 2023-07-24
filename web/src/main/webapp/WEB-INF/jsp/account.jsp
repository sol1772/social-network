<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.apache.commons.text.CaseUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Account info</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/account.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account info</h3>
    <div class="head">
        <div class="head-cont">
            <table class="table table-sm" id="tbl_account">
                <caption></caption>
                <tr>
                    <td><i>Surname</i></td>
                    <td>${account.lastName}</td>
                </tr>
                <tr>
                    <td><i>Name</i></td>
                    <td>${account.firstName}</td>
                </tr>
                <tr>
                    <td><i>Middle name</i></td>
                    <td>${account.middleName}</td>
                </tr>
                <tr>
                    <td><i>Date of birth</i></td>
                    <fmt:parseDate value="${account.dateOfBirth}" pattern="yyyy-MM-dd" var="dateOfBirth" type="date"/>
                    <td><fmt:formatDate pattern="dd.MM.yyyy" value="${dateOfBirth}"/></td>
                </tr>
                <tr>
                    <td><i>Username</i></td>
                    <td>${account.userName}</td>
                </tr>
                <tr>
                    <td><i>E-mail</i></td>
                    <td>${account.email}</td>
                </tr>
                <c:forEach items="${account.phones}" var="phone">
                    <tr>
                        <td><i>${CaseUtils.toCamelCase(phone.phoneType.toString(), true, ' ')} phone</i></td>
                        <td>${phone.number}</td>
                    </tr>
                </c:forEach>
                <c:forEach items="${account.addresses}" var="addr">
                    <tr>
                        <td><i>${CaseUtils.toCamelCase(addr.addrType.toString(), true, ' ')} address</i></td>
                        <td>${addr.addr}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td><i>About</i></td>
                    <td>${StringUtils.isEmpty(account.addInfo)?"---" : account.addInfo}</td>
                </tr>
                <tr>
                    <td><i>Groups</i></td>
                    <td><c:forEach items="${requestScope.groups}" var="group">
                        <span><a class="groups" href="${root}/group/${group.id}">${group.title}</a></span>
                    </c:forEach></td>
                </tr>
            </table>
        </div>
        <div class="head-logo">
            <img src="${root}/account/${account.id}/image" alt="Account image" width="150px">
            <form action="${root}/upload" method="get">
                <div class="btn-group d-block mx-auto">
                    <button class="btn btn-outline-info btn-sm" name="option" id="change" value="Change">Change
                    </button>
                    <button class="btn btn-outline-info btn-sm" name="option" id="delete"
                            onclick="return confirm('Delete?')" value="Delete">Delete
                    </button>
                </div>
                <p style="color: darkgreen">${requestScope.message}</p>
                <input type="hidden" name="path" value="account">
                <input type="hidden" name="id" value="${account.id}">
            </form>
        </div>
    </div>
    <br>
    <h3 style="color: darkgreen">Account wall</h3>
    <form name="msg_form" action="${root}/messages_account" method="post">
        <table class="table table-sm table-hover" id="tbl_posts">
            <caption></caption>
            <c:forEach items="${requestScope.posts}" var="msg" varStatus="loop">
                <tr>
                    <fmt:parseDate value="${msg.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime"
                                   type="both"/>
                    <td id="msg_date"><i><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${parsedDateTime}"/></i></td>
                    <td><c:out value="${msg.textContent}"/></td>
                    <td id="msg_del">
                        <input type="hidden" name="msgId_" id="msgId_" value="${msg.id}">
                        <button class="btn btn-outline-info btn-sm" name="submit" value="delMsg" id="delMsg">x</button>
                    </td>
                </tr>
            </c:forEach>
            <input type="hidden" name="accId" value="${account.id}">
            <input type="hidden" name="trgId" value="${account.id}">
            <input type="hidden" name="msgId" id="msgId">
        </table>
    </form>
    <br>
    <div class="links">
        <c:choose>
            <c:when test="${!(StringUtils.isEmpty(username) || username.equals(account.userName))}">
                <a class="link" href="${root}/message?trgId=${account.id}">Send message</a>
            </c:when>
        </c:choose>
    </div>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/messages.js"/>"></script>
</body>
</html>
