<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="measurement" type="labvision.dto.experiment.MeasurementForExperimentTable" %>
<%@ attribute name="measurementvalues" type="java.util.List" %>
<%-- TODO add service function to get parameters/values --%>
<%--
<%@ attribute name="parameterunitsymbols" %>
<%@ attribute name="parametervalues" type="java.util.Map" %> --%>
<%@ attribute name="id" %>
<%@ attribute name="addnewform" required="false" type="java.lang.Boolean" %>
<c:if test="${addnewform == null}"><c:set var="addnewform" value="${false}" /></c:if>
<div class="table-responsive" id="${id}">
  <table class="table table-fixed w-auto">
    <thead>
      <tr>
        <th scope="col">
          ${measurement.name} (${measurement.unitString})
        </th>
        <%--
        <c:forEach var="parameter" items="${measurement.parameters}">
        <th scope="col">
          ${parameter.name} (${parameterunitsymbols[parameter]})
        </th>
        </c:forEach>
        --%>
        <th scope="col">Taken</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="measurementValue" items="${measurementvalues}">
      <tr>
        <td>${measurementValue.value} ± ${measurementValue.uncertainty}</td>
        <%--
        <c:forEach var="parameterValue" items="${parametervalues}">
        <td>
          ${parameterValue.value} ± ${parameterValue.uncertainty}
        </td>
        </c:forEach>
        --%>
        <td><!--  measurementValue.taken --></td>
      </tr>
      </c:forEach>
    </tbody>
    <c:if test="${addnewform}">
    <tfoot>
      <tr>
       <%-- <td colspan="${fn:length(measurement.parameters) + 2}"> --%>
       <td colspan="2">
         <div class="form-row">
           <div class="col-4">
             <input class="form-control" id="measurementValueInput" name="measurementValue" type="number" step="any" />
           </div>
           <div class="col-1 text-center">
             <label for="measurementUncertaintyInput">±</label>
           </div>
           <div class="col-4">
             <input class="form-control" id="measurementUncertaintyInput" name="measurementUncertainty" type="number" step="any" />
           </div>
           <%-- 
           <c:forEach var="parameter" items="${measurement.parameters}">
           <div class="col-4">
             <label for="parameterValueInput${parameter.id}">${parameter.name}</label>
             <input class="form-control" id="parameterValueInput${parameter.id}" name="parameterValue${parameter.id}" type="number" step="any" />
           </div>
           <div class="col-1 text-center">
             <label for="parameterUncertaintyInput${parameter.id}">±</label>
           </div>
           <div class="col-4">
             <input class="form-control" id="parameterUncertaintyInput${parameter.id}" name="parameterUncertainty${parameter.id}" type="number" step="any" />
           </div>
           </c:forEach>
           --%>
           <div class="col-2">
             <button type="submit" class="btn btn-primary">Add</button>
           </div>
         </div>
        </td>
      </tr>
    </tfoot>
    </c:if>
  </table>
</div>
