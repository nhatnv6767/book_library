<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 23:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../shared/layout.jsp">
    <jsp:param name="title" value="${member.memberId == null ? 'Add New Member' : 'Edit Member'}"/>
    <jsp:param name="content" value="../admin/members/form-content.jsp"/>
    <jsp:param name="active" value="members"/>
</jsp:include>
