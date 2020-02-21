<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:confirmpage
  title="Confirm Unassign ${fn:escapeXml(instructor.displayName)} from ${fn:escapeXml(experiment.name)}"
  heading="Confirm Unassign Instructor"
  question="Are you sure you want to unassign instructor ${fn:escapeXml(instructor.displayName)} from ${fn:escapeXml(experiment.name)}?"
  actionurl="${s:mvcUrl('AC#confirmUnassignInstructorFromExperiment').arg(0, experiment.id).arg(1, instructor.id).build()}"
  nourl="${s:mvcUrl('AC#instructorsForExperiment').arg(0, experiment.id).build()}"
/>