<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="title" scope="request" class="java.lang.String"/>
<jsp:useBean id="metaTitle" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Group creation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/group-add.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">New group</h3>
    <form id="group_add_form" action="${root}/group/add" method="post" novalidate>
        <p class="error" id="error">${error}</p>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="titleLabel">Title *</span>
            <input type="text" class="form-control" name="title" id="title" placeholder="Required" required
                   aria-label="Title" value="${title}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="metaTitleLabel">About</span>
            <textarea class="form-control" id="metaTitle" name="metaTitle" rows="2"
                      aria-label="About">${metaTitle}</textarea>
        </div>
        <div class="btn-group d-block mx-auto">
            <button class="btn btn-outline-info" name="submit" id="create" value="Create">Create</button>
            <button class="btn btn-outline-info" name="submit" id="cancel" value="Cancel">Cancel</button>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
