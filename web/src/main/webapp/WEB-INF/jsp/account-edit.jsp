<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.apache.commons.text.CaseUtils" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="gender" scope="request" class="java.lang.String"/>
<jsp:useBean id="homeAddress" scope="request" class="java.lang.String"/>
<jsp:useBean id="workAddress" scope="request" class="java.lang.String"/>
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
        <p id="error" style="color: red; text-align: center">${error}</p>
        <div class="field">
            <label for="lastName">Last name *</label>
            <input type="text" name="lastName" required id="lastName" value=${account.lastName}>
        </div>
        <div class="field">
            <label for="firstName">First name *</label>
            <input type="text" name="firstName" required id="firstName" value=${account.firstName}>
        </div>
        <div class="field">
            <label for="middleName">Middle name</label>
            <input type="text" name="middleName" id="middleName" value=${account.middleName}>
        </div>
        <div class="field">
            <label for="username">Username *</label>
            <input type="text" name="username" required id="username" value=${account.userName}>
        </div>
        <div class="field">
            <label for="newPhone">New phone</label>
            <input type="text" name="newPhone" id="newPhone" placeholder="+1234567890">
            <select id="newPhoneType">
                <option>Personal</option>
                <option>Work</option>
            </select>
            <input type="button" id="addPhone" value="Add">
            <table id="phoneTable" value="${account.phones}">
                <c:forEach items="${account.phones}" var="phone">
                    <tr>
                        <td id="colPhoneType">${CaseUtils.toCamelCase(phone.phoneType.toString(), true, ' ')} phone</td>
                        <td id="colPhoneNum"><input type="tel" readonly name="phoneNum" id="phoneNum"
                                                    value=${phone.number}></td>
                        <td><input type="button" id="delPhone" value="x"></td>
                        <td><input type="hidden" name="phoneId" id="phoneId" value=${phone.id}></td>
                        <td><input type="hidden" name="phoneType" id="phoneType" value=${phone.phoneType}></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <div class="field">
            <label for="homeAddress">Home address</label>
            <input type="text" name="homeAddress" id="homeAddress" value="${homeAddress}">
            <input type="hidden" name="homeAddressId" id="homeAddressId" value=${requestScope.homeAddressId}>
        </div>
        <div class="field">
            <label for="workAddress">Work address</label>
            <input type="text" name="workAddress" id="workAddress" value="${workAddress}">
            <input type="hidden" name="workAddressId" id="workAddressId" value=${requestScope.workAddressId}>
        </div>
        <div class="field">
            <label for="addInfo">About</label>
            <textarea rows="2" cols="40" name="addInfo" id="addInfo">${account.addInfo}</textarea>
        </div>
        <div class="field">
            <label for="dateOfBirth">Date of birth *</label>
            <input type="date" name="dateOfBirth" required id="dateOfBirth" value=${account.dateOfBirth}>
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
            <input type="submit" name="submit" class="button_ok" id="save" value="Save"/>
            <input type="submit" name="submit" class="button_ok" id="cancel" value="Cancel"/>
        </div>
    </form>
</div>
<script src="<c:url value="/js/accountEdit.js"/>"></script>
</body>
</html>
