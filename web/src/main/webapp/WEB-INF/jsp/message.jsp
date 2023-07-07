<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="targetAccount" scope="request"
             class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="message" scope="request" class="java.lang.String"/>
<jsp:useBean id="report" scope="request" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Send message / ${targetAccount.userName}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/messages_account.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Send message</h3>
    <form id="msg_form" action="messages_account" method="post">
        <h3>Message / ${targetAccount.userName}</h3>
        <div class="field">
            <input type="hidden" name="accId" value=${account.id}>
            <input type="hidden" name="trgId" value=${targetAccount.id}>
            <label for="message"></label>
            <textarea rows="2" cols="50" name="message" id="message" placeholder="Your message">${message}</textarea>
            <input type="submit" value="Send" class="button_ok" name="submit"/>
            <p class="report">${report}</p>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
