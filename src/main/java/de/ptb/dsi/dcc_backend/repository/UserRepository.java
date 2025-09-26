package de.ptb.dsi.dcc_backend.repository;


import de.ptb.dsi.dcc_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserName(String userName);
    boolean existsUserByUserName(String userName);
    List<User> findAll();
}
