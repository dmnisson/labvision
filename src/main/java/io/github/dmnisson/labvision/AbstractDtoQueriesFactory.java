package io.github.dmnisson.labvision;

import org.springframework.data.repository.Repository;

public abstract class AbstractDtoQueriesFactory<
	R extends Repository<T, ID>, T, ID,
	Q extends DtoQueries<? extends DTOBase, UserID>, DTOBase, UserID
	> {
	
	protected AbstractDtoQueriesFactory() {
	}
	
	protected abstract Class<UserID> getUserIdClass();

	protected abstract <DTO extends DTOBase> Q createDtoQueriesForDtoType(R repository, 
			Class<DTO> dtoClass);

}