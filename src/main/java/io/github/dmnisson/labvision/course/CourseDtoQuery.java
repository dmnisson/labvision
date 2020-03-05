package io.github.dmnisson.labvision.course;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.DtoQueries;

interface CourseDtoQuery<DTO, UserID> extends DtoQueries<DTO, UserID> {

	public List<DTO> findCourseData(UserID userId, Pageable pageable);
	
}
