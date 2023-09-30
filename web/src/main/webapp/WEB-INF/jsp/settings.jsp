<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="account" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<c:set var="root" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Settings</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/settings.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <h2 style="color: darkgreen">Social network</h2>
    <h3 style="color: darkgreen">Account settings</h3>
    <p style="color: red" class="error" id="error">${error}</p>
    <p style="color: darkgreen" id="message">${requestScope.message}</p>
    <c:if test="${username.equals(account.userName) || 'ADMIN'.equals(sessionScope.account.role.toString())}">
        <form id="set_form" action="${root}/account/${account.id}/settings" method="post" novalidate>
            <fieldset class="border rounded-3 p-3">
                <legend class="float-none w-auto px-3"><h5>Change password</h5></legend>
                <div class="input-group input-group-sm mb-3">
                    <span class="input-group-text" id="oldPasswordLabel">Old password</span>
                    <input type="text" class="form-control" name="oldPassword" id="oldPassword"
                           aria-label="Old password">
                </div>
                <div class="input-group input-group-sm mb-3">
                    <span class="input-group-text" id="newPasswordLabel">New password *</span>
                    <input type="text" class="form-control" name="newPassword" id="newPassword"
                           placeholder="Required" required aria-label="New password">
                </div>
                <div class="btn-group d-block mx-auto">
                    <button class="btn btn-outline-info btn-sm" name="submit" id="change" value="Change">Change</button>
                    <button class="btn btn-outline-info btn-sm" name="submit" id="cancel" value="Cancel">Cancel</button>
                </div>
            </fieldset>
        </form>
        <br>
        <form id="storage_form" action="${root}/account/${account.id}/fromFile" method="get">
            <fieldset class="border rounded-3 p-3">
                <legend class="float-none w-auto px-3"><h5>Save/Download account from file</h5></legend>
                <div class="btn-group d-block mx-auto">
                    <a href="${root}/account/${account.id}/toXml" class="btn btn-outline-info btn-sm">To XML</a>
                    <a href="${root}/account/${account.id}/toJson" class="btn btn-outline-info btn-sm">To JSON</a>
                    <button class="btn btn-outline-info btn-sm" id="loadXml">From XML/JSON</button>
                </div>
            </fieldset>
        </form>
        <br>
        <form id="del_form" action="${root}/account/${account.id}/delete" method="post">
            <fieldset class="border rounded-3 p-3">
                <legend class="float-none w-auto px-3"><h5>Delete account</h5></legend>
                <div class="alert alert-danger" role="alert">
                    Attention!<br>Once an account is deleted, it cannot be restored!
                </div>
                <div class="btn-group d-block mx-auto">
                    <button type="button" class="btn btn-outline-info btn-sm" data-bs-toggle="modal"
                            data-bs-target="#modal" name="del_account" id="del_account" value="Delete">Delete
                    </button>
                    <button type="submit" class="btn btn-outline-info btn-sm" name="submit" id="cancel2" value="Cancel">
                        Cancel
                    </button>
                </div>
                <div class="modal" id="modal" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Account '${account}' will be deleted. Are you sure?</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Close"></button>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-outline-info" name="submit" id="delete"
                                        value="Delete">Delete
                                </button>
                                <button type="button" class="btn btn-outline-info" id="close" data-bs-dismiss="modal">
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </c:if>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
