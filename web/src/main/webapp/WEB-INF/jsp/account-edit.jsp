<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="org.apache.commons.text.CaseUtils" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="homeAddress" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Address"/>
<jsp:useBean id="workAddress" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Address"/>
<jsp:useBean id="gender" scope="request" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Account edit</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${root}/css/account-edit.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account edit</h3>
    <form id="acc_edit_form" action="${root}/account/${account.id}/edit" method="post" novalidate>
        <p class="error" id="error">${error}</p>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="lastNameLabel">Last name *</span>
            <input type="text" class="form-control" name="lastName" id="lastName" placeholder="Required" required
                   aria-label="Last name" value="${account.lastName}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="firstNameLabel">First name *</span>
            <input type="text" class="form-control" name="firstName" id="firstName" placeholder="Required" required
                   aria-label="First name" value="${account.firstName}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="middleNameLabel">Middle name</span>
            <input type="text" class="form-control" name="middleName" id="middleName"
                   aria-label="Middle name" value="${account.middleName}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="usernameLabel">Username *</span>
            <input type="text" class="form-control" name="userName" id="userName" placeholder="Required" required
                   aria-label="Username" value="${account.userName}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="newPhoneLabel">New phone</span>
            <input type="text" class="form-control" name="newPhone" id="newPhone" placeholder="+1234567890"
                   aria-label="Input new phone number">
            <select class="form-select" id="newPhoneType" aria-label="Choose phone type">
                <option value="Personal">Personal</option>
                <option value="Work">Work</option>
            </select>
            <button class="btn btn-outline-info" type="button" id="addPhone">Add</button>
        </div>

        <div class="input-group input-group-sm mb-3">
            <table id="phoneTable" class="table table-hover">
                <tbody id="tbodyContainer">
                <c:forEach items="${account.phones}" var="phone" varStatus="loop">
                    <tr>
                        <td id="colPhoneType">${CaseUtils.toCamelCase(phone.phoneType.toString(), true, ' ')} phone</td>
                        <td id="colPhoneNum"><input type="tel" readonly name="phones[${loop.index}].number"
                                                    id="phoneNum" value="${phone.number}" aria-label=""></td>
                        <td><input type="hidden" name="phones[${loop.index}].phoneType" id="phoneType"
                                   value="${phone.phoneType}"></td>
                        <td><input type="hidden" name="phones[${loop.index}].id" id="phoneId" value="${phone.id}"></td>
                        <td id="colDelPhone">
                            <button class="btn btn-sm btn-outline-info" type="button" id="delPhone">x</button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="homeAddressLabel">Home address</span>
            <input type="text" class="form-control" name="addresses[0].addr" id="homeAddress"
                   value="${homeAddress.addr}" aria-label="Home address">
            <input type="hidden" name="addresses[0].addrType" id="homeAddressType" value="${homeAddress.addrType}">
            <input type="hidden" name="addresses[0].id" id="homeAddressId" value="${homeAddress.id}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="workAddressLabel">Work address</span>
            <input type="text" class="form-control" name="addresses[1].addr" id="workAddress"
                   value="${workAddress.addr}" aria-label="Work address">
            <input type="hidden" name="addresses[1].addrType" id="workAddressType" value="${workAddress.addrType}">
            <input type="hidden" name="addresses[1].id" id="workAddressId" value="${workAddress.id}">
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="addInfoLabel">About</span>
            <textarea class="form-control" id="addInfo" name="addInfo" rows="2"
                      aria-label="About">${account.addInfo}</textarea>
        </div>
        <div class="input-group input-group-sm mb-3">
            <span class="input-group-text" id="dateOfBirthLabel">Date of birth *</span>
            <input type="date" class="form-control" name="dateOfBirth" id="dateOfBirth" required
                   aria-label="Date of birth" value="${account.dateOfBirth}">
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
        <input type="hidden" name="id" value="${account.id}">
        <input type="hidden" name="email" value="${account.email}">

        <div class="btn-group d-block mx-auto">
            <button type="button" class="btn btn-outline-info" data-bs-toggle="modal" data-bs-target="#modal"
                    name="saveBtn" id="saveBtn" value="Save">Save
            </button>
            <button type="submit" class="btn btn-outline-info" name="submit" id="cancel" value="Cancel">Cancel</button>
        </div>
        <div class="modal" id="modal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Save changes?</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-outline-info" name="submit" id="save" value="Save">Save
                        </button>
                        <button type="button" class="btn btn-outline-info" id="close" data-bs-dismiss="modal">Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value="/js/accountEdit.js"/>"></script>
</body>
</html>
