<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="measurement" %>
<%@ attribute name="measurementunitsymbol" %>
<%@ attribute name="parameterunitsymbols" %>
<%@ attribute name="measurementvalues" %>
<%@ attribute name="id" %>
<%@ attribute name="addnewform" required="false" type="java.lang.Boolean" %>
<c:if test="${addnewform == null}"><c:set var="addnewform" value="${false}" /></c:if>
<div class="table-responsive" id="${id}">
  <table class="table table-fixed">
    <thead>
      <tr>
        <th scope="col">
          ${measurement.name} (${measurementunitsymbol})
        </th>
        <c:forEach var="parameter" items="${measurement.parameters}">
        <th scope="col">
          ${parameter.name} (${parameterunitsymbols[parameter]})
        </th>
        </c:forEach>
        <th scope="col">Taken</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="measurementValue" items="${measurementvalues}">
      <tr>
        <td>${measurementValue.value} ± ${measurementValue.uncertainty}</td>
        <c:forEach var="parameterValue" items="${measurementValue.parameterValues}">
        <td>
          ${parameterValue.value} ± ${parameterValue.uncertainty}
        </td>
        </c:forEach>
        <td>${measurementValue.taken}</td>
      </tr>
      </c:forEach>
    </tbody>
    <c:if test="${addnewform}">
    <tfoot>
      <tr>
       <td class="col">
         <input class="form-control" id="measurementValueInput" name="measurementValue" type="number" step="any" />
       </td>
       <c:forEach var="parameter" items="${measurement.parameters}">
       <td class="col">
         <input class="form-control" id="parameterValueInput${parameter.id}" name="parameterValue${parameter.id}" type="number" step="any" />
       </td>
       </c:forEach>
       <td class="col">
         <button type="submit" class="btn btn-primary">Add</button>
       </td>
      </tr>
    </tfoot>
    </c:if>
  </table>
</div>
