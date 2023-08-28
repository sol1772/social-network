<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Social network</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/login.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Welcome to Social network!</h2>
    <div>
        <p class="error" id="error">${error}</p>
    </div>
    <form id="login_form" action="${root}/login" method="post">
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text">E-mail</span>
            <input type="email" id="email" name="email" aria-label="E-mail" class="form-control">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text">Password</span>
            <input type="password" id="password" name="password" aria-label="Password" class="form-control">
        </div>
        <div class="form-check">
            <input type="checkbox" name="rememberMe" class="form-check-input" id="rememberMe">
            <label class="form-check-label" for="rememberMe">Remember me</label>
        </div>
        <button class="btn btn-outline-info d-block mx-auto" id="button_ok">OK</button>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/login.js"/>"></script>
</body>
</html>