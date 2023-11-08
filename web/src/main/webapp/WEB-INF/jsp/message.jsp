<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="targetAccount" scope="request"
             class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="message" scope="request" class="java.lang.String"/>
<jsp:useBean id="report" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Send message / ${targetAccount.userName}</title>
    <link rel="stylesheet" type="text/css" href="${root}/css/messages_account.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Send message</h3>
    <form name="msg_form" action="${root}/messages_account" method="post">
        <h3>Message / ${targetAccount.userName}</h3>
        <div class="input-group input-group-sm mb-3">
            <label for="message"></label>
            <textarea class="form-control form-control-sm" name="message" id="message"
                      placeholder="Your message">${message}</textarea>
            <input type="hidden" name="accId" value="${account.id}">
            <input type="hidden" name="trgId" value="${targetAccount.id}">
            <button class="btn btn-outline-info btn-sm" type="submit" name="submit" id="btn_send" value="Send">Send
            </button>
            <p class="report">${report}</p>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
