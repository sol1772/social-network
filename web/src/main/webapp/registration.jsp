<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="firstName" scope="request" class="java.lang.String"/>
<jsp:useBean id="lastName" scope="request" class="java.lang.String"/>
<jsp:useBean id="middleName" scope="request" class="java.lang.String"/>
<jsp:useBean id="username" scope="request" class="java.lang.String"/>
<jsp:useBean id="email" scope="request" class="java.lang.String"/>
<jsp:useBean id="addInfo" scope="request" class="java.lang.String"/>
<jsp:useBean id="dateOfBirth" scope="request" class="java.lang.String"/>
<jsp:useBean id="gender" scope="request" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Account registration</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/registration.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div>
    <h1>Social network</h1>
    <h2>Account registration</h2>
    <form name="reg_form" action="registration" method="post">
        <div class="field">
            <input type="text" name="firstName" id="firstName" placeholder="Required" value=${firstName}>
            <label for="firstName">First name *</label>
        </div>
        <div class="field">
            <input type="text" name="lastName" id="lastName" placeholder="Required" value=${lastName}>
            <label for="lastName">Last name *</label><br>
        </div>
        <div class="field">
            <input type="text" name="middleName" id="middleName" value=${middleName}>
            <label for="middleName">Middle name</label><br>
        </div>
        <div class="field">
            <input type="text" name="username" id="username" placeholder="Required" value=${username}>
            <label for="username">Username *</label><br>
        </div>
        <div class="field">
            <input type="text" name="email" id="email" placeholder="Required" value=${email}>
            <label for="email">E-mail *</label><br>
        </div>
        <div class="field">
            <input type="password" name="password" id="password" placeholder="Required">
            <label for="password">Password *</label><br>
        </div>
        <div class="field">
            <input type="text" name="personalPhone" id="personalPhone">
            <label for="personalPhone">Home phone</label><br>
        </div>
        <div class="field">
            <input type="text" name="workPhone" id="workPhone"><label for="workPhone">Work phone</label><br>
        </div>
        <div class="field">
            <input type="text" name="homeAddress" id="homeAddress"><label for="homeAddress">Home address</label><br>
        </div>
        <div class="field">
            <input type="text" name="workAddress" id="workAddress"><label for="workAddress">Work address</label><br>
        </div>
        <div class="field">
            <textarea rows="2" cols="40" name="addInfo" id="addInfo">${addInfo}</textarea>
            <label for="addInfo">About</label><br>
        </div>
        <div class="field">
            <input type="date" name="dateOfBirth" id="dateOfBirth" value=${dateOfBirth}>
            <label for="dateOfBirth">Date of birth *</label><br>
        </div>
        <div class="field">
            <label>Gender *</label>
            <input type="radio" name="gender" id="genderF" value="F"
                   <c:if test="${gender=='F'}">checked</c:if>/>Female
            <input type="radio" name="gender" id="genderM" value="M"
                   <c:if test="${gender=='M'}">checked</c:if>/>Male
            <br>
        </div>
        <div class="field" style="text-align: center">
            <input type="submit" value="Register" class="button_ok" name="submit"/>
            <input type="submit" value="Cancel" class="button_ok" name="submit"/>
        </div>
        <c:if test="${violations != null}">
            <c:forEach items="${violations}" var="violation">
                <p style="color: red">${violation}</p>
            </c:forEach>
        </c:if>
    </form>
</div>
</body>
</html>
