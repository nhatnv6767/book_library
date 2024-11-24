<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../../shared/layout.jsp">
    <jsp:param name="title" value="New Borrow Record"/>
    <jsp:param name="content" value="/WEB-INF/views/admin/borrows/form-content.jsp"/>
    <jsp:param name="active" value="borrows"/>
</jsp:include>