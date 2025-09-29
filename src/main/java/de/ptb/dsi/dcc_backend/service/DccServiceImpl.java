package de.ptb.dsi.dcc_backend.service;

import de.ptb.dsi.dcc_backend.dto.ChangePasswordRequest;
import de.ptb.dsi.dcc_backend.entity.User;
import de.ptb.dsi.dcc_backend.exception.DccAlreadyExistsException;
import de.ptb.dsi.dcc_backend.exception.UserAlreadyExistsException;
import de.ptb.dsi.dcc_backend.exception.UserNotFoundException;
import de.ptb.dsi.dcc_backend.repository.DccRepository;
import de.ptb.dsi.dcc_backend.entity.Dcc;
import de.ptb.dsi.dcc_backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class DccServiceImpl implements DccService {
    private final DccRepository dccRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Override
    public List<Dcc> getDccList() {
        return dccRepository.findAll();
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

    @Override
    public List<String> getUrlListDccPid() {
        List<Dcc> dccList = dccRepository.findAll();
        List<String> pidList = dccList.stream()
                .map(pid -> "https://d-si.ptb.de/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());
//                  .map(pid -> "http://localhost:8085/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());

        return pidList;
    }
    public List<String> getListPid(Principal principal) {
        if ("Admin".equals(principal.getName())) {
            List<Dcc> dccList = dccRepository.findAll();
            return dccList.stream()
                    .map(Dcc::getPid)
                    .collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("Only admin role has authentication to access this resource.");
        }
    }

    public List<String> getPublicListPid() {
        List<Dcc> publicDccList = dccRepository.findByStatus("public");

        List<String> publicPidList = publicDccList.stream().map(pid -> pid.getPid()).collect(Collectors.toList());
        return publicPidList;
    }
    public List<String> getPrivateListPid() {
        List<Dcc> privateDccList = dccRepository.findByStatus("private");

        List<String> privatePidList = privateDccList.stream().map(pid -> pid.getPid()).collect(Collectors.toList());
        return privatePidList;
    }
    public List<String> getCoordinatorListPid( Principal principal) {

        List<Dcc> coordinatorDccList = dccRepository.findDccsByUser_UserName(principal.getName());

        return coordinatorDccList .stream()
                .map(Dcc::getPid)
                .collect(Collectors.toList());
    }

    public User addUser(User user){
        if(userRepository.existsUserByUserName(user.getUserName())){
            throw new UserAlreadyExistsException(user.getUserName());
        }
        User newUser= User.builder().
                userName(user.getUserName()).
                email(user.getEmail()).
                password(user.getPassword()).
                role(user.getRole()).
                isActiv(user.isActiv()).
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
    public List<String> getPublicCoordinatorListPid( Principal principal) {
        List<String> publicCoordinatorDccList = new ArrayList<>();
        List<String>  coordinatorDccListPid =getCoordinatorListPid(principal);
        List<String>  publicDccListPid = getPublicListPid();
        publicCoordinatorDccList.addAll(publicDccListPid);
        publicCoordinatorDccList.addAll(coordinatorDccListPid);
        return publicCoordinatorDccList ;
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

    public String getBase64EncodedXml(String pid, User user) {
        Dcc dcc = dccRepository.findByPidAndUser(pid, user)
                .orElseThrow(() -> new RuntimeException("DCC not found"));
        return dcc.getXmlBase64();
    }
    public Dcc processAndSaveDcc(String pid, String information,String status, byte[] xmlBytes) throws Exception {
        if (dccRepository.existsDccByPid(pid)) {
            throw new DccAlreadyExistsException(pid);
        }
        long startTotal = System.currentTimeMillis();

        // 1. XML in Base64
        long start = System.currentTimeMillis();
        String xmlBase64 = Base64.getEncoder().encodeToString(xmlBytes);
        System.out.println("Base64 Encoding dauerte: " + (System.currentTimeMillis() - start) + "ms");

        // 2. SHA-512 Hash
        start = System.currentTimeMillis();
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(xmlBytes);
        System.out.println("Hashing dauerte: " + (System.currentTimeMillis() - start) + "ms");

        // 3. TimestampRequest erzeugen
        start = System.currentTimeMillis();
        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        reqGen.setCertReq(true);
        TimeStampRequest request = reqGen.generate(
                new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.3"), // SHA-512 OID
                hash
        );
        byte[] tsqBytes = request.getEncoded();
        System.out.println("TimestampRequest erzeugen dauerte: " + (System.currentTimeMillis() - start) + "ms");

        // 4. Anfrage an FreeTSA
        start = System.currentTimeMillis();
        byte[] tsrBytes = sendToFreeTSA(tsqBytes);
        System.out.println("FreeTSA-Anfrage dauerte: " + (System.currentTimeMillis() - start) + "ms");

        // 5. Dcc bauen und speichern
        start = System.currentTimeMillis();
        Dcc dcc = Dcc.builder()
                .pid(pid)
                .xmlBase64(xmlBase64)
                .signedTsrFile(tsrBytes)
                .isDccValid(true)
                .information(information)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        dccRepository.save(dcc);
        return dcc;
    }

//    public Dcc processAndSaveDcc(String pid, byte[] xmlBytes, User user) throws Exception {
//        if (dccRepository.existsDccByPid(pid)) {
//            throw new DccAlreadyExistsException(pid);
//        }
//        long startTotal = System.currentTimeMillis();
//
//        // 1. XML in Base64
//        long start = System.currentTimeMillis();
//        String xmlBase64 = Base64.getEncoder().encodeToString(xmlBytes);
//        System.out.println("Base64 Encoding dauerte: " + (System.currentTimeMillis() - start) + "ms");
//
//        // 2. SHA-512 Hash
//        start = System.currentTimeMillis();
//        MessageDigest digest = MessageDigest.getInstance("SHA-512");
//        byte[] hash = digest.digest(xmlBytes);
//        System.out.println("Hashing dauerte: " + (System.currentTimeMillis() - start) + "ms");
//
//        // 3. TimestampRequest erzeugen
//        start = System.currentTimeMillis();
//        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
//        reqGen.setCertReq(true);
//        TimeStampRequest request = reqGen.generate(
//                new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.3"), // SHA-512 OID
//                hash
//        );
//        byte[] tsqBytes = request.getEncoded();
//        System.out.println("TimestampRequest erzeugen dauerte: " + (System.currentTimeMillis() - start) + "ms");
//
//        // 4. Anfrage an FreeTSA
//        start = System.currentTimeMillis();
//        byte[] tsrBytes = sendToFreeTSA(tsqBytes);
//        System.out.println("FreeTSA-Anfrage dauerte: " + (System.currentTimeMillis() - start) + "ms");
//
//        // 5. Dcc bauen und speichern
//        start = System.currentTimeMillis();
//        Dcc dcc = Dcc.builder()
//                .pid(pid)
//                .xmlBase64(xmlBase64)
//                .signedTsrFile(tsrBytes)
//                .isDccValid(true)
//                .status("private")
//                .user(user)
//                .createdAt(LocalDateTime.now())
//                .build();
//        dccRepository.save(dcc);
//        return dcc;
//    }

    public List<Dcc> findAllByUser(User user) {
        return dccRepository.findByUser(user);
    }
//    @Async
//    public CompletableFuture<Dcc> processAndSaveDccAsync(String pid, byte[] xmlBytes, User user) {
//        try {
//            Dcc dcc = processAndSaveDcc(pid, xmlBytes, user);
//            return CompletableFuture.completedFuture(dcc);
//        } catch (Exception e) {
//            // Fehlerbehandlung hier
//            return CompletableFuture.failedFuture(e);
//        }
//    }

    public void deleteByIdAndUser(String id, User user) {
        dccRepository.deleteByIdAndUser(id, user);
    }

    private byte[] sendToFreeTSA(byte[] tsqBytes) throws IOException {

        URL url = new URL("https://freetsa.org/tsr");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/timestamp-query");
        conn.setRequestProperty("Content-Length", String.valueOf(tsqBytes.length));
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("webproxy.bs.ptb.de", 8080));
//         conn = (HttpURLConnection) url.openConnection(proxy);
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
    public boolean verifyTimestamp(byte[] data, byte[] tsrBytes) throws Exception {
        TimeStampResponse response = new TimeStampResponse(tsrBytes);
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hashedData = digest.digest(data);

        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        reqGen.setCertReq(true);
        TimeStampRequest request = reqGen.generate(
                new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.3"), hashedData);

        response.validate(request);
        return response.getStatus() == 0; // 0 = granted
    }
    public void changePassword(String userName, ChangePasswordRequest request) {
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


//    @Override
//    public  String convertDocumentToString(Document document) throws TransformerException {
//        TransformerFactory tf =TransformerFactory.newInstance();
//        Transformer transformer= tf.newTransformer();
//        StringWriter writer =new StringWriter();
//        transformer.transform(new DOMSource(document),new StreamResult(writer));
//
//        return writer.getBuffer().toString();
//    }


}
