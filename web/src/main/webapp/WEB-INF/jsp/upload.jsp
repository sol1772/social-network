<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="path" scope="request" class="java.lang.String"/>
<jsp:useBean id="change_delete" scope="request" class="java.lang.String"/>
<jsp:useBean id="id" scope="request" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Upload</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/default.css"/>
</head>
<body>
<div>
    <h1>Social network</h1>
    <h2>Upload image</h2>
    <form action="upload" method="post" enctype="multipart/form-data">
        <input type="text" name="description" placeholder="file size up to 65 kb"/>
        <input type="file" name="file"/>
        <p><input type="submit"/></p>
        <input type="hidden" name="change_delete" value=${change_delete}>
        <input type="hidden" name="path" value=${path}>
        <input type="hidden" name="id" value=${id}>
    </form>
</div>
</body>
</html>
