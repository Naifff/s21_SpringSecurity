package org.example.repositories;

import org.example.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	@Modifying
	@Query("UPDATE User u SET u.loginAttempts = u.loginAttempts + 1 WHERE u.username = :username")
	void incrementLoginAttempts(String username);

	@Modifying
	@Query("UPDATE User u SET u.loginAttempts = 0 WHERE u.username = :username")
	void resetLoginAttempts(String username);

	@Modifying
	@Query("UPDATE User u SET u.accountNonLocked = :locked WHERE u.username = :username")
	void updateAccountLock(String username, boolean locked);
}