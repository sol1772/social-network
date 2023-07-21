<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="messages" scope="request" type="java.util.List"/>
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
    <title>Messages / ${targetAccount.userName}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/messages_account.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account messages</h3>
    <form name="msg_form" action="${root}/messages_account" method="post">
        <h5 style="text-align: left">Messages / ${targetAccount.userName}</h5>
        <p class="report">${report}</p>
        <div class="input-group input-group-sm mb-3">
            <label for="message"></label>
            <textarea class="form-control form-control-sm" name="message" id="message"
                      placeholder="Your message">${message}</textarea>
            <input type="hidden" name="accId" value="${account.id}">
            <input type="hidden" name="trgId" value="${targetAccount.id}">
            <button class="btn btn-outline-info btn-sm" type="submit" name="submit" id="btn_send" value="Send">Send
            </button>
        </div>
        <table class="table table-sm table-hover" id="tbl_messages">
            <caption></caption>
            <c:forEach items="${messages}" var="msg" varStatus="loop">
                <tr>
                    <fmt:parseDate value="${msg.createdAt}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime"
                                   type="both"/>
                    <td id="msg_date"><i><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${parsedDateTime}"/></i></td>
                    <td id="msg_user">
                        <c:choose>
                            <c:when test="${msg.account.id==account.id}">${account.userName}</c:when>
                            <c:otherwise>${targetAccount.userName}</c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${msg.textContent}"/></td>
                    <td id="msg_del">
                        <input type="hidden" name="msgId_" id="msgId_" value="${msg.id}">
                        <button class="btn btn-outline-info btn-sm" name="submit" value="delMsg" id="delMsg">x</button>
                    </td>
                </tr>
            </c:forEach>
            <input type="hidden" name="msgId" id="msgId">
        </table>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/messages.js"/>"></script>
</body>
</html>
