<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 13:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../shared/layout.jsp">
    <jsp:param name="title" value="${book.bookId == null ? 'Add New Book' : 'Edit Book'}"/>
    <jsp:param name="content" value="/WEB-INF/views/admin/books/form-content.jsp"/>
    <jsp:param name="active" value="books"/>
</jsp:include>
