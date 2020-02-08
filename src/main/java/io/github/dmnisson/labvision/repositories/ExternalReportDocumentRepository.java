package io.github.dmnisson.labvision.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.dmnisson.labvision.entities.ExternalReportDocument;

public interface ExternalReportDocumentRepository extends JpaRepository<ExternalReportDocument, Integer> {

}
