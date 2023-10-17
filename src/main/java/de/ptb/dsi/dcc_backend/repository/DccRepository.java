package de.ptb.dsi.dcc_backend.repository;

import de.ptb.dsi.dcc_backend.model.Dcc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DccRepository extends JpaRepository<Dcc, Long> {

    List<Dcc> findAllByPid(String pid);

    Dcc findDccByPid(String pid);
}

