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
    <h3 style="color: darkgreen">XML Loader</h3>
    <form action="${root}/account/${id}/fromXml" method="post" enctype="multipart/form-data"
          onSubmit="return Validate();">
        <p class="error" id="error">${error}</p>
        <div class="input-group input-group-sm mb-3">
            <input type="text" class="form-control" name="description" placeholder="choose xml-file" aria-label=""/>
            <input type="file" accept=".xml,.json" class="form-control" name="file" id="xmlFile"/>
            <button class="btn btn-outline-info btn-sm" type="submit" name="submit" id="btn_loadXml">Load from XML
            </button>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script>
    function Validate() {
        const xmlFile = document.getElementById("xmlFile").value;
        if (xmlFile !== '') {
            const check_img = xmlFile.toLowerCase();
            if (!check_img.match(/(\.xml|\.XML|\.json|\.JSON)$/)) {
                document.getElementById("error").innerText = "Please choose xml/json-file";
                document.getElementById("xmlFile").focus();
                return false;
            }
        }
        return true;
    }
</script>
</body>
</html>
