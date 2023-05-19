<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<jsp:useBean id="searchString" scope="request" class="java.lang.String"/>
<jsp:useBean id="accountsTotal" scope="request" class="java.lang.String"/>
<jsp:useBean id="accountsPages" scope="request" class="java.lang.String"/>
<jsp:useBean id="groupsTotal" scope="request" class="java.lang.String"/>
<jsp:useBean id="groupsPages" scope="request" class="java.lang.String"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div>
    <h1>Social network</h1>
    <h2>Account/group search</h2>
    <c:choose>
        <c:when test="${fn:length(requestScope.accounts)==0}">
            <h4>Accounts not found by substring '${searchString}'</h4>
        </c:when>
        <c:otherwise>
            <table>
                <caption><h4>Accounts found by substring '${searchString}': ${accountsTotal},
                    page ${pageContext.request.getParameter("page")} of ${accountsPages}</h4></caption>
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>E-mail</th>
                </tr>
                <c:forEach items="${requestScope.accounts}" var="account">
                    <tr>
                        <td><a href="account?id=${account.id}">${account.id}</a></td>
                        <td><a href="account?id=${account.id}">${account.lastName} ${account.firstName}</a></td>
                        <td><a href="account?id=${account.id}">${account.email}</a></td>
                    </tr>
                </c:forEach>
            </table>
            <br>
            <form action="search" method="post" name='accountsForm'>
                <a href='search?page=1&searchString=${searchString}'>Page 1 ||</a>
                <c:choose>
                    <c:when test="${pageContext.request.getParameter('page')==1}">
                        <a href='search' class="disabled">Previous ||</a>
                    </c:when>
                    <c:otherwise>
                        <a href='search?page=${(pageContext.request.getParameter('page')-1).toString()}&searchString=${searchString}'>
                            Previous ||</a>
                    </c:otherwise>
                </c:choose>
                <label for="num"></label>
                <input type="number" id="num" name="page" style="width: 50px"
                       oninput='document.forms["accountsForm"].submit(); return false'
                       value=${pageContext.request.getParameter("page")} min="1" max=${accountsPages}>
                <input type="hidden" name="searchString" value=${searchString}>
                <c:choose>
                    <c:when test="${pageContext.request.getParameter('page').toString().equals(accountsPages)}">
                        <a href='search' class="disabled">Next ||</a>
                    </c:when>
                    <c:otherwise>
                        <a href='search?page=${(pageContext.request.getParameter('page')+1).toString()}&searchString=${searchString}'>
                            Next ||</a>
                    </c:otherwise>
                </c:choose>
                <a href='search?page=${accountsPages}&searchString=${searchString}'>Page ${accountsPages}</a>
            </form>
        </c:otherwise>
    </c:choose>

    <br><br>
    <c:choose>
        <c:when test="${fn:length(requestScope.groups)==0}">
            <h4>Groups not found by substring '${searchString}'</h4>
        </c:when>
        <c:otherwise>
            <table>
                <caption><h4>Groups found by substring '${searchString}': ${groupsTotal},
                    page ${pageContext.request.getParameter("page")} of ${groupsPages}</h4></caption>
                <tr>
                    <th>Id</th>
                    <th>Title</th>
                    <th>About</th>
                </tr>
                <c:forEach items="${requestScope.groups}" var="group">
                    <tr>
                        <td><a href="group?id=${group.id}">${group.id}</a></td>
                        <td><a href="group?id=${group.id}">${group.title}</a></td>
                        <td><a href="group?id=${group.id}">${group.metaTitle}</a></td>
                    </tr>
                </c:forEach>
            </table>
            <br>
            <form action="search" method="post" name='groupsForm'>
                <a href='search?page=1&searchString=${searchString}'>Page 1 ||</a>
                <c:choose>
                    <c:when test="${pageContext.request.getParameter('page')==1}">
                        <a href='search' class="disabled">Previous ||</a>
                    </c:when>
                    <c:otherwise>
                        <a href='search?page=${(pageContext.request.getParameter('page')-1).toString()}&searchString=${searchString}'>
                            Previous ||</a>
                    </c:otherwise>
                </c:choose>
                <label for="numGr"></label>
                <input type="number" id="numGr" name="page" style="width: 50px"
                       oninput='document.forms["groupsForm"].submit(); return false'
                       value=${pageContext.request.getParameter("page")} min="1" max=${groupsPages}>
                <input type="hidden" name="searchString" value=${searchString}>
                <c:choose>
                    <c:when test="${pageContext.request.getParameter('page').toString().equals(groupsPages)}">
                        <a href='search' class="disabled">Next ||</a>
                    </c:when>
                    <c:otherwise>
                        <a href='search?page=${(pageContext.request.getParameter('page')+1).toString()}&searchString=${searchString}'>
                            Next ||</a>
                    </c:otherwise>
                </c:choose>
                <a href='search?page=${groupsPages}&searchString=${searchString}'>Page ${groupsPages}</a>
            </form>
        </c:otherwise>
    </c:choose>
    <br>
    <c:if test="${StringUtils.isEmpty(username)}">
        <a class="link" href="login">Login</a><br>
    </c:if>
</div>
</body>
</html>
