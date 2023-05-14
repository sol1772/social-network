<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="title" scope="request" class="java.lang.String"/>
<jsp:useBean id="metaTitle" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Group creation</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/group-add.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div>
    <h1>Social network</h1>
    <h2>New group</h2>
    <form name="group-add_form" action="group-add" method="post">
        <div class="field">
            <input type="text" name="title" id="title" placeholder="Required" value=${title}>
            <label for="title">Title *</label>
        </div>
        <div class="field">
            <textarea rows="2" cols="40" name="metaTitle" id="metaTitle">${metaTitle}</textarea>
            <label for="metaTitle">About</label><br>
        </div>
        <div class="field" style="text-align: center">
            <input type="submit" value="Create" class="button_ok" name="submit"/>
            <input type="submit" value="Cancel" class="button_ok" name="submit"/>
        </div>
        <p class="error" style="color: red">
            ${error}
        </p>
    </form>
</div>
</body>
</html>
