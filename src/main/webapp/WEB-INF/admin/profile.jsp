<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<t:userpage title="My Profile – LabVision">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>My Profile&nbsp;
        <a class="btn btn-primary" href="${s:mvcUrl('AC#editProfile').build()}">Edit</a>
      </h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h3>Username</h3>
    </div>
    <div class="col">
      <c:out value="${user.username}" />
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h3>Display Name</h3>
    </div>
    <div class="col">
      <c:out value="${user.displayName}" />
    </div>
  </div>
  <c:if test="${not empty user.adminInfo.email}">
  <div class="row">
    <div class="col">
      <h2>Email Address</h2>
    </div>
    <div class="col">
      ${user.adminInfo.email}
    </div>
  </div>
  </c:if>
  <c:if test="${not empty user.adminInfo.phone}">
  <div class="row">
    <div class="col">
      <h2>Telephone Number</h2>
    </div>
    <div class="col">
      ${user.adminInfo.phone}
    </div>
  </div>
  </c:if>
  <div class="row">
    <div class="col">
      <h3>Password</h3>
    </div>
    <div class="col">
      <a class="btn btn-primary" href="${s:mvcUrl('RPC#beginPasswordReset').build()}">Reset Password</a>
    </div>
  </div>
</div>

</t:userpage>