<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="root" value="${pageContext.request.contextPath}"/>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Chat WebSocket</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/chat.css"/>
    <link id="contextPathHolder" data-contextPath="${root}"/>
</head>
<body>
<jsp:include page="header.jsp"/>

<div id="username-page" class="hidden">
    <div class="username-page-container">
        <input type="hidden" id="name" name="username" value="${account.userName}" class="form-control"/>
    </div>
</div>
<div id="chat-page">
    <div class="chat-container">
        <div class="chat-header">
            <h2 style="color: darkgreen">Social network</h2>
            <h3 style="color: darkgreen">Chat</h3>
        </div>
        <div class="connecting">
            Connecting...
        </div>
        <ul id="messageArea">
        </ul>
        <form id="messageForm" name="messageForm">
            <div class="form-group">
                <div class="input-group clearfix">
                    <input type="text" id="message" placeholder="Type a message..." autocomplete="off"
                           class="form-control" aria-label=""/>
                    <button class="btn btn-outline-info btn-sm" type="submit" name="submit" id="btn_send">Send</button>
                </div>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/sockjs.js"/>"></script>
<script src="<c:url value="/js/stomp.js"/>"></script>
<script src="<c:url value="/js/chat.js"/>"></script>
</body>
</html>
