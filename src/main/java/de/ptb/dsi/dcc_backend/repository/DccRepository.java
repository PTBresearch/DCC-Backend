package de.ptb.dsi.dcc_backend.repository;

import de.ptb.dsi.dcc_backend.entity.Dcc;
import de.ptb.dsi.dcc_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface DccRepository extends JpaRepository<Dcc, String> {

    Dcc findDccByPid(String pid);
    Boolean existsDccByPid(String pid);
    List<Dcc> findByStatus(String status);
    List<Dcc> findByStatusIgnoreCase(String status);
    List<Dcc> findByUser_UserName(String userName);
    List<Dcc> findDccsByUserRole(String role);
    List<Dcc> findByUser(User user);
    List<Dcc> findDccsByUserRoleAndStatus(String role ,String status);
    List<Dcc> findAll();
    List<Dcc> findDccsByUser_UserName(String userName);
    void deleteByIdAndUser(String id, User user);
    Optional<Dcc> findByPidAndUser(String pid, User user);


    Optional<Dcc> findByIdAndUser(String id, User user);

    Optional<Dcc> findByPid(String pid);
}