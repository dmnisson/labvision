<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:confirmpage
  title="Confirm Dismiss ${fn:escapeXml(student.displayName)} from ${fn:escapeXml(courseClass.name)}"
  heading="Confirm Dismiss Student"
  question="Are you sure you want to dismiss student ${fn:escapeXml(student.displayName)} from ${fn:escapeXml(courseClass.name)}?"
  actionurl="${s:mvcUrl('AC#confirmLeaveStudent').arg(0, courseClass.id).arg(1, student.id).build()}"
  nourl="${s:mvcUrl('AC#studentsForCourseClass').arg(0, courseClass.id).build()}"
/>