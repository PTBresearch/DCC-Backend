package de.ptb.dsi.dcc_backend.service;

import de.ptb.dsi.dcc_backend.repository.DccRepository;
import de.ptb.dsi.dcc_backend.model.Dcc;
import lombok.AllArgsConstructor;
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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class DccServiceImpl implements DccService {
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
    public List<String> getListDccPid() {
        List<Dcc> dccList = dccRepository.findAll();
        List<String> pidList = dccList.stream()
//                .map(pid -> "https://d-si.ptb.de/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());
                  .map(pid -> "http://localhost:8085/api/d-dcc/dcc/" + pid.getPid()).collect(Collectors.toList());

        return pidList;
    }
    @Override
    public List<String> getListPid() {
        List<Dcc> dccList = dccRepository.findAll();
        List<String> pidList = dccList.stream()
                .map(pid ->  pid.getPid()).collect(Collectors.toList());
        return pidList;
    }

    @Override
    public String findNodeByRefType(String pid, String refType) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        boolean existsDccByPid = dccRepository.existsDccByPid(pid);
        if (existsDccByPid) {
            //decode Base 64 and parse  xml
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
