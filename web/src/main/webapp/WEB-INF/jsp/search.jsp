<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<jsp:useBean id="q" scope="request" class="java.lang.String"/>
<jsp:useBean id="accountsTotal" scope="request" class="java.lang.String"/>
<jsp:useBean id="accountsPages" scope="request" class="java.lang.String"/>
<jsp:useBean id="groupsTotal" scope="request" class="java.lang.String"/>
<jsp:useBean id="groupsPages" scope="request" class="java.lang.String"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search</title>
    <link rel="stylesheet" type="text/css" href="${root}/css/search.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account / group search</h3>
    <p class="error" id="error">${error}</p>
    <c:choose>
        <c:when test="${requestScope.accountsTotal==0}">
            <h5>Accounts not found by substring '${q}'</h5>
        </c:when>
        <c:otherwise>
            <table class="table table-sm table-bordered table-hover">
                <caption>
                    <h5>Accounts found by substring '${q}': ${accountsTotal},
                        <c:choose>
                            <c:when test="${fn:length(requestScope.accounts)==0}">
                                page ${pageContext.request.getParameter("page")} exceeds number of pages ${accountsPages}
                            </c:when>
                            <c:otherwise>
                                page ${pageContext.request.getParameter("page")} of ${accountsPages}
                            </c:otherwise>
                        </c:choose>
                    </h5>
                </caption>
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>E-mail</th>
                </tr>
                <c:forEach items="${requestScope.accounts}" var="account">
                    <tr>
                        <td><a href="${root}/account/${account.id}">${account.id}</a></td>
                        <td><a href="${root}/account/${account.id}">${account.lastName} ${account.firstName}</a></td>
                        <td><a href="${root}/account/${account.id}">${account.email}</a></td>
                    </tr>
                </c:forEach>
            </table>
            <form action="${root}/search" method="get" name='accountsForm'>
                <a href='${root}/search?q=${q}&page=1'>Page 1 ||</a>
                <c:choose>
                    <c:when test="${pageContext.request.getParameter('page')==1}">
                        <a href='${root}/search' class="disabled">Previous ||</a>
                    </c:when>
                    <c:otherwise>
                        <a href='${root}/search?q=${q}&page=${(pageContext.request.getParameter('page')-1).toString()}'>
                            Previous ||</a>
                    </c:otherwise>
                </c:choose>
                <input type="hidden" name="q" value="${q}">
                <label for="num"></label>
                <input type="number" id="num" name="page" style="width: 50px"
                       oninput="function submitForm() {
                               const value = document.getElementById('page').value;
                               return value <= ${accountsPages};
                               }
                               return submitForm()"
                       value="${pageContext.request.getParameter("page")}" min="1" max=${accountsPages}>
                <c:choose>
                    <c:when test="${pageContext.request.getParameter('page').toString().equals(accountsPages)}">
                        <a href='${root}/search' class="disabled">Next ||</a>
                    </c:when>
                    <c:otherwise>
                        <a href='${root}/search?q=${q}&page=${(pageContext.request.getParameter('page')+1).toString()}'>
                            Next ||</a>
                    </c:otherwise>
                </c:choose>
                <a href='${root}/search?q=${q}&page=${accountsPages}'>Page ${accountsPages}</a>
            </form>
        </c:otherwise>
    </c:choose>
    <br>
    <c:choose>
        <c:when test="${requestScope.groupsTotal==0}">
            <h5>Groups not found by substring '${q}'</h5>
        </c:when>
        <c:otherwise>
            <table class="table table-sm table-bordered table-hover">
                <caption>
                    <h5>Groups found by substring '${q}': ${groupsTotal},
                        <c:choose>
                            <c:when test="${fn:length(requestScope.groups)==0}">
                                page ${pageContext.request.getParameter("page")} exceeds number of pages ${groupsPages}
                            </c:when>
                            <c:otherwise>
                                page ${pageContext.request.getParameter("page")} of ${groupsPages}
                            </c:otherwise>
                        </c:choose>
                    </h5>
                </caption>
                <tr>
                    <th>Id</th>
                    <th>Title</th>
                    <th>About</th>
                </tr>
                <c:forEach items="${requestScope.groups}" var="group">
                    <tr>
                        <td><a href="${root}/group/${group.id}">${group.id}</a></td>
                        <td><a href="${root}/group/${group.id}">${group.title}</a></td>
                        <td><a href="${root}/group/${group.id}">${group.metaTitle}</a></td>
                    </tr>
                </c:forEach>
            </table>
            <form action="${root}/search" method="get" name='groupsForm'>
                <a href='${root}/search?q=${q}&page=1'>Page 1 ||</a>
                <c:choose>
                    <c:when test="${pageContext.request.getParameter('page')==1}">
                        <a href='${root}/search' class="disabled">Previous ||</a>
                    </c:when>
                    <c:otherwise>
                        <a href='${root}/search?q=${q}&page=${(pageContext.request.getParameter('page')-1).toString()}'>
                            Previous ||</a>
                    </c:otherwise>
                </c:choose>
                <input type="hidden" name="q" value="${q}">
                <label for="numGr"></label>
                <input type="number" id="numGr" name="page" style="width: 50px"
                       oninput="function submitForm() {
                               const value = document.getElementById('page').value;
                               return value <= ${groupsPages};
                               }
                               return submitForm()"
                       value="${pageContext.request.getParameter("page")}" min="1" max=${groupsPages}>
                <c:choose>
                    <c:when test="${pageContext.request.getParameter('page').toString().equals(groupsPages)}">
                        <a href='${root}/search' class="disabled">Next ||</a>
                    </c:when>
                    <c:otherwise>
                        <a href='${root}/search?q=${q}&page=${(pageContext.request.getParameter('page')+1).toString()}'>
                            Next ||</a>
                    </c:otherwise>
                </c:choose>
                <a href='${root}/search?q=${q}&page=${groupsPages}'>Page ${groupsPages}</a>
            </form>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
