<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:confirmpage
  title="Confirm Delete ${fn:escapeXml(course.name)}"
  heading="Confirm Delete"
  question="Are you sure you want to delete course ${fn:escapeXml(course.name)}?"
  actionurl="${s:mvcUrl('AC#confirmDeleteCourse').arg(0, course.id).build()}"
  nourl="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"
  nobtncontext="primary"
  yesbtncontext="danger"
/>