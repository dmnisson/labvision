<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page
  language="java"
  contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"
  import="io.github.dmnisson.labvision.DatabaseAction"
%>
    
<c:set var="CREATE" value="<%= DatabaseAction.CREATE %>" />
<c:set var="DELETE" value="<%= DatabaseAction.DELETE %>" />
 
<t:userpage title="${empty experiment ? 'New Experiment' : 'Editing'} ${empty experiment ? '' : name} – ${course.name}">
<jsp:attribute name="style">
tr.selected {
  background-color: #495057;
  color: #fff;
}

tr.selected .btn-secondary {
  background-color: #f8f9fa;
  color: #343a40;
}
</jsp:attribute>
<jsp:attribute name="script">
function capitalize(str) {
  return str.replace(/^\w/, function (c) {
    return c.toUpperCase();
  });
}

$(function() {
  // confirm before leaving page
  window.onbeforeunload = function () {
    return "Are you sure you want to leave this page? Your changes may not be saved.";
  };
  
  $("#editExperimentForm").submit(function () {
    window.onbeforeunload = undefined;
  });

  // pass id to correct modal data key for quantity type picker:
  // "selectedMeasurement" for measurements
  // "selectedParameter" for parameters
  const handleChooseQuantityTypeClick = function(id, variableType) {
    return function (e) {
      $("#quantityTypeSelectModal").data("selectedVariable", id)
      $("#quantityTypeSelectModal").data("selectedVariableType", variableType)
      $("#quantityTypeSelectModal").modal();
    }
  };

  const attachChooseQuantityTypeHandlers = function(variableType) {
    $("." + variableType + "-select").each(function(index, el) {
      const variableId = $(el).data("selectId");
    
      const chooseBtn = $("#select-quantity-type-" + variableType + "-" + variableId);
      chooseBtn.click(handleChooseQuantityTypeClick(variableId, variableType));
    });
  };
  attachChooseQuantityTypeHandlers("measurement");
  attachChooseQuantityTypeHandlers("parameter");
  
  const attachVariableTableHandlers = function(variableType) {
    $("#" + variableType + "-table").on("click", "." + variableType + "-select", function(e) {
	    const target = $(e.target);
	    const variableId = target.data("selectId");
	   
	    const targetRow = $("#" + variableType + "-row-" + variableId);
	    if (target.prop("checked")) {
	      targetRow.addClass("selected");
	    } else {
	      targetRow.removeClass("selected");
	    }
	    
	    const deleteBtn = $("#delete" + capitalize(variableType) + "s");
	    deleteBtn.prop(
	      "disabled",
	      $("." + variableType + "-select:checked").length === 0
	    );
	    
	    if (variableType === "measurement") {
		    const manageParametersBtn = $("#manageParameters");
		    manageParametersBtn.prop(
		      "disabled",
		      $(".measurement-select:checked").length !== 1
		    );
	    }
	  });
	  
	  $("#add" + capitalize(variableType)).click(function() {
	    // generate unique client-side ID for new variable
	    const newVariableIndex = $("#" + variableType + "-table").data("newVarIdCounter") || 0;
	    const newVariableId = "N" + (newVariableIndex + 1);
	    $("#" + variableType + "-table").data("newVarIdCounter", newVariableIndex + 1);
	  
	    const newTr = $("<tr></tr>")
	      .attr("id", variableType + "-row-" + newVariableId);
	    
	    if (variableType === "parameter") {
	      newTr.addClass("parameter-row-for-measurement-" 
	        + $("#manageParametersModal").data("measurementId"));
	    }
	    
	    // select row checkbox
	    const newCheckboxCell = $("<td></td>");
	    const newCheckbox = $("<input class=\"form-control " + variableType + "-select\" type=\"checkbox\" />")
	      .attr("id", "select-" + variableType + "-" + newVariableId)
	      .data("selectId", newVariableId);
	    newCheckboxCell.append(newCheckbox);
	    newTr.append(newCheckboxCell);
	    
	    // name field
	    const newNameCell = $("<td></td>");
	    const newNameField = $("<input class=\"form-control\" type=\"text\" />")
	      .attr("id", variableType + "Name" + newVariableId)
	      .attr("name", variableType + "Name" + newVariableId);
	    newNameCell.append(newNameField);
	    newTr.append(newNameCell);
	    
	    // quantity type with button to bring up choice dialog
	    const newQuantityTypeCell = $("<td></td>");
	    const newQuantityTypeSpan = $("<span></span>")
	      .attr("id", variableType + "QuantityType" + newVariableId);
	    newQuantityTypeCell.append(newQuantityTypeSpan);
	    newQuantityTypeCell.append(" ");
	    const newQuantityTypeHiddenField = $("<input class=\"form-control\" type=\"hidden\" />")
	      .attr("id", variableType + "QuantityTypeId" + newVariableId)
	      .attr("name", variableType + "QuantityTypeId" + newVariableId);
	    newQuantityTypeCell.append(newQuantityTypeHiddenField);
	    const newQuantityTypeChooseBtn = $("<button class=\"btn btn-secondary\" type=\"button\">Choose...</button>")
	      .attr("id", "select-quantity-type-" + variableType + "-" + newVariableId);
	    newQuantityTypeChooseBtn.click(handleChooseQuantityTypeClick(newVariableId, variableType));
	    newQuantityTypeCell.append(newQuantityTypeChooseBtn);
	    newTr.append(newQuantityTypeCell);
	    
	    if (variableType === "measurement") {
		    // number of parameters
		    const newNumOfParametersCell = $("<td></td>");
		    const newNumOfParametersSpan = $("<span></span>")
		      .attr("id", "numOfParametersMeasurement" + newVariableId)
		      .text("0");
		    newNumOfParametersCell.append(newNumOfParametersSpan);
		    newTr.append(newNumOfParametersCell);
	    }
	    
	    $("#" + variableType + "-table tbody:first").append(newTr);
	    
	    // hidden input to tell server that this is a new variable to be created
	    const actionCreateInput = $("<input type=\"hidden\" />")
	      .attr("id", variableType + "Action" + newVariableId)
	      .attr("name", variableType + "Action" + newVariableId)
	      .attr("value", "${CREATE}");
	    $("#editExperimentForm").append(actionCreateInput);
	    
	    if (variableType === "parameter") {
	       // hidden input to tell server which measurement needs this new parameter
	       const parameterMeasurementInput = $("<input type=\"hidden\" />")
	         .attr("id", "parameterMeasurementId" + newVariableId)
	         .attr("name", "parameterMeasurementId" + newVariableId)
	         .attr("value", $("#manageParametersModal").data("measurementId"));
	       $("#editExperimentForm").append(parameterMeasurementInput);
	    }
	  });
	
	  $("#delete" + capitalize(variableType) + "s").click(function() {
	    $("#numVariablesToDelete")
	      .text($("." + variableType + "-select:checked").length);
	    $("#variableTypeNumbered")
	      .text(variableType + ($("." + variableType + "-select:checked").length !== 1 ? "s" : ""));
	    $("#deleteVariablesModal").data("variableType", variableType);  
	    $("#deleteVariablesModal").modal();
	  });
  };
  attachVariableTableHandlers("measurement");
  attachVariableTableHandlers("parameter");

  $("#manageParameters").click(function () {
    const measurementId = $(".measurement-select:checked").data("selectId");
    $("#manageParametersModal").data("measurementId", measurementId);
    
    $(".parameter-row-for-measurement-" + measurementId).removeClass("d-none");
    
    $("#manageParametersModal").modal();
  })

  $("#submissionDeadline").datetimepicker({
    <c:if test="${not empty reportDueDate}">
    defaultDate: "${reportDueDate}",
    </c:if>
    icons: {
      time: "fas fa-clock",
      date: "fas fa-calendar",
      up: "fas fa-arrow-up",
      down: "fas fa-arrow-down"
    }
  });
  
  // modal buttons
  $("#chooseQuantityType").click(function() {
    const quantityTypeSelect = $("#quantityTypeSelect");
    const quantityType = quantityTypeSelect.val();
    const quantityTypeDisplay = quantityTypeSelect
      .children("option:selected")
      .text();
    const variableId = $("#quantityTypeSelectModal").data("selectedVariable");
    const variableType = $("#quantityTypeSelectModal").data("selectedVariableType");
  
    if (quantityType) {
	    $("#" + variableType + "QuantityTypeId" + variableId)
	      .val(quantityType);
	    $("#" + variableType + "QuantityType" + variableId)
	      .text(quantityTypeDisplay);
      
      $("#quantityTypeSelectModal").modal("hide");
    }
  });
  
  $("#manageParametersModal").on("hide.bs.modal", function() {
    const measurementId = $("#manageParametersModal").data("measurementId");
    
    $(".parameter-row-for-measurement-" + measurementId).addClass("d-none");
    $("#numOfParametersMeasurement" + measurementId)
      .text($(".parameter-row-for-measurement-" + measurementId).length);
  });
  
  $("#confirmDeleteVariables").click(function() {
    const variableType = $("#deleteVariablesModal").data("variableType");
  
    $("." + variableType + "-select:checked").each(function (index, checkboxEl) {
      const checkbox = $(checkboxEl);
      const variableId = checkbox.data("selectId");
      
      // remove the table row
      $("#" + variableType + "-row-" + variableId).remove();
      
      // if this was a variable to be created
      if ($("#" + variableType + "Action" + variableId).length !== 0
        && $("#" + variableType + "Action" + variableId).val() === "${CREATE}") {
        // simply remove the action input
        $("#" + variableType + "Action" + variableId).remove();
        if (variableType === "parameter") {
          // remove the measurement id input as well
          $("#parameterMeasurementId" + variableId).remove();
        }
      } else {
        // tell the server we need to delete this variable
        const deleteActionInput = $("<input type=\"hidden\" />")
          .attr("id", variableType + "Action" + variableId)
          .attr("name", variableType + "Action" + variableId)
          .attr("value", "${DELETE}");
        $("#editExperimentForm").append(deleteActionInput);
      }
    });
    
    $("#deleteVariablesModal").modal("hide");
  });
});
</jsp:attribute>
<jsp:body>
<div class="container-fluid p-lg-5 userpage-container">
  <form id="editExperimentForm" method="POST" action="${actionURL}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
  
    <input class="form-control form-control-lg" type="text" name="experimentName" id="experimentName" value="${fn:escapeXml(name)}" placeholder="Name of experiment" />
    
    <div class="form-group">
     <label for="description">Description</label>
     <textarea class="form-control" name="description" id="description">${description}</textarea>
    </div>
    
    <div class="row">
      <div class="col">
        <h2>Measurements</h2>
        <div class="form-group">
			    <table class="table" id="measurement-table">
			      <thead>
			        <tr>
			          <th scope="col"><i class="fas fa-check"></i></th>
			          <th scope="col">Name</th>
			          <th scope="col">Quantity Type</th>
			          <th scope="col">Num. of Parameters</th>
			        </tr>
			      </thead>
			      <tbody>
			        <c:forEach var="measurement" items="${measurements}">
			        <tr id="measurement-row-${measurement.id}">
			          <td>
			            <input class="form-control measurement-select" type="checkbox" id="select-measurement-${measurement.id}" data-select-id="${measurement.id}" />
			          </td>
			          <td>
			            <input class="form-control" type="text" name="measurementName${measurement.id}" value="${fn:escapeXml(measurement.name)}" />
			          </td>
			          <td>
			            <span id="measurementQuantityType${measurement.id}">${measurement.quantityTypeId.displayName}</span>
			            <input class="form-control" type="hidden" id="measurementQuantityTypeId${measurement.id}" name="measurementQuantityTypeId${measurement.id}" value="${measurement.quantityTypeId}" />
			            <button class="btn btn-secondary" type="button" id="select-quantity-type-measurement-${measurement.id}">
			              Choose...
			            </button>
			          </td>
			          <td>
			            <span id="numOfParametersMeasurement${measurement.id}">${fn:length(parameters[measurement.id])}</span>
			          </td>
			        </tr>
			        </c:forEach>
			      </tbody>
			    </table>
			    <div class="btn-group" role="group">
				    <button class="btn btn-secondary" id="addMeasurement" type="button">
				      <i class="fas fa-plus"></i>
				    </button>
				    <button class="btn btn-secondary" id="deleteMeasurements" type="button" disabled>
				      <i class="fas fa-minus"></i>
				    </button>
				    <button class="btn btn-secondary" id="manageParameters" type="button" disabled>
			        Manage Parameters...
			      </button>
			    </div>
		    </div>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <h2>Reports</h2>
		    <div class="form-group form-row">
		      <div class="col">
		        <label for="submissionDeadline">Submission Deadline</label>
		      </div>
		      <div class="col">
			      <div class="input-group date" id="submissionDeadline" data-target-input="nearest">
			        <input type="text" class="form-control datetimepicker-input" name="submissionDeadline" data-target="#submissionDeadline" />
			        <div class="input-group-append" data-target="#submissionDeadline" data-toggle="datetimepicker">
			          <div class="input-group-text"><i class="fas fa-calendar"></i></div>
			        </div>
			      </div>
		      </div>
		    </div>
      </div>
    </div>
    
    <button class="btn btn-primary" type="submit">Save Experiment</button>
    
    <!-- Modal dialogs -->
    <div class="modal fade" id="manageParametersModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h3>Manage Parameters</h3>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <i class="fas fa-window-close" aria-hidden="true"></i>
            </button>
          </div>
          <div class="modal-body">
            <table class="table" id="parameter-table">
              <thead>
                <tr>
                  <th scope="col"><i class="fas fa-check"></i></th>
                  <th scope="col">Name</th>
                  <th scope="col">Quantity Type</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="measurement" items="${measurements}">
                <c:forEach var="parameter" items="${parameters[measurement.id]}">
                <tr class="d-none parameter-row-for-measurement-${measurement.id}" id="parameter-row-${parameter.id}">
                  <td>
                    <input class="form-control parameter-select" id="select-parameter-${parameter.id}" type="checkbox" data-select-id="${parameter.id}" />
                  </td>
                  <td>
                    <input class="form-control" id="parameterName${parameter.id}" type="text" placeholder="Name of parameter" value="${fn:escapeXml(parameter.name)}" />
                  </td>
                  <td>
                    <span id="parameterQuantityType${parameter.id}">${parameter.quantityTypeId.displayName}</span>
                    <button class="btn btn-secondary" id="select-quantity-type-parameter" type="button">Choose...</button>
                  </td>
                </tr>
                </c:forEach>
                </c:forEach>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <div class="btn-group" role="group">
              <button class="btn btn-secondary" id="addParameter" type="button">
                <i class="fas fa-plus"></i>
              </button>
              <button class="btn btn-secondary" id="deleteParameters" type="button">
                <i class="fas fa-minus"></i>
              </button>
            </div>
            <button class="btn btn-primary" id="closeManageParametersModal" type="button" data-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="modal fade" id="quantityTypeSelectModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h3 class="modal-title">Select Quantity Type</h3>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <i class="fas fa-window-close" aria-hidden="true"></i>
            </button>
          </div>
          <div class="modal-body">
            <select class="form-control" id="quantityTypeSelect" size="8">
              <c:forEach var="quantityTypeId" items="${quantityTypeIdValues}">
              <option value="${quantityTypeId}">${quantityTypeId.displayName}</option>
              </c:forEach>
            </select>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" type="button" id="cancelQuantityTypeSelect" data-dismiss="modal">Cancel</button>
            <button class="btn btn-primary" type="button" id="chooseQuantityType">Choose</button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="modal fade" id="deleteVariablesModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h3>Confirm Delete</h3>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <i class="fas fa-window-close" aria-hidden="true"></i>
            </button>
          </div>
          <div class="modal-body">
            Are you sure you want to delete <span id="numVariablesToDelete"></span> <span id="variableTypeNumbered"></span>?
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" id="cancelDeleteVariables" data-dismiss="modal">No</button>
            <button type="button" class="btn btn-primary" id="confirmDeleteVariables">Yes</button>
          </div>
        </div>
      </div>
    </div>
    
	</form>
</div>
</jsp:body>
</t:userpage>