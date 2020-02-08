<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<t:userpage title="Edit Profile – LabVision">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Edit My Profile</h1>
    </div>
  </div>
  <form method="POST" action="${actionUrl}">
	  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	  <div class="row">
	    <div class="col">
	      <h3>Username</h3>
	    </div>
	    <div class="col">
	      ${student.username}
	    </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <label for="studentName"><h3>Full Name</h3></label>
	    </div>
	    <div class="col">
	      <input type="text" class="form-control" name="studentName" value="${student.name}" />
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