<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="title" scope="request" class="java.lang.String"/>
<jsp:useBean id="servletName" scope="request" class="java.lang.String"/>
<jsp:useBean id="reqUri" scope="request" class="java.lang.String"/>
<jsp:useBean id="exceptionName" scope="request" class="java.lang.String"/>
<jsp:useBean id="exceptionMessage" scope="request" class="java.lang.String"/>
<jsp:useBean id="statusCode" scope="request" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/error.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div>
    <h1>Social network</h1>
    <h2>Error page</h2><br>
    <h3><i>${title}</i></h3>
    <p><i>Exception Name: </i>${exceptionName}</p>
    <p><i>Exception Message: </i>${exceptionMessage}</p>
    <p><i>Servlet Name: </i>${servletName}</p>
    <p><i>Requested URI: </i>${reqUri}</p>
    <p><i>Status Code: </i>${statusCode}</p>
    <a class="link" href="login">Home page</a><br>
</div>
</body>
</html>
