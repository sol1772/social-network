<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.apache.commons.text.CaseUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Account info</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/account.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account info</h3>
    <div class="head">
        <div class="head-cont">
            <h5>
                <p><i>Surname: </i>${account.lastName}</p>
                <p><i>Name: </i>${account.firstName}</p>
                <p><i>Middle name: </i>${account.middleName}</p>
                <fmt:parseDate value="${account.dateOfBirth}" pattern="yyyy-MM-dd" var="dateOfBirth" type="date"/>
                <p><i>Date of birth: </i><fmt:formatDate pattern="dd.MM.yyyy" value="${dateOfBirth}"/></p>
                <p><i>Username: </i>${account.userName}</p>
                <p><i>E-mail: </i>${account.email}</p>
                <c:forEach items="${account.phones}" var="phone">
                    <p><i>${CaseUtils.toCamelCase(phone.phoneType.toString(), true, ' ')} phone: </i>${phone.number}</p>
                </c:forEach>
                <c:forEach items="${account.addresses}" var="addr">
                    <p><i>${CaseUtils.toCamelCase(addr.addrType.toString(), true, ' ')} address: </i>${addr.addr}</p>
                </c:forEach>
                <p><i>About: </i>${StringUtils.isEmpty(account.addInfo)?"---" : account.addInfo}</p>
                <p><i>Groups: </i>
                    <c:forEach items="${requestScope.groups}" var="group">
                        <span><a class="groups" href="group?id=${group.id}">${group.title}</a></span>
                    </c:forEach>
                </p>
            </h5>
        </div>
        <div class="head-logo">
            <img src="<c:url value="upload?command=account&id=${account.id}"/>" alt="Account image"/>
            <c:if test="${username.equals(account.userName)}">
                <form action="upload" method="get">
                    <div class="btn-group d-block mx-auto">
                        <button class="btn btn-outline-info btn-sm" name="submit" id="change" value="Change">Change
                        </button>
                        <button class="btn btn-outline-info btn-sm" name="submit" id="delete"
                                onclick="return confirm('Delete?')" value="Delete">Delete
                        </button>
                    </div>
                    <p style="color: darkgreen">${requestScope.message}</p>
                    <input type="hidden" name="path" value="account">
                </form>
            </c:if>
        </div>
    </div>
    <br>
    <h3 style="color: darkgreen">Account wall</h3>
    <form name="msg_form" action="messages_account" method="post">
        <c:forEach items="${requestScope.posts}" var="msg">
            <div class="container-wall">
                <div class="wall"><h5><i><fmt:parseDate value="${msg.createdAt}" pattern="yyyy-MM-dd'T'HH:mm"
                                                        var="parsedDateTime" type="both"/>
                    <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${parsedDateTime}"/></i></h5></div>
                <div class="wall"><h5>${msg.textContent}</h5></div>
                <h5>
                    <button name="submit" value="del_msg" id="del_msg"
                            onclick="return confirm('Delete?')">x
                    </button>
                </h5>
                <input type="hidden" name="accId" value="${account.id}">
                <input type="hidden" name="trgId" value="${account.id}">
                <input type="hidden" name="msg_id" value="${msg.id}">
            </div>
        </c:forEach>
    </form>
    <br>
    <div class="links">
        <c:choose>
            <c:when test="${username.equals(account.userName)}">
                <ul class="nav justify-content-center">
                    <li class="nav-item">
                        <a class="nav-link" href="messages?id=${account.id}">Messages</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="message?trgId=${account.id}">New post</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="group-add">New group</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="account-edit">Edit</a>
                    </li>
                </ul>
            </c:when>
            <c:when test="${!(StringUtils.isEmpty(username) || username.equals(account.userName))}">
                <a class="link" href="message?trgId=${account.id}">Send message</a>
            </c:when>
        </c:choose>
    </div>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete() {
        if (confirm("Delete?")) {
            return true;
        }
    }
</script>
</body>
</html>
