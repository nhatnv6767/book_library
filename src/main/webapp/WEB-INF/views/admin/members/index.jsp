<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 22:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../shared/layout.jsp">
    <jsp:param name="title" value="Member Management"/>
    <jsp:param name="content" value="../admin/members/index-content.jsp"/>
    <jsp:param name="active" value="members"/>
</jsp:include>
