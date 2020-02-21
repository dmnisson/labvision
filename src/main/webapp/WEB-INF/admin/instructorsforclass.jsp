<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Instructors of ${fn:escapeXml(courseClass.name)} – ${fn:escapeXml(course.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#courses').build()}">Courses</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#classes').arg(0, course.id).build()}"><c:out value="${course.name}" /> Classes</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourseClass').arg(0, courseClass.id).build()}"><c:out value="${courseClass.name}" /></a></li>
      <li class="breadcrumb-item active" aria-current="page">Instructors of <c:out value="${courseClass.name}" /></li>
    </ol>
  </nav>
  <div class="row">
    <div class="col">
      <h1>Instructors of <c:out value="${courseClass.name}" /></h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${empty instructors}">
        No instructors assigned yet.
      </c:if>
      <c:if test="${not empty instructors}">
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              <th scope="col">Username</th>
              <th scope="col">Instructor Name</th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="instructor" items="${instructors.content}">
            <tr>
              <td><a href="${s:mvcUrl('AC#getUser').arg(0, instructor.id).build()}"><c:out value="${instructor.username}" /></a></td>
              <td><c:out value="${instructor.displayName}" /></td>
              <td>
                <a class="btn btn-primary" href="${s:mvcUrl('AC#unassignInstructor').arg(0, courseClass.id).arg(1, instructor.id).build()}">
                  <i class="fas fa-minus"></i> Unassign
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
      <form class="form-inline" method="POST" action="${assignInstructorActionUrl}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <label class="sr-only" for="instructorUsername">Username</label>
        <input type="text" class="form-control mb-2 mr-sm-2" id="instructorUsername" name="instructorUsername" placeholder="Instructor username" />
        
        <button type="submit" class="btn btn-primary"><i class="fas fa-plus"></i> Assign</button>
      </form>
    </div>
  </div>
</div>

</t:userpage>