<%@ page contentType="text/html;charset=UTF-8" %>
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
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Upload image</h3>
    <form action="upload" method="post" enctype="multipart/form-data">
        <input type="text" name="description" placeholder="file size up to 65 kb" aria-label=""/>
        <input type="file" name="file"/>
        <input type="submit"/>
        <input type="hidden" name="change_delete" value="${change_delete}">
        <input type="hidden" name="path" value="${path}">
        <input type="hidden" name="id" value="${id}">
    </form>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
