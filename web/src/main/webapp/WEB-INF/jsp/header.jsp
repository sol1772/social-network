<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Common parts</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
<div>
    <c:if test="${!StringUtils.isEmpty(sessionScope.username)}">
        <form id="logout" action="logout" method="post" style="float: right; margin-left: 10px; margin-right: 10px;">
            <input type="submit" value="Logout">
        </form>
        <span style="color: darkgreen; font-style: italic; float: right; margin-left: 10px; margin-right: 10px;">
            <a href="account?id=${sessionScope.account.id}">User: ${sessionScope.username}</a>
        </span>
    </c:if>
    <form class="search" action="search" method="get"
          style="float: right; margin-left: 10px; margin-right: 10px;">
        <input type="search" name="q" id="search" placeholder="Search"><label for="search"></label>
        <button type="submit"><i class="fa fa-search"></i></button>
        <input type="hidden" name="page" value=1>
    </form>
</div>
</body>
</html>
