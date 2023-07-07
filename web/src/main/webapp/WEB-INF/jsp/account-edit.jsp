<%@ page contentType="text/html;charset=UTF-8" %>
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
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/account-edit.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account edit</h3>
    <form id="acc_edit_form" action="account-edit" method="post" novalidate>
        <p class="error" id="error">${error}</p>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="lastNameLabel">Last name *</span>
            <input type="text" class="form-control" name="lastName" id="lastName" placeholder="Required" required
                   aria-label="Last name" value=${account.lastName}>
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="firstNameLabel">First name *</span>
            <input type="text" class="form-control" name="firstName" id="firstName" placeholder="Required" required
                   aria-label="First name" value=${account.firstName}>
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="middleNameLabel">Middle name</span>
            <input type="text" class="form-control" name="middleName" id="middleName"
                   aria-label="Middle name" value=${account.middleName}>
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="usernameLabel">Username *</span>
            <input type="text" class="form-control" name="username" id="username" placeholder="Required" required
                   aria-label="Username" value=${account.userName}>
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="newPhoneLabel">New phone</span>
            <input type="text" class="form-control" name="newPhone" id="newPhone" placeholder="+1234567890"
                   aria-label="Ввод нового телефона">
            <select class="form-select" id="newPhoneType" aria-label="Выбор типа телефона">
                <option value="Personal">Personal</option>
                <option value="Work">Work</option>
            </select>
            <button class="btn btn-outline-info" type="button" id="addPhone">Add</button>
        </div>

        <div class="input-group input-group-sm mb-3">
            <table id="phoneTable" class="table table-hover">
                <c:forEach items="${account.phones}" var="phone">
                    <tr>
                        <td id="colPhoneType">${CaseUtils.toCamelCase(phone.phoneType.toString(), true, ' ')} phone</td>
                        <td id="colPhoneNum"><input type="tel" readonly name="phoneNum" id="phoneNum"
                                                    value='${phone.number}' aria-label=""></td>
                        <td><input type="hidden" name="phoneId" id="phoneId" value=${phone.id}></td>
                        <td><input type="hidden" name="phoneType" id="phoneType" value=${phone.phoneType}></td>
                        <td>
                            <button class="btn btn-sm btn-outline-info" type="button" id="delPhone">x</button>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="homeAddressLabel">Home address</span>
            <input type="text" class="form-control" name="homeAddress" id="homeAddress"
                   aria-label="Home address">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="workAddressLabel">Work address</span>
            <input type="text" class="form-control" name=workAddress"" id="workAddress"
                   aria-label="Work address">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="addInfoLabel">About</span>
            <textarea class="form-control" id="addInfo" name="addInfo" rows="2"
                      aria-label="About">${account.addInfo}</textarea>
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="dateOfBirthLabel">Date of birth *</span>
            <input type="date" class="form-control" name="dateOfBirth" id="dateOfBirth" required
                   aria-label="Date of birth" value=${account.dateOfBirth}>
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
            <button class="btn btn-outline-info" name="submit" id="save" value="Save">Save</button>
            <button class="btn btn-outline-info" name="submit" id="cancel" value="Cancel">Cancel</button>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/accountEdit.js"/>"></script>
</body>
</html>
