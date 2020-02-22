<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<t:userpage title="My Error Analysis">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>My Error Analysis</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <div class="accordion" id="experimentErrorsAccordion">
        <c:forEach var="experiment" items="${experiments}">
          <div class="card">
            <div class="card-header" id="experimentErrorsHeading${experiment.id}">
              <h2 class="mb-0">
                <button class="btn btn-link" type="button" data-toggle="collapse" data-target="#experimentErrors${experiment.id}" aria-expanded="true" aria-controls="experimentErrors${experiment.id}">
                  <c:out value="${experiment.name}" />
                </button>
              </h2>
            </div>
            
            <div id="experimentErrors${experiment.id}" class="collapse" aria-labelledby="experimentErrorsHeading${experiment.id}" data-parent="#experimentErrorsAccordion">
              <table class="table">
                <thead>
                  <tr>
                    <th scope="col" class="font-italic">Measurement</th>
                    <th scope="col">Mean</th>
                    <th scope="col">Sample Standard Deviation</th>
                    <th scope="col">Sample Size</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="measurement" items="${measurements[experiment.id]}">
                  <tr>
                    <th scope="row"><c:out value="${measurement.name}" /> (${measurement.quantityTypeId.unitString})</th>
                    <td>${(empty measurement.mean) ? '—' : measurement.mean}</td>
                    <td>${(empty measurement.sampleStandardDeviation) ? '—' : measurement.sampleStandardDeviation}</td>
                    <td>${measurement.sampleSize}</td>
                  </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>
</div>

</t:userpage>