<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:userpage title="${(empty course) ? 'New Course' : ''}${(not empty course) ? 'Editing Course ' : ''}${(not empty course) ? fn:escapeXml(course.name) : ''}">

<div class="container-fluid p-lg-5 userpage-container">
  <form method="POST" action="${actionUrl}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	  <div class="row">
	    <div class="col">
	      <input type="text" id="name" name="name" class="form-control h1" value="${fn:escapeXml(course.name)}" placeholder="Name of course" />
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