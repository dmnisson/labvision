<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:confirmpage
  title="Confirm Delete ${fn:escapeXml(courseClass.name)} from ${fn:escapeXml(course.name)}"
  heading="Confirm Delete"
  question="Are you sure you want to delete class ${fn:escapeXml(courseClass.name)} from ${fn:escapeXml(course.name)}?"
  actionurl="${s:mvcUrl('AC#confirmDeleteCourseClass').arg(0, courseClass.id).build()}"
  nourl="${s:mvcUrl('AC#getCourseClass').arg(0, courseClass.id).build()}"
  nobtncontext="primary"
  yesbtncontext="danger"
/>