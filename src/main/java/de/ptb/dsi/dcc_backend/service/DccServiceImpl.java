package de.ptb.dsi.dcc_backend.service;

import de.ptb.dsi.dcc_backend.config.CustomProperties;
import de.ptb.dsi.dcc_backend.dto.ChangePasswordRequest;
import de.ptb.dsi.dcc_backend.dto.TimestampVerificationResult;
import de.ptb.dsi.dcc_backend.entity.User;
import de.ptb.dsi.dcc_backend.exception.DccAlreadyExistsException;
import de.ptb.dsi.dcc_backend.exception.UserAlreadyExistsException;
import de.ptb.dsi.dcc_backend.exception.UserNotFoundException;
import de.ptb.dsi.dcc_backend.repository.DccRepository;
import de.ptb.dsi.dcc_backend.entity.Dcc;
import de.ptb.dsi.dcc_backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.tsp.*;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class DccServiceImpl implements DccService {
    private final DccRepository dccRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomProperties customProperties;
    private static final Logger log = null;

    @Override
    public List<Dcc> getDccList() {
        return dccRepository.findAll();
    }

    public List<Dcc> getAllDccList(Principal principal) {
        Authentication auth = (Authentication) principal;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return dccRepository.findAll();
        } else {
            throw new AccessDeniedException("Only admin role has authentication to access this resource.");
        }

    }


    @Override
    public Dcc getDccByPid(String pid) {

        return dccRepository.findDccByPid(pid);
    }

    @Override
    @Hidden
    public Boolean isDccValid(String pid) {
        Dcc dcc = dccRepository.findDccByPid(pid);
        return dcc.isDccValid();
    }

    @Override
    public boolean saveIfNotExist(Dcc dcc) {
        if (!dccRepository.existsDccByPid(dcc.getPid())) {
            return true;
        } else return false;
    }

    @Override
    public Dcc saveDcc(Dcc dcc) {
        if (!dccRepository.existsDccByPid(dcc.getPid())) {
            dccRepository.save(dcc);
        }
        return dcc;
    }

    @Override
    public String getBase64XmlByPid(String pid) {
        String base64 = dccRepository.findDccByPid(pid).getXmlBase64();
        if (dccRepository.existsDccByPid(pid)) {
            System.out.println("base64: " + base64);
            return base64;
        } else return "pid not exist";
    }



    public List<String> getListPid(Principal principal) {
        Authentication auth = (Authentication) principal;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            List<Dcc> dccList = dccRepository.findAll();
            return dccList.stream().map(Dcc::getPid).collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("Only admin role has authentication to access this resource.");
        }
    }

    public List<String> getPublicListPid() {
        List<Dcc> publicDccList = dccRepository.findByStatus("public");

        List<String> publicPidList = publicDccList.stream()
                                  .map(pid -> "http://localhost:8085/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());
//                .map(pid ->"https://d-si.ptb.de/api/d-dcc/dcc/" +  pid.getPid()).collect(Collectors.toList());
        return publicPidList;
    }
    @Override
    public List<String> getUrlListDccPid() {
        List<Dcc> dccList = dccRepository.findAll();
        List<String> pidList = dccList.stream()
//                .map(pid -> "https://d-si.ptb.de/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());
                  .map(pid -> "http://localhost:8085/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());

        return pidList;
    }
    public List<String> getPrivateListPid() {
        List<Dcc> privateDccList = dccRepository.findByStatus("private");

        List<String> privatePidList = privateDccList.stream()
                .map(pid -> "https://d-si.ptb.de/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());
        return privatePidList;
    }

    public List<String> getCoordinatorListPid(Principal principal) {

        List<Dcc> coordinatorDccList = dccRepository.findDccsByUser_UserName(principal.getName());

        return coordinatorDccList.stream()
//                .map(pid -> "https://d-si.ptb.de/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());
                  .map(pid -> "http://localhost:8085/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());

    }

    public List<Dcc> getCoordinatorDccList(Principal principal) {
        return dccRepository.findDccsByUser_UserName(principal.getName());
    }

    public List<Dcc> getPublicAndOwnDccList(Principal principal) {
        String userName = principal.getName();

        List<Dcc> publicDccs = dccRepository.findByStatusIgnoreCase("public");
        List<Dcc> userDccs = dccRepository.findByUser_UserName(userName);

        // Zusammenführen & Duplikate entfernen
        Set<Dcc> publicAndOwnDccSet = new HashSet<>();
        publicAndOwnDccSet.addAll(publicDccs);
        publicAndOwnDccSet.addAll(userDccs);
        return new ArrayList<>(publicAndOwnDccSet);
    }

    public Page<Dcc> getPublicAndOwnDccListPaged(Principal principal, int page, int size) {
        String userName = principal.getName();

        List<Dcc> publicDccs = dccRepository.findByStatusIgnoreCase("public");
        List<Dcc> userDccs = dccRepository.findByUser_UserName(userName);

        Set<Dcc> combinedSet = new LinkedHashSet<>();
        combinedSet.addAll(publicDccs);
        combinedSet.addAll(userDccs);

        List<Dcc> combinedList = new ArrayList<>(combinedSet);
        // Paging
        int start = page * size;
        int end = Math.min(start + size, combinedList.size());

        List<Dcc> pageContent = start < end ? combinedList.subList(start, end) : Collections.emptyList();

        return new PageImpl<>(pageContent, PageRequest.of(page, size), combinedList.size());
    }

    public User addUser(User user) {
        if (userRepository.existsUserByUserName(user.getUserName())) {
            throw new UserAlreadyExistsException(user.getUserName());
        }
        User newUser = User.builder().
                userName(user.getUserName()).
                email(user.getEmail()).
                password(user.getPassword()).
                role(user.getRole()).
                active(user.isActive()).
                build();
        return newUser;
    }

    public void deleteUserByUserName(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with username '" + username + "' not found.");
        }
        userRepository.delete(user.get());
    }

    public List<String> getListPidByUser() {
        List<Dcc> userDccList = dccRepository.findDccsByUser_UserName("Max");

        return userDccList.stream()
                .map(Dcc::getPid)
                .collect(Collectors.toList());
    }

    public List<String> getPublicCoordinatorListPid(Principal principal) {
        List<String> publicCoordinatorDccList = new ArrayList<>();
        List<String> coordinatorDccListPid = getCoordinatorListPid(principal);
        List<String> publicDccListPid = getPublicListPid();
        publicCoordinatorDccList.addAll(publicDccListPid);
        publicCoordinatorDccList.addAll(coordinatorDccListPid);
        return publicCoordinatorDccList;
    }

    @Override
    public String findNodeByRefType(String pid, String refType) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        boolean existsDccByPid = dccRepository.existsDccByPid(pid);
        if (existsDccByPid) {
            //decode Base 64
            String xmlBase64 = dccRepository.findDccByPid(pid).getXmlBase64();
            byte[] byteBase64 = Base64.getDecoder().decode(xmlBase64);
            String decodedXml = new String(byteBase64, StandardCharsets.UTF_8);
            //parse the xml
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(decodedXml));
            Document document = builder.parse(is);

            NodeList nodeList = document.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element element) {
                    //check if the element has the desired attribute with the desired value
                    if (element.hasAttribute("refType") && element.getAttribute("refType").equals(refType)) {
                        //convert the found element to a string, including its children and attributes
                        String resultXml = nodeToString(element);
                        System.out.println("result: " + resultXml);
                        return resultXml;
                    }
                }
            }
            return "refType not exist";
        } else
            return "pid not exist";
    }

    @Override
    public String nodeToString(Node node) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

    @Override
    public boolean findIfPidExist(Dcc dcc) {
        return dccRepository.existsDccByPid(dcc.getPid());
    }

    @Override
    public boolean existsDccByPid(String pid) {
        return dccRepository.existsDccByPid(pid);
    }
    public String getBase64EncodedXml(String pid, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isAdminOrCoordinator = user.getRole().equalsIgnoreCase("ADMIN")
                || user.getRole().equalsIgnoreCase("COORDINATOR");

        Optional<Dcc> dccOptional;

        if (isAdminOrCoordinator) {
            dccOptional = Optional.ofNullable(dccRepository.findDccByPid(pid));
        } else {
            dccOptional = dccRepository.findByPidAndUser(pid, user);
        }

        Dcc dcc = dccOptional.orElseThrow(() -> {
            log.warn("DCC not found or access denied for pid='{}' and user='{}'", pid, username);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "DCC not found or access denied");
        });

        String base64Xml = dcc.getXmlBase64();

        if (base64Xml == null || base64Xml.isBlank()) {
            log.warn("DCC has no XML content for pid='{}'", pid);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "DCC has no content");
        }

        return base64Xml;
    }


    public Dcc processAndSaveDcc(String pid, String information, String status, byte[] xmlBytes, User user) throws Exception {
        if (dccRepository.existsDccByPid(pid)) {
            throw new DccAlreadyExistsException(pid);
        }
        long startTotal = System.currentTimeMillis();

        // 1. XML in Base64
        long start = System.currentTimeMillis();
        String xmlBase64 = Base64.getEncoder().encodeToString(xmlBytes);
        System.out.println("Base64 Encoding lasted: " + (System.currentTimeMillis() - start) + "ms");

        // 2. SHA-512 Hash
        start = System.currentTimeMillis();
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(xmlBytes);
        System.out.println("Hashing lasted: " + (System.currentTimeMillis() - start) + "ms");

        // 3. TimestampRequest erzeugen
        start = System.currentTimeMillis();
        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        reqGen.setCertReq(true);
        TimeStampRequest request = reqGen.generate(
                new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.3"), // SHA-512 OID
                hash
        );
        byte[] tsqBytes = request.getEncoded();
        System.out.println("Generate TimestampRequest lasted: " + (System.currentTimeMillis() - start) + "ms");

        // 4. Anfrage an FreeTSA
        start = System.currentTimeMillis();
        byte[] tsrBytes = sendToFreeTSA(tsqBytes);
        System.out.println("FreeTSA-Anfrage dauerte: " + (System.currentTimeMillis() - start) + "ms");

        // 5. Dcc bauen und speichern
        //  pid ggf. bcrypt hashen
        String pidToSave = pid;
        if ("private".equalsIgnoreCase(status)) {
            pidToSave = passwordEncoder.encode(pid);
        }
        start = System.currentTimeMillis();
        Dcc dcc = Dcc.builder()
                .pid(pidToSave)
                .xmlBase64(xmlBase64)
                .signedTsrFile(tsrBytes)
                .isDccValid(true)
                .information(information)
                .status(status)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        dccRepository.save(dcc);
//        start = System.currentTimeMillis();
//        Dcc dcc = Dcc.builder()
//                .pid(pid)
//                .xmlBase64(xmlBase64)
//                .signedTsrFile(tsrBytes)
//                .isDccValid(true)
//                .information(information)
//                .status(status)
//                .user(user)
//                .createdAt(LocalDateTime.now())
//                .build();
//        dccRepository.save(dcc);
//        // 6. Wenn status == "private", pid mit ID überschreiben und nochmal speichern
//        if ("private".equalsIgnoreCase(status)) {
//            dcc.setPid(String.valueOf(dcc.getId()));  // id in String umwandeln
//            dcc = dccRepository.save(dcc);            // nochmal speichern
//        }
        return dcc;
    }


    public List<Dcc> findAllByUser(User user) {
        return dccRepository.findByUser(user);
    }

public boolean deleteByIdAndUserOrAdmin(String id, User user) {
    Optional<Dcc> dccOptional;

    if (user.getRole().equals("ADMIN")) {
        dccOptional = dccRepository.findById(id);
    } else {
        dccOptional = dccRepository.findByIdAndUser(id, user);
    }

    if (dccOptional.isPresent()) {
        dccRepository.delete(dccOptional.get());
        return true;
    }
    return false;
}
    public boolean deleteByPidAndUserOrAdmin(String pid, User user) {
        Optional<Dcc> dccOptional;

        if (user.getRole().equals("ADMIN")) {
            dccOptional = dccRepository.findByPid(pid);
        } else {
            dccOptional = dccRepository.findByPidAndUser(pid, user);
        }

        if (dccOptional.isPresent()) {
            dccRepository.delete(dccOptional.get());
            return true;
        }
        return false;
    }
    private byte[] sendToFreeTSA(byte[] tsqBytes) throws IOException {

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(customProperties.getProxyHost(), customProperties.getProxyPort()));
        URL url = new URL("https://freetsa.org/tsr");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/timestamp-query");
        conn.setRequestProperty("Content-Length", String.valueOf(tsqBytes.length));
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(tsqBytes);
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException("Fehler bei FreeTSA: " + conn.getResponseCode());
        }
        try (InputStream is = conn.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            is.transferTo(baos);
            return baos.toByteArray();
        }
    }
    public TimestampVerificationResult verifyTimestamp(byte[] data, byte[] tsrBytes, Principal principal) throws Exception {
        TimeStampResponse response = new TimeStampResponse(tsrBytes);

        // Hash des Originaldokuments berechnen
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hashedData = digest.digest(data);

        // Anfrage generieren für Verifikation
        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        reqGen.setCertReq(true);
        TimeStampRequest request = reqGen.generate(new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.3"), hashedData);

        // Gültigkeit prüfen
        response.validate(request);

        TimeStampToken token = response.getTimeStampToken();
        TimeStampTokenInfo info = token.getTimeStampInfo();

        // Ergebnis-Objekt füllen
        TimestampVerificationResult result = new TimestampVerificationResult();
        result.setGranted(response.getStatus() == 0);
        result.setStatusDescription(response.getStatusString() != null ? response.getStatusString() : "none");
        result.setTimestamp(info.getGenTime());
        result.setPolicy(info.getPolicy().getId());
        result.setSerialNumber(info.getSerialNumber().toString());
        result.setHashAlgorithm(info.getMessageImprintAlgOID().getId());
        result.setMessageImprint(Hex.toHexString(info.getMessageImprintDigest()));
        result.setTsa(token.getSID().getIssuer().toString()); // TSA Info (Issuer)

        return result;
    }

        public void changePassword (String userName, ChangePasswordRequest request){
            User user = userRepository.findByUserName(userName)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Old password is incorrect");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        }


//    @Override
//    public String searchRefType( String refType ) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {
////        boolean dcc= dccRepository.existsDccByPid(pid);
////        if(dcc){
//            //decode Base 64 and parse s xml
//            String xmlBase64= dccRepository.findDccByPid("CCM.M-K1-PTB9608").getXmlBase64();
//            byte[] byteBase64 = Base64.getDecoder().decode(xmlBase64);
//            String decodedXml = new String(byteBase64, StandardCharsets.UTF_8);
//           // System.out.println(decodedXml);
//
//            //parse the xml
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder= factory.newDocumentBuilder();
//            InputSource is = new InputSource(new StringReader(decodedXml));
//            Document document= builder.parse(is);
//
//            //search with XPath
//            String expression =  "/digitalCalibrationCertificate/measurementResults/measurementResult/results/result[@refType="+refType+"]";
////            String expression = "/digitalCalibrationCertificate/measurementResults/measurementResult/results/result[@refType=\"conventionalMass\"]/data/quantity[@refType="+ refType +"]";
//
//            XPath xPath= XPathFactory.newInstance().newXPath();
////            String result1 = (String) xPath.compile(expression).evaluate(document,XPathConstants.STRING);
////            System.out.println("fin"+ result1);
//            NodeList result = (NodeList) xPath.compile(expression).evaluate(document,XPathConstants.NODESET);
////            NodeList result = (NodeList) xPath.compile(expression ).evaluate(document,XPathConstants.NODESET);
//            Document resultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//            Element rootElement =resultDocument.createElement("dcc:digitalCalibrationCertificate");
//            resultDocument.appendChild(rootElement);
//            for (int i=0; i< result.getLength(); i++) {
//                Node element = result.item(i).cloneNode(true);
//                resultDocument.adoptNode(element);
//                rootElement.appendChild(element);
//            }
//            String resultXml =convertDocumentToString(resultDocument);
//            System.out.println("result: "+resultXml);
//            return resultXml;
////        }else
////            return "pid not exist";
//
//    }


    }
