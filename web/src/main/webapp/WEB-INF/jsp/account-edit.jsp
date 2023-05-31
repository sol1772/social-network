<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="firstName" scope="request" class="java.lang.String"/>
<jsp:useBean id="lastName" scope="request" class="java.lang.String"/>
<jsp:useBean id="middleName" scope="request" class="java.lang.String"/>
<jsp:useBean id="username" scope="request" class="java.lang.String"/>
<jsp:useBean id="addInfo" scope="request" class="java.lang.String"/>
<jsp:useBean id="dateOfBirth" scope="request" class="java.lang.String"/>
<jsp:useBean id="gender" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Account edit</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/account-edit.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div>
    <h1>Social network</h1>
    <h2>Account edit</h2>
    <form action="account-edit" method="post">
        <div class="field">
            <label for="firstName">First name *</label>
            <input type="text" name="firstName" required id="firstName" value=${firstName}>
        </div>
        <div class="field">
            <label for="lastName">Last name *</label><br>
            <input type="text" name="lastName" required id="lastName" value=${lastName}>
        </div>
        <div class="field">
            <label for="middleName">Middle name</label><br>
            <input type="text" name="middleName" id="middleName" value=${middleName}>
        </div>
        <div class="field">
            <label for="username">Username *</label><br>
            <input type="text" name="username" required id="username" value=${username}>
        </div>
        <div class="field">
            <label for="addInfo">About</label><br>
            <textarea rows="2" cols="40" name="addInfo" id="addInfo">${addInfo}</textarea>
        </div>
        <div class="field">
            <label for="dateOfBirth">Date of birth *</label><br>
            <input type="date" name="dateOfBirth" required id="dateOfBirth" value=${dateOfBirth}>
        </div>
        <div class="field">
            <label>Gender *</label>
            <input type="radio" name="gender" id="genderF" value="F"
                   <c:if test="${gender=='F'}">checked</c:if>/>
            <label for="genderF">Female</label>
            <input type="radio" name="gender" id="genderM" value="M"
                   <c:if test="${gender=='M'}">checked</c:if>/>
            <label for="genderM">Male</label>
            <br>
        </div>
        <div class="field" style="text-align: center">
            <input type="submit" name="submit" class="button_ok" value="Save"/>
            <input type="submit" name="submit" class="button_ok" value="Cancel"/>
        </div>
        <p class="error" style="color: red">
            ${error}
        </p>
    </form>
</div>
</body>
</html>
