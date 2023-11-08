<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="id" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>XML Loader</title>
    <link rel="stylesheet" type="text/css" href="${root}/css/upload.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">File Loader</h3>
    <form action="${root}/account/${id}/fromFile" method="post" enctype="multipart/form-data"
          onSubmit="return Validate();">
        <p class="error" id="error">${error}</p>
        <div class="input-group input-group-sm mb-3">
            <input type="text" class="form-control" name="description" placeholder="choose file" aria-label=""/>
            <input type="file" accept=".xml,.json" class="form-control" name="file" id="file"/>
            <button class="btn btn-outline-info btn-sm" type="submit" name="submit" id="btn_fileLoad">Load</button>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function Validate() {
        const file = document.getElementById("file").value;
        if (file !== '') {
            const check_img = file.toLowerCase();
            if (!check_img.match(/(\.xml|\.XML|\.json|\.JSON)$/)) {
                document.getElementById("error").innerText = "Please choose xml/json file";
                document.getElementById("file").focus();
                return false;
            }
        }
        return true;
    }
</script>
</body>
</html>
