<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
<div class="wrapper">
    <jsp:include page="header.jsp"/>
    <br><br>
    <h1>Social network</h1>
    <h2>Send message</h2>
    <br>
    <form name="msg_form" action="messages_account" method="post">
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
</body>
</html>
