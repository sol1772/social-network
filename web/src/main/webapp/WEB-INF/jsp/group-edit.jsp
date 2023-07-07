<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="title" scope="request" class="java.lang.String"/>
<jsp:useBean id="metaTitle" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<jsp:useBean id="group" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Group"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/group-edit.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Group edit</h3>
    <form id="group_edit_form" action="group-edit" method="post">
        <p class="error" id="error">${error}</p>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="titleLabel">Title (readonly)</span>
            <input type="text" class="form-control" name="title" id="title" placeholder="Required" required
                   aria-label="Title" value="${group.title}" readonly>
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="metaTitleLabel">About</span>
            <textarea class="form-control" id="metaTitle" name="metaTitle" rows="2"
                      aria-label="About">${group.metaTitle}</textarea>
        </div>
        <div class="btn-group d-block mx-auto">
            <button class="btn btn-outline-info" name="submit" id="save" value="Save">Save</button>
            <button class="btn btn-outline-info" name="submit" id="cancel" value="Cancel">Cancel</button>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
