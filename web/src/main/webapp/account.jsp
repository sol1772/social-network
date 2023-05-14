<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/account.css"/>
</head>
<body>
<div class="wrapper">
    <jsp:include page="header.jsp"/>
    <br><br>
    <h1>Social network</h1>
    <h2>Account info</h2>
    <div class="head">
        <div class="head-cont">
            <h3>
                <p><i>Surname: </i>${account.lastName}</p>
                <p><i>Name: </i>${account.firstName}</p>
                <p><i>Middle name: </i>${account.middleName}</p>
                <p><i>Date of birth: </i><fmt:formatDate type="date" value="${account.dateOfBirth}"/></p>
                <p><i>E-mail: </i>${account.email}</p>
                <c:forEach items="${account.phones}" var="phone">
                    <p><i>${CaseUtils.toCamelCase(phone.phoneType.toString(), true, ' ')} phone: </i>${phone.number}</p>
                </c:forEach>
                <c:forEach items="${account.addresses}" var="addr">
                    <p><i>${CaseUtils.toCamelCase(addr.addrType.toString(), true, ' ')} address: </i>${addr.address}</p>
                </c:forEach>
                <p><i>About: </i>${StringUtils.isEmpty(account.addInfo)?"---" : account.addInfo}</p>
                <p><i>Groups: </i>
                    <c:forEach items="${requestScope.groups}" var="group">
                        <span><a class="groups" href="group?id=${group.id}">${group.title}</a></span>
                    </c:forEach>
                </p>
            </h3>
        </div>
        <div class="head-logo">
            <img src="<c:url value="upload?command=account&id=${account.id}"/>" alt="Account image"/>
            <c:if test="${username.equals(account.userName)}">
                <form action="upload" method="get">
                    <p><input type="submit" name="submit" value="Change"/>
                        <input type="submit" name="submit" onclick="return confirm('Delete?')" value="Delete"/></p>
                    <script>
                        function confirmDelete() {
                            if (confirm("Delete?")) {
                                return true;
                            }
                        }
                    </script>
                    <p style="color: darkgreen">${requestScope.message}</p>
                    <input type="hidden" name="path" value="/account.jsp">
                </form>
            </c:if>
        </div>
    </div>
    <br>
    <h2>Account wall</h2>
    <c:forEach items="${account.messages}" var="msg">
        <c:if test="${msg.msgType=='PUBLIC'}">
            <div class="container">
                <div class="wall"><h4><i><fmt:parseDate value="${msg.createdAt}" pattern="yyyy-MM-dd'T'HH:mm"
                                                        var="parsedDateTime" type="both"/>
                    <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${parsedDateTime}"/></i></h4></div>
                <div class="wall"><h3>${msg.textContent}</h3></div>
            </div>
        </c:if>
    </c:forEach>
    <br>
    <c:choose>
        <c:when test="${StringUtils.isEmpty(username)}">
            <a class="link" href="login">Login</a><br>
        </c:when>
        <c:when test="${username.equals(account.userName)}">
            <a class="link" href="account-edit">Edit</a><br>
            <a class="link" href="group-add">New group</a>
        </c:when>
    </c:choose>
</div>
</body>
</html>