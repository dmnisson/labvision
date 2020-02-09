<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Dashboard">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Welcome, <c:out value="${instructor.displayName}" />!</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <div class="card">
        <div class="card-body">
          <p class="h2 card-text">${numOfExperiments}</p>
          <p>experiment${numOfExperiments eq 1 ? '' : 's'}</p>
          <a class="btn btn-primary" href="${s:mvcUrl('FC#experiments')}">Go To Experiments List</a>
        </div>
      </div>
    </div>
    <div class="col">
      <div class="card">
        <div class="card-body">
          <p class="h2 card-text">${numOfStudents}</p>
          <p>student${numOfStudents eq 1 ? '' : 's'}</p>
        </div>
      </div>
    </div>
  </div>
</div>

</t:userpage>