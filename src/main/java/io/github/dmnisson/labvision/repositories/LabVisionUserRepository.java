package io.github.dmnisson.labvision.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import io.github.dmnisson.labvision.entities.LabVisionUser;

public interface LabVisionUserRepository extends BaseLabVisionUserRepository<LabVisionUser> {
	
	@Transactional
	@Modifying
	@Query("UPDATE LabVisionUser lvu SET lvu.failedLoginAttempts = 0 WHERE lvu.username=:username")
	public void resetFailedLogins(@Param("username") String username);
	
	@Transactional
	@Modifying
	@Query(	"UPDATE LabVisionUser lvu "
			+ "SET lvu.failedLoginAttempts = lvu.failedLoginAttempts + 1 "
			+ "WHERE lvu.username=:username")
	public void incrementFailedLogins(@Param("username") String username);
	
	@Transactional
	@Modifying
	@Query(	"UPDATE LabVisionUser lvu "
			+ "SET lvu.accountNonLocked = :nonlocked "
			+ "WHERE lvu.username = :username")
	public void setAccountNonLocked(@Param("username") String username, @Param("nonlocked") Boolean nonlocked);
}
