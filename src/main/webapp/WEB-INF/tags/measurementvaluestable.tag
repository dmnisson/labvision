<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="measurement" type="io.github.dmnisson.labvision.dto.experiment.MeasurementInfo" %>
<%@ attribute name="measurementvalues" type="java.util.List" %>
<%@ attribute name="parameters" type="java.util.List" %>
<%@ attribute name="parametervalues" type="java.util.Map" %>
<%@ attribute name="id" %>
<%@ attribute name="addnewform" required="false" type="java.lang.Boolean" %>
<c:if test="${addnewform == null}"><c:set var="addnewform" value="${false}" /></c:if>
<div class="table-responsive" id="${id}">
  <table class="table table-fixed w-auto">
    <thead>
      <tr>
        <th scope="col">
          <c:out value="${measurement.name}" /> (${measurement.quantityTypeId.unitString})
        </th>
        <c:forEach var="parameter" items="${parameters}">
        <th scope="col">
          <c:out value="${parameter.name}" /> (${parameter.quantityTypeId.unitString})
        </th>
        </c:forEach>
        <th scope="col">Taken</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="measurementValue" items="${measurementvalues}">
      <tr>
        <td>${measurementValue.value} ± ${measurementValue.uncertainty}</td>
        <c:forEach var="parameter" items="${parameters}">
        <c:set var="parameterValue" value="${parametervalues[measurementValue.id][parameter.id]}" />
        <td>
          ${parameterValue.value} ± ${parameterValue.uncertainty}
        </td>
        </c:forEach>
        <td>
          <javatime:format value="${measurementValue.taken}" style="SS" />
        </td>
      </tr>
      </c:forEach>
    </tbody>
    <c:if test="${addnewform}">
    <tfoot>
      <tr>
       <td>
         <div class="form-row">
           <div class="col-3">
             <input class="form-control" id="measurementValueInput" name="measurementValue" type="number" step="any" />
           </div>
           <div class="col-1 text-center">
             <label for="measurementUncertaintyInput">±</label>
           </div>
           <div class="col-3">
             <input class="form-control" id="measurementUncertaintyInput" name="measurementUncertainty" type="number" step="any" />
           </div>
         </div>
       </td>
       <c:forEach var="parameter" items="${parameters}">
       <td>
         <div class="form-row">
           <div class="col-3">
             <input class="form-control" id="parameterValueInput${parameter.id}" name="parameterValue${parameter.id}" type="number" step="any" />
           </div>
           <div class="col-1 text-center">
             <label for="parameterUncertaintyInput${parameter.id}">±</label>
           </div>
           <div class="col-3">
             <input class="form-control" id="parameterUncertaintyInput${parameter.id}" name="parameterUncertainty${parameter.id}" type="number" step="any" />
           </div>
         </div>
       </td>
       </c:forEach>
         <td>
           <div class="col-2">
             <button type="submit" class="btn btn-primary">Add</button>
           </div>
        </td>
      </tr>
    </tfoot>
    </c:if>
  </table>
</div>
