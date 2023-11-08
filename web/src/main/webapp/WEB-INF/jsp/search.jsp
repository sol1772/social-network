<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>
<c:set var="requestPage" value="${pageContext.request.getParameter('page')}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Search</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/search.css"/>
    <link id="contextPathHolder" data-contextPath="${root}"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account / group search</h3>
    <p class="error" id="error">${error}</p>
    <input type="hidden" name="q" value="${q}">
    <input type="hidden" id="accountsTotal" name="accountsTotal" value="${accountsTotal}">
    <input type="hidden" id="groupsTotal" name="groupsTotal" value="${groupsTotal}">
    <input type="hidden" id="currentPage" name="currentPage" value="${param.currentPage}"/>
    <table class="table table-sm table-bordered table-hover">
        <caption><h5 id="tblAccCaption"></h5></caption>
        <thead>
        <tr>
            <th scope="col">Id</th>
            <th scope="col">Name</th>
            <th scope="col">E-mail</th>
        </tr>
        </thead>
        <tbody id="accountData"></tbody>
    </table>
    <nav aria-label="...">
        <ul class="pagination pagination-sm justify-content-center">
            <li class="page-item">
                <button class="page-link" id="firstPageA" value="1" onclick="pageA(this)">1</button>
            </li>
            <li class="page-item">
                <button class="page-link" id="prevPageA" onclick="prevPage()">Previous</button>
            </li>
            <input type="number" id="pageA" name="pageA" aria-label="" onchange="pageA(this)"
                   value="${requestPage}" min="1" max=${accountPages}>
            <li class="page-item">
                <button class="page-link" id="nextPageA" onclick="nextPageA()">Next</button>
            </li>
            <li class="page-item">
                <button class="page-link" id="lastPageA" value="${accountPages}"
                        onclick="pageA(this)">${accountPages}</button>
            </li>
        </ul>
    </nav>
    <br>

    <table class="table table-sm table-bordered table-hover">
        <caption><h5 id="tblGrpCaption"></h5></caption>
        <thead>
        <tr>
            <th scope="col">Id</th>
            <th scope="col">Title</th>
            <th scope="col">About</th>
        </tr>
        </thead>
        <tbody id="groupData"></tbody>
    </table>
    <nav aria-label="...">
        <ul class="pagination pagination-sm justify-content-center">
            <li class="page-item">
                <button class="page-link" id="firstPageG" value="1" onclick="pageG(this)">1</button>
            </li>
            <li class="page-item">
                <button class="page-link" id="prevPageG" onclick="prevPage()">Previous</button>
            </li>
            <input type="number" id="pageG" name="pageA" aria-label="" onchange="pageG(this)"
                   value="${requestPage}" min="1" max=${groupPages}>
            <li class="page-item">
                <button class="page-link" id="nextPageG" onclick="nextPageG()">Next</button>
            </li>
            <li class="page-item">
                <button class="page-link" id="lastPageG" value="${groupPages}"
                        onclick="pageG(this)">${groupPages}</button>
            </li>
        </ul>
    </nav>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/search.js"/>"></script>
</body>
</html>
