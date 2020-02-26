<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="io.github.dmnisson.labvision.entities.UserRole"
    %>

<t:userpage title="User ${fn:escapeXml(user.username)}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>User <c:out value="${user.username}"></c:out>&nbsp;<a
          class="btn btn-primary" href="${s:mvcUrl('AC#editUser').arg(0, user.id).build()}">
          <i class="fas fa-user-edit"></i> Edit
        </a>&nbsp;<a
          class="btn btn-danger" href="${s:mvcUrl('AC#deleteUser').arg(0, user.id).build()}">
          <i class="fas fa-user-minus"></i> Delete
        </a>
        </h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h2>Type</h2>
    </div>
    <div class="col">
      <c:out value="${user.role}" />
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h2>Admin Access?</h2>
    </div>
    <div class="col">
      <c:if test="${admin}">Yes</c:if>
      <c:if test="${not admin}">No</c:if>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h2>Display Name</h2>
    </div>
    <div class="col">
      <c:out value="${user.displayName}" />
    </div>
  </div>
  <c:if test="${not admin}">
  
  <c:set var="STUDENT" value="<%= UserRole.STUDENT %>" />
  <c:set var="FACULTY" value="<%= UserRole.FACULTY %>" />
  <c:set var="ADMIN" value="<%= UserRole.ADMIN %>" />
  
  <c:if test="${user.role eq FACULTY}">
  <div class="row">
    <div class="col">
      <h2>Email Address</h2>
    </div>
    <div class="col">
      <c:out value="${user.email}" />
    </div>
  </div>
  </c:if>
  
  </c:if>
  <c:if test="${admin}">
  
  <c:if test="${not empty user.adminInfo.email}">
  <div class="row">
    <div class="col">
      <h3>Email Address</h3>
    </div>
    <div class="col">
      ${user.adminInfo.email}
    </div>
  </div>
  </c:if>
  <c:if test="${not empty user.adminInfo.phone}">
  <div class="row">
    <div class="col">
      <h3>Telephone Number</h3>
    </div>
    <div class="col">
      ${user.adminInfo.phone}
    </div>
  </div>
  </c:if>
  
  </c:if>
  
  <div class="row">
    <div class="col">
      <h2>Password</h2>
    </div>
    <div class="col">
      <div class="btn-group">
	      <a class="btn btn-primary" href="${s:mvcUrl('AC#getPasswordResetLink').arg(0, user.id).build()}">Make Password Reset Link</a>
	      <a class="btn btn-primary" href="${s:mvcUrl('AC#forcePasswordReset').arg(0, user.id).build()}">Force Password Reset</a>
      </div>
    </div>
  </div>
  
  <div class="row">
    <div class="col">
      <h2>${accountStatusMessage}</h2>
    </div>
    <div class="col">
      <c:if test="${accountNonLocked and enabled}">
      <a class="btn btn-primary" href="${s:mvcUrl('AC#disableUser').arg(0, user.id).build()}">
        <i class="fas fa-lock"></i> Disable Account
      </a>
      </c:if>
      <c:if test="${not enabled}">
      <a class="btn btn-primary" href="${s:mvcUrl('AC#enableUser').arg(0, user.id).build()}">
        <i class="fas fa-lock-open"></i> Enable Account
      </a>
      </c:if>
      <c:if test="${(not accountNonLocked) and enabled}">
      <a class="btn btn-primary" href="${s:mvcUrl('AC#unlockUser').arg(0, user.id).build()}">
        <i class="fas fa-lock-open"></i> Unlock Account
      </a>
      </c:if>
    </div>
  </div>
</div>

</t:userpage>