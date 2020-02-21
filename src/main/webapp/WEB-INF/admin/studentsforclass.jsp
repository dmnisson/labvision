<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Students in ${fn:escapeXml(courseClass.name)} – ${fn:escapeXml(course.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#courses').build()}">Courses</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#classes').arg(0, course.id).build()}"><c:out value="${course.name}" /> Classes</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourseClass').arg(0, courseClass.id).build()}"><c:out value="${courseClass.name}" /></a></li>
      <li class="breadcrumb-item active" aria-current="page">Students in <c:out value="${courseClass.name}" /></li>
    </ol>
  </nav>
  <div class="row">
    <div class="col">
      <h1>Students in <c:out value="${courseClass.name}" /></h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${empty students.content}">
        No students enrolled yet.
      </c:if>
      <c:if test="${not empty students.content}">
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              <th scope="col">Username</th>
              <th scope="col">Student Name</th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="student" items="${students.content}">
            <tr>
              <td><a href="${s:mvcUrl('AC#getUser').arg(0, student.id).build()}"><c:out value="${student.username}" /></a></td>
              <td><c:out value="${student.displayName}" /></td>
              <td>
                <a class="btn btn-primary" href="${s:mvcUrl('AC#leaveStudent').arg(0, courseClass.id).arg(1, student.id).build()}">
                  <i class="fas fa-minus"></i> Leave
                </a>
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
      </c:if>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${error eq 'nostudentfound'}">
      <div class="alert alert-info">
        No student with that username found.
      </div>
      </c:if>
      <c:if test="${error eq 'alreadyenrolled'}">
      <div class="alert alert-info">
        That student is already enrolled.
      </div>
      </c:if>
      <form class="form-inline" method="POST" action="${enrollStudentActionUrl}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <label class="sr-only" for="studentUsername">Username</label>
        <input type="text" class="form-control mb-2 mr-sm-2" id="studentUsername" name="studentUsername" placeholder="Student username" />
        
        <button type="submit" class="btn btn-primary mb-2"><i class="fas fa-plus"></i> Enroll</button>
      </form>
    </div>
  </div>
</div>

</t:userpage>