<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<t:userpage title="My Profile – LabVision">

<div class="container-fluid p-lg-5 userpage-container">
  <form method="POST" action="${actionUrl}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	  <div class="row">
	    <div class="col">
	      <h1>Editing My Profile</h1>
	    </div>
	  </div>
	  <c:if test="${not empty errors}">
	  <div class="row">
	    <div class="col">
	       <div class="alert alert-warning">
	         <c:choose>
	         <c:when test="${fn:length(errors) eq 1}">
	         ${fn:escapeXml(errors[0])}
	         </c:when>
	         <c:otherwise>
	         <p>Please correct the following problems:</p>
	         <ul>
	           <c:forEach var="error" items="${errors}">
	           <li>${fn:escapeXml(error)}</li>
	           </c:forEach>
	         </ul>
	         </c:otherwise>
	         </c:choose>
	       </div>
	    </div>
	  </div>
	  </c:if>
	  <div class="row">
	    <div class="col">
	      <h3>Username</h3>
	    </div>
	    <div class="col">
	      ${instructor.username}
	    </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <h3><label for="instructorName">Full Name</label></h3>
	    </div>
	    <div class="col">
	      <input type="text" class="form-control" name="instructorName" value="${instructor.name}" required autofocus />
	    </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <h3>Email Address</h3>
	    </div>
	    <div class="col">
	      <input type="email" class="form-control" name="instructorEmail" value="${instructor.email}" />
	    </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <h3>Password</h3>
	    </div>
	    <div class="col">
	      <a class="btn btn-primary" href="${s:mvcUrl('RPC#beginPasswordReset').build()}">Reset Password</a>
	    </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <button type="submit" class="btn btn-primary">Save</button>
	    </div>
	  </div>
  </form>
</div>

</t:userpage>