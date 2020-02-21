<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Users">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Users</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <div class="table-responsive">
	      <table class="table">
	        <thead>
	          <tr>
	            <th scope="col">Username</th>
	            <th scope="col">Full Name</th>
	            <th scope="col">Type</th>
	            <th scope="col">Admin Access</th>
	            <th scope="col"></th>
	          </tr>
	        </thead>
	        <tbody>
	          <c:forEach var="user" items="${users}">
	          <tr>
	            <td><a href="${s:mvcUrl('AC#getUser').arg(0, user.id).build()}"><c:out value="${user.username}" /></a></td>
	            <td><c:out value="${user.displayName}" /></td>
	            <td>${user.userRole}</td>
	            <td>
	            <c:if test="${user.admin}">
	              <i class="fas fa-check"></i>
	            </c:if>
	            <c:if test="${not user.admin}">
	              <i class="fas fa-times"></i>
	            </c:if>
	            </td>
	            <td>
	              <div class="btn-group" role="group" aria-label="Actions for ${fn:escapeXml(user.username)}">
	                <a class="btn btn-primary" href="${s:mvcUrl('AC#editUser').arg(0, user.id).build()}"><i class="fas fa-user-edit"></i>Edit</a>
	                <a class="btn btn-primary" href="${s:mvcUrl('AC#deleteUser').arg(0, user.id).build()}"><i class="fas fa-user-minus"></i>Delete</a>
	              </div>
	            </td>
	          </tr>
	          </c:forEach>
	        </tbody>
	      </table>
      </div>
      <t:pagenav 
        pages="${pages}"
        currentpage="${currentPage}"
        prevpageurl="${prevPageUrl}"
        nextpageurl="${nextPageUrl}"
        pageurls="${pageUrls}"
      />
    </div>
  </div>
  <div class="row">
    <div class="col">
      <a class="btn btn-primary" href="${s:mvcUrl('AC#newUser').build()}"><i class="fas fa-user-plus"></i> New User</a>
    </div>
  </div>
</div>

</t:userpage>