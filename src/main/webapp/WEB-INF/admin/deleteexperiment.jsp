<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:confirmpage
  title="Confirm Delete ${fn:escapeXml(experiment.name)} from ${fn:escapeXml(course.name)}"
  heading="Confirm Delete"
  question="Are you sure you want to delete experiment ${fn:escapeXml(experiment.name)} from course ${fn:escapeXml(course.name)}?"
  actionurl="${s:mvcUrl('AC#confirmDeleteExperiment').arg(0, experiment.id).build()}"
  nourl="${s:mvcUrl('AC#getExperiment').arg(0, experiment.id).build()}"
  nobtncontext="primary"
  yesbtncontext="danger"
/>