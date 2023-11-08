<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="title" scope="request" class="java.lang.String"/>
<jsp:useBean id="servletName" scope="request" class="java.lang.String"/>
<jsp:useBean id="reqUri" scope="request" class="java.lang.String"/>
<jsp:useBean id="exceptionName" scope="request" class="java.lang.String"/>
<jsp:useBean id="exceptionMessage" scope="request" class="java.lang.String"/>
<jsp:useBean id="statusCode" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error page</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/error.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Error page</h3>
    <table class="table table-sm table-bordered table-hover">
        <caption><h4><i>${title}</i></h4></caption>
        <tr>
            <td><i>Exception Name</i></td>
            <td>${exceptionName}</td>
        </tr>
        <tr>
            <td><i>Exception Message</i></td>
            <td>${exceptionMessage}</td>
        </tr>
        <tr>
            <td><i>Servlet Name</i></td>
            <td>${servletName}</td>
        </tr>
        <tr>
            <td><i>Requested URI</i></td>
            <td>${reqUri}</td>
        </tr>
        <tr>
            <td><i>Status Code</i></td>
            <td>${statusCode}</td>
        </tr>
    </table>
    <a class="link" href="${root}/login">Home page</a><br>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
