package de.ptb.dsi.dcc_backend.controller;


import de.ptb.dsi.dcc_backend.service.DccServiceImpl;
import de.ptb.dsi.dcc_backend.model.Dcc;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;

@Tag(name = "DCC Backend Api", description = " management API")

@OpenAPIDefinition(servers = {
        @Server(url = "http://localhost:8080", description = "local system")
})
@RestController
@RequestMapping("/api/d-dcc")
@AllArgsConstructor
public class DccController {
    private DccServiceImpl dccService;

    @GetMapping(value = "/dccPidList")
    public List<String> getPidList() {

        return dccService.getListDccPid();
    }
    @GetMapping(value = "/dccList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Dcc> getDccList() {
        return dccService.getDccList();
    }
    @GetMapping(value = "/dccList/{pid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dcc> getDccByPid(@PathVariable String pid) {
        return  new ResponseEntity<>(dccService.getDccByPid(pid), HttpStatus.OK);
    }
    @GetMapping(value = "/dcc/{pid}")
    public ResponseEntity<String> getXmlBase64ByPid(@PathVariable String pid) {
        return  new ResponseEntity<>( dccService.getBase64XmlByPid(pid), HttpStatus.OK);
    }

    @GetMapping(value = "dccValidation/{pid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> isDccValid(@PathVariable  String pid)  {
        return new ResponseEntity<>(dccService.isDccValid(pid), HttpStatus.OK);
    }

    @PostMapping(value = "/addDcc", produces = {MediaType.APPLICATION_JSON_VALUE})
    public  ResponseEntity<Dcc> addDcc(@RequestBody Dcc dcc) {
        return new ResponseEntity<>(dccService.saveDcc(dcc), HttpStatus.CREATED);
    }
    @GetMapping(value = "/dcc/{pid}/{refType}", produces = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getRefBase64ByPid(@PathVariable String pid, @PathVariable String refType) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException, TransformerException {
        return  new ResponseEntity<>( dccService.findNodeByRefType(pid, refType), HttpStatus.OK);
    }
}
