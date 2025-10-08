package de.ptb.dsi.dcc_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimestampVerificationResult {
    private boolean granted;
    private String statusDescription;
    private Date timestamp;
    private String serialNumber;
    private String policy;
    private String hashAlgorithm;
    private String messageImprint;
    private String tsa;
}
