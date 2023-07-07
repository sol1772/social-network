<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="accounts" scope="request" type="java.util.List"/>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Messages</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/messages_account.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account messages</h3>
    <form name="msg_form" action="messages_account" method="post" style="width: 50%">
        <c:if test="${accounts != null}">
            <c:forEach items="${accounts}" var="targetAccount">
                <ul class="nav justify-content-center">
                    <li class="nav-item">
                        <a class="nav-link"
                           href="messages_account?id=${account.id}&trgId=${targetAccount.id}">${targetAccount}</a>
                    </li>
                </ul>
            </c:forEach>
            <c:if test="${accounts.size() == 0}">No messages</c:if>
        </c:if>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
