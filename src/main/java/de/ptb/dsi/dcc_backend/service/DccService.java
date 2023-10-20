package de.ptb.dsi.dcc_backend.service;

import de.ptb.dsi.dcc_backend.model.Dcc;


import java.util.List;

public interface DccService {

    List<Dcc> getDccList();
    Dcc getDccByPid(String pid);
    Boolean isDccValid(String pid);
    Dcc saveDcc(Dcc dcc);
    String getBase64XmlByPid(String pid);

}
