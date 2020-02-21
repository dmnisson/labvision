<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="io.github.dmnisson.labvision.entities.UserRole"
    %>

<c:set var="STUDENT" value="<%= UserRole.STUDENT %>" />
<c:set var="FACULTY" value="<%= UserRole.FACULTY %>" />
<c:set var="ADMIN" value="<%= UserRole.ADMIN %>" />

<t:userpage title="${(empty user) ? 'New User' : '' }${(not empty user) ? 'Editing user ' : ''}${fn:escapeXml(user.username)}">

<jsp:attribute name="script">
function showRows(admin, role) {
  // show admin info if admin box is selected
  if (admin) {
    $(".admin-row").removeClass("d-none");
    $(".faculty-row").addClass("d-none");
    $(".faculty-or-student-row").addClass("d-none");
  } else if (role === "${FACULTY}") {
    $(".admin-row").addClass("d-none");
    $(".faculty-row").removeClass("d-none");
    $(".faculty-or-student-row").removeClass("d-none");
  } else if (role === "${STUDENT}") {
    $(".admin-row").addClass("d-none");
    $(".faculty-row").addClass("d-none");
    $(".faculty-or-student-row").removeClass("d-none");
  }
}

$(function() {
  // confirm before leaving page
  window.onbeforeunload = function() {
    return "Are you sure you want to leave this page? Your changes may not be saved.";
  };
  
  // ensure correct rows are displayed
  showRows(${admin ? 'true' : 'false'}, "${user.role}")
  
  $("#editUserForm").submit(function() {
    window.onbeforeunload = undefined;
  });
  
  $("#role").change(function() {
    const newRole = $("#role").val();
    const admin = $("#admin").prop('checked') || newRole === "${ADMIN}";
    
    // update admin checkbox
    if (newRole === "${ADMIN}") {
      $("#admin").prop("checked", true);
      $("#admin").attr("disabled", true);
    } else {
      $("#admin").removeAttr("disabled");
    }
    
    showRows(admin, newRole);
  });
  
  $("#admin").click(function() {
    const admin = $("#admin").prop('checked');
    <c:if test="${empty user}">
    const role = $("#role").val();
    </c:if>
    <c:if test="${not empty user}">
    const role = "${user.role}";
    </c:if>
    
    showRows(admin, role);
  });
});
</jsp:attribute>

<jsp:body>

<div class="container-fluid p-lg-5 userpage-container">
  <form id="editUserForm" method="POST" action="${actionUrl}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <c:if test="${not empty errors}">
    <div class="row">
      <div class="col">
        <div class="alert alert-warning">
          <p>Please correct the following problem${fn:length(errors) eq 1 ? '' : 's'}:</p>
          <ul>
            <c:forEach var="error" items="${errors}">
            <li>${error}</li>
            </c:forEach>
          </ul>
        </div>
      </div>
    </div>
    </c:if>
    <c:if test="${empty user}">
	  <div class="row">
	    <div class="col">
	      <label class="h2" for="username">Username</label>
	    </div>
	    <div class="col">
	      <input type="text" class="form-control" id="username" name="username" required />
	    </div>
	  </div>
	  </c:if>
	  <c:if test="${not empty user}">
	  <div class="row">
	    <div class="col">
	      <h2><c:out value="${user.username}" /></h2>
	    </div>
	  </div>
	  </c:if>
	  <div class="row">
	    <div class="col">
	      <label class="h2" for="role">Type</label>
	    </div>
	    <c:if test="${empty user}">
	    <div class="col">
	      <select id="role" name="role" class="form-control" required>
	        <option ${(empty user) ? 'selected' : ''}>Please select a user type</option>
	        <option value="${STUDENT}" ${(user.role eq STUDENT) ? 'selected' : ''}>Student</option>
	        <option value="${FACULTY}" ${(user.role eq FACULTY) ? 'selected' : ''}>Faculty</option>
	        <option value="${ADMIN}" ${(user.role eq ADMIN) ? 'selected' : ''}>Administrator</option>
	      </select>
	    </div>
	    </c:if>
	    <c:if test="${not empty user}">
	    <div class="col">
	      ${user.role}
	    </div>
	    </c:if>
	  </div>
	  <div class="row">
	    <div class="col">
	      <h2>Admin Access</h2>
	    </div>
	    <div class="col">
	      <div class="form-check">
		      <input type="checkbox" class="form-check-input" id="admin" name="admin" value="true" ${(admin) ? 'checked' : ''} />
		      <label class="form-check-label" for="admin">Grant admin privileges</label>
	      </div>
	    </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <h2>Full Name</h2>
	    </div>
	    <div class="col">
	      <div class="row mb-2 admin-row">
	        <div class="col">
	          <label for="firstName">First</label>
	          <input
	            type="text"
	            class="form-control"
	            id="firstName"
	            name="firstName"
	            value="${fn:escapeXml(firstName)}"
	          />
	        </div>
	        <div class="col">
            <label for="lastName">Last</label>
            <input
              type="text"
              class="form-control"
              id="lastName"
              name="lastName"
              value="${fn:escapeXml(lastName)}"
            />
          </div>
	      </div>
	      <div class="row mb-2 faculty-or-student-row">
	        <div class="col">
	          <input
	            type="text"
	            class="form-control"
	            id="facultyOrStudentName"
	            name="facultyOrStudentName"
	            value="${fn:escapeXml(facultyOrStudentName)}"
	          />
	        </div>
	      </div>
	    </div>
	  </div>
	  <div class="row faculty-row ${(user.role eq FACULTY) ? '' : 'd-none'}">
	    <div class="col">
	      <label for="facultyEmail" class="h2">Email Address</label>
	    </div>
	    <div class="col">
	      <input type="text" class="form-control" id="facultyEmail" name="facultyEmail" value="${facultyEmail}" />
	    </div>
	  </div>
	  <div class="row admin-row ${(admin) ? '' : 'd-none'}">
	    <div class="col">
	      <label for="adminEmail" class="h2">Email Address</label>
	    </div>
	    <div class="col">
	      <input type="email" class="form-control" id="adminEmail" name="adminEmail" value="${user.adminInfo.email}" />
	    </div>
	  </div>
	  <div class="row admin-row ${(admin) ? '' : 'd-none'}">
	    <div class="col">
	      <label for="adminPhone" class="h2">Telephone Number</label>
	    </div>
	    <div class="col">
	      <input type="text" class="form-control" id="adminPhone" name="adminPhone" value="${user.adminInfo.phone}" /><br />
	      <small>Format: +15555550123</small>
	    </div>
	  </div>
	  <div class="row">
	    <div class="col">
	      <button type="submit" class="btn btn-primary">Save</button>
	    </div>
	  </div>
  </form>
</div>

</jsp:body>

</t:userpage>