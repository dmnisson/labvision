<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:confirmpage
  title="Confirm Deactivate ${fn:escapeXml(student.displayName)} from ${fn:escapeXml(experiment.name)}"
  heading="Confirm Deactivate Student from ${fn:escapeXml(experiment.name)}"
  question="Are you sure you want to deactivate student ${fn:escapeXml(student.displayName)} from ${fn:escapeXml(experiment.name)}?"
  actionurl="${s:mvcUrl('AC#confirmDeactivateStudent').arg(0, experiment.id).arg(1, student.id).build()}"
  nourl="${s:mvcUrl('AC#activeStudentsForExperiment').arg(0, experiment.id).build()}"
/>