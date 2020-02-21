package io.github.dmnisson.labvision.dto.reportedresult;

import java.time.LocalDateTime;

public class ReportedResultInfo {
	private final Integer id;
	private final String name;
	private final LocalDateTime added;
	
	public ReportedResultInfo(Integer id, String name, LocalDateTime added) {
		this.id = id;
		this.name = name;
		this.added = added;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getAdded() {
		return added;
	}

}
