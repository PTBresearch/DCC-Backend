package de.ptb.dsi.dcc_backend.service;

import de.ptb.dsi.dcc_backend.repository.DccRepository;
import de.ptb.dsi.dcc_backend.model.Dcc;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DccServiceImpl implements DccService{
    private final DccRepository dccRepository;

    @Override
    public List<Dcc> getDccList() {
        return dccRepository.findAll();
    }


    @Override
    public Dcc getDccByPid(String pid) {
        return dccRepository.findDccByPid(pid);
    }

    @Override
    public Boolean isDccValid(String pid) {
       Dcc dcc= dccRepository.findDccByPid(pid);
        return dcc.isDccValid();
    }

    @Override
    public Dcc saveDcc(Dcc dcc) {
        return dccRepository.save(dcc);
    }

    @Override
    public String getBase64XmlByPid(String pid) {
        if(dccRepository.existsDccByPid(pid))
        return dccRepository.findDccByPid(pid).getXmlBase64();
        else return "pid not exist";
    }


}
