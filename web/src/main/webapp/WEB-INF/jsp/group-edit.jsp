<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="title" scope="request" class="java.lang.String"/>
<jsp:useBean id="metaTitle" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<jsp:useBean id="group" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Group"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/group-edit.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div>
    <h1>Social network</h1>
    <h2>Group edit</h2>
    <form name="group-edit_form" action="group-edit" method="post">
        <div class="field">
            <input type="text" name="title" id="title" placeholder="Required" value="${group.title}" readonly>
            <label for="title">Title (readonly)</label>
        </div>
        <div class="field">
            <textarea rows="2" cols="40" name="metaTitle" id="metaTitle">${group.metaTitle}</textarea>
            <label for="metaTitle">About</label><br>
        </div>
        <div class="field" style="text-align: center">
            <input type="submit" value="Save" class="button_ok" name="submit"/>
            <input type="submit" value="Cancel" class="button_ok" name="submit"/>
        </div>
        <p class="error" style="color: red">
            ${error}
        </p>
    </form>
</div>
</body>
</html>
