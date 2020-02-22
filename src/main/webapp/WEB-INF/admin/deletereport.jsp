<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:confirmpage
  title="Confirm Delete ${fn:escapeXml(report.name)} from ${fn:escapeXml(experiment.name)}"
  heading="Confirm Delete"
  question="Are you sure you want to delete report ${fn:escapeXml(report.name)} from experiment ${fn:escapeXml(experiment.name)}?"
  actionurl="${s:mvcUrl('AC#confirmDeleteReportedResult').arg(0, report.id).build()}"
  nourl="${s:mvcUrl('AC#getReportedResult').arg(0, report.id).build()}"
  nobtncontext="primary"
  yesbtncontext="danger"
/>