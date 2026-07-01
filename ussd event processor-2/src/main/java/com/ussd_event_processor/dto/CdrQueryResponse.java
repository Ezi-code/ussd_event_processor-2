package com.ussd_event_processor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record CdrQueryResponse(
        @JsonProperty("RECORD_DATE")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime recordDate,

        @JsonProperty("MSISDN")
        String msisdn,

        @JsonProperty("IMSI")
        String imsi
) {
}
