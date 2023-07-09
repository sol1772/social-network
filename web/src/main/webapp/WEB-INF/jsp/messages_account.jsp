<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="messages" scope="request" type="java.util.List"/>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="targetAccount" scope="request"
             class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="message" scope="request" class="java.lang.String"/>
<jsp:useBean id="report" scope="request" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Messages / ${targetAccount.userName}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/messages_account.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account messages</h3>
    <form name="msg_form" action="messages_account" method="post">
        <h3>Messages / ${targetAccount.userName}</h3>
        <div class="field">
            <input type="hidden" name="accId" value="${account.id}">
            <input type="hidden" name="trgId" value="${targetAccount.id}">
            <label for="message"></label>
            <textarea rows="2" cols="50" name="message" id="message" placeholder="Your message">${message}</textarea>
            <input type="submit" value="Send" class="button_ok" name="submit"/>
            <p class="report">${report}</p>
        </div>
        <c:forEach items="${messages}" var="msg">
            <div class="container_msg">
                <div class="wall"><h5><i><fmt:parseDate value="${msg.createdAt}" pattern="yyyy-MM-dd'T'HH:mm"
                                                        var="parsedDateTime" type="both"/>
                    <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${parsedDateTime}"/></i></h5>
                </div>
                <div class="wall"><h5>
                    <c:choose>
                        <c:when test="${msg.account.id==account.id}">${account.userName}</c:when>
                        <c:otherwise>${targetAccount.userName}</c:otherwise>
                    </c:choose></h5>
                </div>
                <div class="wall">
                    <h5>${msg.textContent}</h5>
                </div>
                <h5>
                    <button name="submit" value="del_msg" id="del_msg"
                            onclick="return confirm('Delete?')">x
                    </button>
                </h5>
                <input type="hidden" name="msg_id" value="${msg.id}">
            </div>
        </c:forEach>
    </form>
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
