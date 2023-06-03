<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="accounts" scope="request" type="java.util.List"/>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Messages</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/default.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div>
    <h1>Social network</h1>
    <h2>Account messages</h2>
    <br>
    <form name="msg_form" action="messages_account" method="post">
        <c:if test="${accounts != null}">
            <c:forEach items="${accounts}" var="targetAccount">
                <p><a class="link"
                      href="messages_account?id=${account.id}&trgId=${targetAccount.id}">${targetAccount}</a></p>
            </c:forEach>
            <c:if test="${accounts.size() == 0}">No messages</c:if>
        </c:if>
    </form>
</div>
</body>
</html>
