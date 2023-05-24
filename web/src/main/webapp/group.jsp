<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.apache.commons.text.CaseUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<jsp:useBean id="group" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Group"/>
<jsp:useBean id="owner" scope="request" class="com.getjavajob.training.maksyutovs.socialnetwork.domain.Account"/>
<jsp:useBean id="username" scope="session" class="java.lang.String"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Group</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/group.css"/>
</head>
<body>
<div class="wrapper">
    <jsp:include page="header.jsp"/>
    <br><br>
    <h1>Social network</h1>
    <h2>Group info</h2>
    <div class="head">
        <div class="head-cont">
            <h3>
                <p><i>Title: </i>${group.title}</p>
                <p><i>About: </i>${StringUtils.isEmpty(group.metaTitle)?"---" : group.metaTitle}</p>
                <p><i>Owner: </i>${owner.firstName} ${owner.lastName}</p>
                <fmt:parseDate value="${group.createdAt}" pattern="yyyy-MM-dd" var="createdAt" type="date"/>
                <p><i>Date of creation: </i><fmt:formatDate pattern="dd.MM.yyyy" value="${createdAt}"/></p>
                <c:if test="${!group.members.isEmpty()}">
                    <p><i>Members:</i></p>
                    <c:forEach items="${group.members}" var="member">
                        <p>${member.account.firstName} ${member.account.lastName}
                            (${CaseUtils.toCamelCase(member.role.toString(), true, ' ')})</p><br>
                    </c:forEach>
                </c:if>
            </h3>
        </div>
        <div class="head-logo">
            <img src="<c:url value="upload?command=group&id=${group.id}"/>" alt="Group image"/>
            <c:if test="${username.equals(owner.userName)}">
                <form action="upload" method="get">
                    <p><input type="submit" name="submit" value="Change"/>
                        <input type="submit" name="submit" onclick="return confirm('Delete?')" value="Delete"/></p>
                    <script>
                        function confirmDelete() {
                            if (confirm("Delete?")) {
                                return true;
                            }
                        }
                    </script>
                    <p style="color: darkgreen">${requestScope.message}</p>
                    <input type="hidden" name="path" value="/group.jsp">
                    <input type="hidden" name="id" value=${group.id}>
                </form>
            </c:if>
        </div>
    </div>
    <c:if test="${username.equals(owner.userName)}">
        <a class="link" href="group-edit?id=${group.id}"> Edit</a><br>
    </c:if>
</div>
</body>
</html>
