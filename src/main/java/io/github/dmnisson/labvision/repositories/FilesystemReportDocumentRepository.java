package io.github.dmnisson.labvision.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.dmnisson.labvision.entities.FilesystemReportDocument;

public interface FilesystemReportDocumentRepository extends JpaRepository<FilesystemReportDocument, Integer> {

	Optional<FilesystemReportDocument> findByFilesystemPath(String string);
	
}
