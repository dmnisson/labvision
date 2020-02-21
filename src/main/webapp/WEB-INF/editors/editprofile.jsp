<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<t:userpage title="Edit Admin Profile – LabVision">

<div class="container-fluid p-lg-5 userpage-container">
  <form method="POST" action="${actionUrl}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <c:if test="${not empty errors}">
    <div class="row">
      <div class="col">
        <div class="alert alert-warning">
          <p>Please correct the following problem${fn:length(errors) eq 1 ? '' : 's'}:</p>
          <ul>
            <c:forEach var="error" items="${errors}">
            <li><c:out value="${error}" /></li>
            </c:forEach>
          </ul>
        </div>
      </div>
    </div>
    </c:if>
	  <div class="row">
	    <div class="col">
	      <h1>Editing Profile</h1>
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
	      <h3>Full Name</h3>
	    </div>
	    <div class="col">
        <div class="row mb-2">
          <div class="col">
            <label for="firstName">First</label>
            <input
              type="text"
              class="form-control"
              id="firstName"
              name="firstName"
              value="${fn:escapeXml(user.adminInfo.firstName)}"
            />
          </div>
          <div class="col">
            <label for="lastName">Last</label>
            <input
              type="text"
              class="form-control"
              id="lastName"
              name="lastName"
              value="${fn:escapeXml(user.adminInfo.lastName)}"
            />
          </div>
        </div>
      </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <label for="email" class="h2">Email Address</label>
	    </div>
	    <div class="col">
	      <input
          type="text"
          class="form-control"
          id="email"
          name="email"
          value="${user.adminInfo.email}"
        />
	    </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <label for="phone" class="h2">Telephone Number</label>
	    </div>
	    <div class="col">
	      <input
          type="text"
          class="form-control"
          id="phone"
          name="phone"
          value="${user.adminInfo.phone}"
        />
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