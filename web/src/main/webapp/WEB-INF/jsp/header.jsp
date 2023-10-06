<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Common navigation bar</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/header.css"/>
    <link id="contextPathHolder" data-contextPath="${root}"/>
</head>
<body>
<nav class="navbar navbar-expand-sm navbar-light" id="navbar_search">
    <div class="container-fluid">
        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-sm-0">
                <c:if test="${!empty sessionScope.account}">
                    <li class='nav-item dropdown'>
                        <a class='nav-link dropdown-toggle' href='#' id='navbarDropdownAbout' role='button'
                           data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>Menu</a>
                        <div class='dropdown-menu' aria-labelledby='navbarDropdownAbout'>
                            <a class="nav-link" href="${root}/messages?id=${account.id}">Messages</a>
                            <a class="nav-link" href="${root}/chat">Chat</a>
                            <a class="nav-link" href="${root}/message/?trgId=${account.id}">New post</a>
                            <a class="nav-link" href="${root}/group/add">New group</a>
                            <a class="nav-link" href="${root}/account/${account.id}/edit">Edit</a>
                            <a class="nav-link" href="${root}/account/${account.id}/settings">Settings</a>
                        </div>
                    </li>
                </c:if>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <form class="d-flex" action="${root}/search" method="get" autocomplete="off">
                        <div class="autocomplete">
                            <input class="form-control me-2" type="search" name="q" id="search" placeholder="Search..."
                                   aria-label="" value="${requestScope.q}">
                        </div>
                        <button class="btn btn-outline-info" id="btn_search" type="submit">🔍</button>
                        <input type="hidden" name="page" value=1>
                    </form>
                </li>
                <c:if test="${!empty sessionScope.account}">
                    <li class="nav-item">
                        <a class="nav-link"
                           href="${root}/account/${sessionScope.account.id}">User: ${sessionScope.username}</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${root}/logout">Logout</a>
                    </li>
                </c:if>
                <c:if test="${empty sessionScope.account}">
                    <li class="nav-item">
                        <a class="nav-link" href="${root}/login">Login</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link nav-link-o" href="${root}/account/add">Sign-up</a>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>
<script src="<c:url value="/js/header.js"/>" type="module"></script>
</body>
</html>
