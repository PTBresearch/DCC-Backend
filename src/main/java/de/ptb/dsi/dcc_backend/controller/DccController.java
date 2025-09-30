package de.ptb.dsi.dcc_backend.controller;


import de.ptb.dsi.dcc_backend.dto.ChangePasswordRequest;
import de.ptb.dsi.dcc_backend.dto.LoginRequest;
import de.ptb.dsi.dcc_backend.dto.LoginResponse;
import de.ptb.dsi.dcc_backend.entity.User;
import de.ptb.dsi.dcc_backend.exception.UserNotFoundException;
import de.ptb.dsi.dcc_backend.repository.DccRepository;
import de.ptb.dsi.dcc_backend.repository.UserRepository;
import de.ptb.dsi.dcc_backend.entity.Dcc;
import de.ptb.dsi.dcc_backend.service.AuthService;
import de.ptb.dsi.dcc_backend.service.DccServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
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
        @Server(url = "http://localhost:8085", description = "URL local environment"),
        @Server(url = "https://d-si.ptb.de", description = "URL in production  environment")
//       , @Server(url = "http://localhost:8085", description = "URL local environment")
})
@Tag(name = "DCC_Controller" , description = "Controller with endpoints: /api/d-dcc")
@RestController
@RequestMapping("/api/d-dcc")
@AllArgsConstructor

public class DccController {
    private  final DccServiceImpl dccService;
    private  final UserRepository userRepository;
    private final DccRepository dccRepository;
    private final AuthService authService;

    @Operation(
            summary = "Retrieve available pidListUrl of DCC ",
            description = "The Get response is a List of String Pid data",
            tags = { "dccPidList"})
    @GetMapping(value = "/dccPidList")
    public ResponseEntity<List<String>> getPidList() {
        return  new ResponseEntity<>(dccService.getUrlListDccPid(),HttpStatus.OK);
    }
    @GetMapping(value = "/listAllDccPid")

    public ResponseEntity<List<String>> getAllListPid(Principal principal) {
        return new ResponseEntity<>(dccService.getListPid(principal),HttpStatus.OK);
    }
    @GetMapping(value = "/dcc/{pid}")
    public ResponseEntity<String> getBase64XmlDccByPid(@PathVariable String pid) {
        if (dccService.existsDccByPid(pid)) {
        return new ResponseEntity<>(dccService.getBase64XmlByPid(pid), HttpStatus.OK);}
        else return new ResponseEntity<>("pid not exist", HttpStatus.NOT_FOUND);
    }
    //TODO forgetpassword

    @GetMapping(value = "dccValidation/{pid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Hidden
    public ResponseEntity<Boolean> isDccValid(@PathVariable String pid) {
        if (dccService.existsDccByPid(pid)) {
        return new ResponseEntity<>(dccService.isDccValid(pid), HttpStatus.OK);}
        else return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/addDcc", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Hidden
    public ResponseEntity<String> addDcc(@RequestBody Dcc dcc) {
        if (dccService.saveIfNotExist(dcc)) {
            dccService.saveDcc(dcc);
            return new ResponseEntity<>(" Dcc successful created", HttpStatus.CREATED);
        } else return new ResponseEntity<>(dcc.getPid() + "  :pid already exist", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/addUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createUser(@RequestBody User user) {

            return new ResponseEntity<>(dccService.addUser(user), HttpStatus.CREATED);
    }
    @DeleteMapping("/by-username/{username}")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username) {
        try {
            dccService.deleteUserByUserName(username);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        }
    }
    @GetMapping(value = "/dcc/{pid}/{refType}", produces = {MediaType.APPLICATION_XML_VALUE})
    @Hidden
    public ResponseEntity<String> geDccContentByRefType(@PathVariable String pid, @PathVariable String refType) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException, TransformerException, ParserConfigurationException, IOException, TransformerException, SAXException {
        return new ResponseEntity<>(dccService.findNodeByRefType(pid, refType), HttpStatus.OK);
    }

    @GetMapping("/privateListPid")
    @Hidden
    public ResponseEntity<List<String>> getPrivateListPid() {

        return  new ResponseEntity<>(dccService.getPrivateListPid(),HttpStatus.OK);
    }

    @GetMapping("/publicListPid")
    public ResponseEntity<List<String>> getPublicListPid() {

        return  new ResponseEntity<>(dccService.getPublicListPid(),HttpStatus.OK);
    }
    @GetMapping("/coordinatorListPid")
    @Hidden
    public ResponseEntity<List<String>> getCoordinatorListPid( Principal principal) {

        return  new ResponseEntity<>(dccService.getCoordinatorListPid(principal),HttpStatus.OK);
    }
    @GetMapping("/coordinatorListPidAndPublic")
    public ResponseEntity<List<String>> getCoordinatorListPidAndPublic( Principal principal) {

        return  new ResponseEntity<>(dccService.getPublicCoordinatorListPid(principal),HttpStatus.OK);
    }
    @GetMapping("/userListPid")
    @Hidden
    public ResponseEntity<List<String>> getUserListPid() {

        return  new ResponseEntity<>(dccService.getListPidByUser(),HttpStatus.OK);
    }

    @GetMapping("/dccList")
    @Hidden
    public ResponseEntity<List<Dcc>> getDCcList() {

        return  new ResponseEntity<>(dccService.getDccList(),HttpStatus.OK);
    }

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDcc(
            @RequestPart("file") MultipartFile file, @RequestPart("status") String status,
            @RequestPart("pid") String pid, @RequestPart("information") String information) throws Exception {




        byte[] xmlBytes = file.getBytes();

        dccService.processAndSaveDcc(pid,information,status, xmlBytes);

        return ResponseEntity.accepted().body("Upload received, Time Stamp Request file is generated.");
    }
//    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> uploadDcc(
//            @RequestPart("file") MultipartFile file,
//            @RequestPart("pid") String pid,
//            Principal principal) throws Exception {
//
//        String username = principal.getName();
//        User user = userRepository.findByUserName(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        byte[] xmlBytes = file.getBytes();
//
//        dccService.processAndSaveDcc(pid, xmlBytes, user);
//
//        return ResponseEntity.accepted().body("Upload received, Time Stamp Request file is generated.");
//    }
    @GetMapping("/downloadXml")
    public ResponseEntity<byte[]> downloadDcc(@RequestParam("pid") String pid, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Base64-kodierten XML-String aus Datenbank/Service holen
        String base64Xml = dccService.getBase64EncodedXml(pid, user);

        if (base64Xml == null || base64Xml.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Base64 in Byte-Array dekodieren
        byte[] xmlBytes = Base64.getDecoder().decode(base64Xml);

        // Header setzen
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("dcc_" + pid + ".xml")
                .build());

        return new ResponseEntity<>(xmlBytes, headers, HttpStatus.OK);
    }
//    @PostMapping("/verify")
//    public ResponseEntity<String> verifyTimestamp(
//            @RequestParam("pid") String pid) {
//
//
//
//        Dcc dcc = dccRepository.findDccByPid(pid);
//
//        try {
//            byte[] xmlBytes = Base64.getDecoder().decode(dcc.getXmlBase64());
//            boolean valid = dccService.verifyTimestamp(xmlBytes, dcc.getSignedTsrFile());
//            return ResponseEntity.ok(valid ? "Timestamp is valid" : "Timestamp is invalid");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error during verification: " + e.getMessage());
//        }
//    }
    @PostMapping("/verify")
    public ResponseEntity<String> verifyTimestamp(
            @RequestParam("pid") String pid,
            Principal principal) {

        User user = userRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Dcc dcc = dccRepository.findByPidAndUser(pid, user)
                .orElseThrow(() -> new RuntimeException("DCC not found"));

        try {
            byte[] xmlBytes = Base64.getDecoder().decode(dcc.getXmlBase64());
            boolean valid = dccService.verifyTimestamp(xmlBytes, dcc.getSignedTsrFile());
            return ResponseEntity.ok(valid ? "Timestamp is valid" : "Timestamp is invalid");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during verification: " + e.getMessage());
        }
    }
    @Operation(summary = "Download TSR file for a DCC",
            description = "Downloads the timestamp response (.tsr) as a file")
    @GetMapping("/download-tsr")
    public ResponseEntity<byte[]> downloadTsr(
            @Parameter(description = "DCC PID ", example = "Mass_NIST")
            @RequestParam("pid") String pid,
            Principal principal) {

    // 1. Aktuellen User holen
        User user = userRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. DCC-Eintrag mit passender PID und User holen
        Dcc dcc = dccRepository.findByPidAndUser(pid, user)
                .orElseThrow(() -> new RuntimeException("DCC not found"));

        byte[] tsrBytes = dcc.getSignedTsrFile();

        // 3. Datei als .tsr zum Download zurückgeben
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pid + ".tsr\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(tsrBytes);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteDcc(@PathVariable String id, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        dccService.deleteByIdAndUser(id, user);
        return ResponseEntity.noContent().build();
    }
//    @GetMapping("/user")
//    public ResponseEntity<List<Dcc>> getUserDccs(Authentication auth) {
//        String email = auth.getName();
//        // Optional mit Exception Handling
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<Dcc> userDccs = dccRepository.findByUser(user);
//        return ResponseEntity.ok(userDccs);
//    }

//    @GetMapping("/admin")
//    public ResponseEntity<List<Dcc>> getAllDccs(Authentication auth) {
//        String email = auth.getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!user.getRole().equalsIgnoreCase("ADMIN")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        List<Dcc> allDccs = dccRepository.findAll();
//        return ResponseEntity.ok(allDccs);
//    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userRepository.findAll() ,HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.login(request);
            LoginResponse response = new LoginResponse(
                    user.getUserName(),
                    user.getRole(),
                    user.isActive()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Principal principal) {
        dccService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Password changed successfully");
    }

}
