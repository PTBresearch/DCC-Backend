package de.ptb.dsi.dcc_backend.service;

import de.ptb.dsi.dcc_backend.model.Dcc;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

public interface DccService {

    List<Dcc> getDccList();
    Dcc getDccByPid(String pid);
    Boolean isDccValid(String pid);
    Dcc saveDcc(Dcc dcc);
    String getBase64XmlByPid(String pid);
    List<String> getListDccPid();
//    String searchRefType(String refType ) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException;
    String findNodeByRefType(String pid ,String attributeValue) throws ParserConfigurationException, IOException, SAXException, TransformerException;
//    String convertDocumentToString(Document document) throws TransformerException;
    String nodeToString(Node node) throws TransformerException;


}
