package de.ptb.dsi.dcc_backend.service;


import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

@Service
public class TimestampService {

    public byte[] createTimestampRequest(byte[] data) throws Exception {
        // SHA-256 Hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedData = digest.digest(data);

        // Request
        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        reqGen.setCertReq(true);

        TimeStampRequest request = reqGen.generate(
                new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.1"), // SHA-256 OID
                hashedData
        );

        return request.getEncoded();
    }

    public byte[] sendToFreeTSA(byte[] tsqBytes) throws Exception {
        URL url = new URL("https://freetsa.org/tsr");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/timestamp-query");
        conn.setRequestProperty("Content-Length", String.valueOf(tsqBytes.length));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(tsqBytes);
        }

        try (InputStream is = conn.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            is.transferTo(baos);
            return baos.toByteArray(); // .tsr Datei
        }
    }

    public boolean verifyTimestamp(byte[] data, byte[] tsrBytes) throws Exception {
        TimeStampResponse response = new TimeStampResponse(tsrBytes);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedData = digest.digest(data);

        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        reqGen.setCertReq(true);
        TimeStampRequest request = reqGen.generate(
                new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.1"), hashedData);

        response.validate(request);
        return response.getStatus() == 0; // 0 = granted
    }
}
