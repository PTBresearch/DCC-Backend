package de.ptb.dsi.dcc_backend.controller;


import de.ptb.dsi.dcc_backend.service.DccServiceImpl;
import de.ptb.dsi.dcc_backend.model.Dcc;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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



@OpenAPIDefinition(
        info = @Info(
                title = "DCC Service Backend API",
                description = "This API exposes endpoints to manage DCC-Backend.",
                version = "1.0.0",
                contact = @Contact(
                        name = "ptb",
                        url = "https://www.ptb.de",
                        email = "contact@ptb.de"
                )),servers = {
        @Server(url = "http://localhost:8085", description = "local system")
})
@Tag(name = "DCC_Controller" , description = "Controller with endpoints: /api/d-dcc")
@RestController
@RequestMapping("/api/d-dcc")
@AllArgsConstructor
public class DccController {
    private  final DccServiceImpl dccService;

    @Operation(
            summary = "Retrieve available pidList of DCC ",
            description = "The Get response is a List of String Pid data",
            tags = { "dccPidList"})
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "PidList url")
    })
    @GetMapping(value = "/dccPidList")
    public List<String> getPidList() {
        return dccService.getListDccPid();
    }
    @GetMapping(value = "/dccPid")
    public List<String> getListPid() {
        return dccService.getListPid();
    }
    @GetMapping(value = "/dcc/{pid}")
    public ResponseEntity<String> getDccByPid(@PathVariable String pid) {
        if (dccService.existsDccByPid(pid)) {
        return new ResponseEntity<>(dccService.getBase64XmlByPid(pid), HttpStatus.OK);}
        else return new ResponseEntity<>("pid not exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "dccValidation/{pid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> isDccValid(@PathVariable String pid) {
        if (dccService.existsDccByPid(pid)) {
        return new ResponseEntity<>(dccService.isDccValid(pid), HttpStatus.OK);}
        else return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/addDcc")
    public ResponseEntity<String> addDcc(@RequestBody Dcc dcc) {
        if (dccService.saveIfNotExist(dcc)) {
            dccService.saveIfNotExist(dcc);
            return new ResponseEntity<>(" Dcc successful created", HttpStatus.CREATED);
        } else return new ResponseEntity<>(dcc.getPid() + "  :pid already exist", HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/dcc/{pid}/{refType}", produces = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> geDccContentByRefType(@PathVariable String pid, @PathVariable String refType) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException, TransformerException {
        return new ResponseEntity<>(dccService.findNodeByRefType(pid, refType), HttpStatus.OK);
    }
}
