<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="io.github.dmnisson.labvision.entities.UserRole"
    %>

<t:userpage title="User ${fn:escapeXml(user.username)} created">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="alert alert-info confirm-page-alert text-center">
    <h1>Password reset link for user <c:out value="${user.username}" /></h1>
    <h2>
      Please send the following link to <c:out value="${user.username}" /> so that they can
      set a new password:
    </h2>
    <p class="lead wrap-break">
      <a href="${resetPasswordUrl}">${resetPasswordUrl}</a>
    </p>
    <p>
      <a class="btn btn-primary" href="${s:mvcUrl('AC#dashboard').build()}">Back To Dashboard</a>
    </p>
  </div>
</div>

</t:userpage>