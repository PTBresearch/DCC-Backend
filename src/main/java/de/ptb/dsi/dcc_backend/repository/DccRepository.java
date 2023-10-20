package de.ptb.dsi.dcc_backend.repository;

import de.ptb.dsi.dcc_backend.model.Dcc;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DccRepository extends JpaRepository<Dcc, String> {

    Dcc findDccByPid(String pid);
    Boolean existsDccByPid(String pid);
}

