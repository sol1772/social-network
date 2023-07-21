<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="firstName" scope="request" class="java.lang.String"/>
<jsp:useBean id="lastName" scope="request" class="java.lang.String"/>
<jsp:useBean id="middleName" scope="request" class="java.lang.String"/>
<jsp:useBean id="userName" scope="request" class="java.lang.String"/>
<jsp:useBean id="email" scope="request" class="java.lang.String"/>
<jsp:useBean id="personalPhone" scope="request" class="java.lang.String"/>
<jsp:useBean id="workPhone" scope="request" class="java.lang.String"/>
<jsp:useBean id="homeAddress" scope="request" class="java.lang.String"/>
<jsp:useBean id="workAddress" scope="request" class="java.lang.String"/>
<jsp:useBean id="addInfo" scope="request" class="java.lang.String"/>
<jsp:useBean id="dateOfBirth" scope="request" class="java.lang.String"/>
<jsp:useBean id="gender" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<jsp:useBean id="violations" scope="request" class="java.util.ArrayList"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Account registration</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/registration.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account registration</h3>
    <form id="reg_form" action="${root}/account/add" method="post" novalidate>
        <c:if test="${violations != null}">
            <c:forEach items="${violations}" var="violation">
                <p id="violation">${violation}</p>
            </c:forEach>
        </c:if>
        <p class="error" id="error">${error}</p>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="lastNameLabel">Last name *</span>
            <input type="text" class="form-control" name="lastName" id="lastName" placeholder="Required" required
                   aria-label="Last name" value="${lastName}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="firstNameLabel">First name *</span>
            <input type="text" class="form-control" name="firstName" id="firstName" placeholder="Required" required
                   aria-label="First name" value="${firstName}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="middleNameLabel">Middle name</span>
            <input type="text" class="form-control" name="middleName" id="middleName"
                   aria-label="Middle name" value="${middleName}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="usernameLabel">Username *</span>
            <input type="text" class="form-control" name="userName" id="userName" placeholder="Required" required
                   aria-label="Username" value="${userName}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="emailLabel">E-mail *</span>
            <input type="text" class="form-control" name="email" id="email" placeholder="Required" required
                   aria-label="E-mail" value="${email}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="passwordLabel">Password *</span>
            <input type="text" class="form-control" name="password" id="password" placeholder="Required" required
                   aria-label="Password">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="personalPhoneLabel">Home phone</span>
            <input type="text" class="form-control" name="personalPhone" id="personalPhone" aria-label="Home phone"
                   value="${personalPhone}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="workPhoneLabel">Work phone</span>
            <input type="text" class="form-control" name="workPhone" id=workPhone"" aria-label="Work phone"
                   value="${workPhone}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="homeAddressLabel">Home address</span>
            <input type="text" class="form-control" name="homeAddress" id="homeAddress" aria-label="Home address"
                   value="${homeAddress}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="workAddressLabel">Work address</span>
            <input type="text" class="form-control" name="workAddress" id="workAddress" aria-label="Work address"
                   value="${workAddress}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="addInfoLabel">About</span>
            <textarea class="form-control" id="addInfo" name="addInfo" rows="2"
                      aria-label="About">${addInfo}</textarea>
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="dateOfBirthLabel">Date of birth *</span>
            <input type="date" class="form-control" name="dateOfBirth" id="dateOfBirth" required
                   aria-label="Date of birth" value="${dateOfBirth}">
        </div>
        <div class="input-group input-group-sm mb-3" id="radio_group">
            <span class="input-group-text" id="genderLabel">Gender *</span>
            <div class="col-sm-7">
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="gender" id="genderF" value="F" required
                           <c:if test="${gender=='F'}">checked</c:if>/>
                    <label class="form-check-label" for="genderF">Female</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="gender" id="genderM" value="M" required
                           <c:if test="${gender=='M'}">checked</c:if>/>
                    <label class="form-check-label" for="genderM">Male</label>
                </div>
            </div>
        </div>
        <div class="btn-group d-block mx-auto">
            <button class="btn btn-outline-info" name="submit" id="register" value="Register">Register</button>
            <button class="btn btn-outline-info" name="submit" id="cancel" value="Cancel">Cancel</button>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/registration.js"/>"></script>
</body>
</html>
