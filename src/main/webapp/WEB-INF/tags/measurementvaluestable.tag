<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="measurement" type="labvision.entities.Measurement" %>
<%@ attribute name="measurementunitsymbol" %>
<%@ attribute name="parameterunitsymbols" %>
<%@ attribute name="measurementvalues" type="java.util.List" %>
<%@ attribute name="id" %>
<%@ attribute name="addnewform" required="false" type="java.lang.Boolean" %>
<c:if test="${addnewform == null}"><c:set var="addnewform" value="${false}" /></c:if>
<div class="table-responsive" id="${id}">
  <table class="table table-fixed">
    <thead>
      <tr>
        <th scope="col" class="col-3">
          ${measurement.name} (${measurementunitsymbol})
        </th>
        <c:forEach var="parameter" items="${measurement.parameters}">
        <th scope="col" class="col-3">
          ${parameter.name} (${parameterunitsymbols[parameter]})
        </th>
        </c:forEach>
        <th scope="col" class="col-3">Taken</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="measurementValue" items="${measurementvalues}">
      <tr>
        <td class="col-3">${measurementValue.value} ± ${measurementValue.uncertainty}</td>
        <c:forEach var="parameterValue" items="${measurementValue.parameterValues}">
        <td class="col-3">
          ${parameterValue.value} ± ${parameterValue.uncertainty}
        </td>
        </c:forEach>
        <td class="col-3">${measurementValue.taken}</td>
      </tr>
      </c:forEach>
    </tbody>
    <c:if test="${addnewform}">
    <tfoot>
      <tr class="form-row">
       <td class="col-3">
         <input class="form-control" id="measurementValueInput" name="measurementValue" type="number" step="any" />
       </td>
       <c:forEach var="parameter" items="${measurement.parameters}">
       <td class="col-3">
         <input class="form-control" id="parameterValueInput${parameter.id}" name="parameterValue${parameter.id}" type="number" step="any" />
       </td>
       </c:forEach>
       <td class="col-3">
         <button type="submit" class="btn btn-primary">Add</button>
       </td>
      </tr>
    </tfoot>
    </c:if>
  </table>
</div>
