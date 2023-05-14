<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Social network</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div>
    <h1>Welcome to Social network!</h1>
    <form action="login" method="post">
        <div class="field">
            <label for="check1">E-mail</label>
            <input type="email" name="email" id="check1">
        </div>
        <div class="field">
            <label for="check2">Password</label>
            <input type="password" name="password" id="check2"><br>
        </div>
        <div class="field">
            <input type="checkbox" name="rememberMe" value="lsRememberMe" id="rememberMe">
            <label for="rememberMe">Remember me</label>
        </div>
        <div style="text-align: center">
            <input type="submit" value="OK" class="button_ok">
            <p><a href="registration">Registration</a></p>
            <p class="error" style="color: red">
                ${error}
            </p>
        </div>
    </form>
</div>
</body>
</html>