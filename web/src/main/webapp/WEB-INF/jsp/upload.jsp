<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="path" scope="request" class="java.lang.String"/>
<jsp:useBean id="option" scope="request" class="java.lang.String"/>
<jsp:useBean id="id" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Upload</title>
    <link rel="stylesheet" type="text/css" href="${root}/css/upload.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Upload image</h3>
    <form action="${root}/upload" method="post" enctype="multipart/form-data" onSubmit="return Validate();">
        <p class="error" id="error">${error}</p>
        <div class="input-group input-group-sm mb-3">
            <input type="text" class="form-control" name="description" placeholder="file size up to 64 kb"
                   aria-label=""/>
            <input type="file" accept="image/*" class="form-control" name="file" id="image"/>
            <button class="btn btn-outline-info btn-sm" type="submit" name="submit" id="btn_send">Submit</button>
            <input type="hidden" name="option" value="${option}">
            <input type="hidden" name="path" value="${path}">
            <input type="hidden" name="id" value="${id}">
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function Validate() {
        const image = document.getElementById("image").value;
        if (image !== '') {
            const check_img = image.toLowerCase();
            if (!check_img.match(/(\.jpg|\.png|\.jpeg|\.JPG|\.PNG|\.JPEG)$/)) {
                document.getElementById("error").innerText = "Please choose image file (jpg, png, jpeg)";
                document.getElementById("image").focus();
                return false;
            }
        }
        return true;
    }
</script>
</body>
</html>
