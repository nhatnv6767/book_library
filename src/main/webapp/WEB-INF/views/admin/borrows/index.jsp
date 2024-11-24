<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/24
  Time: 12:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../../shared/layout.jsp">
    <jsp:param name="title" value="Borrow Records"/>
    <jsp:param name="content" value="../admin/borrows/index-content.jsp"/>
    <jsp:param name="active" value="borrows"/>
</jsp:include>

