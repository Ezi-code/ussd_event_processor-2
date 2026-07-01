package com.ussd_event_processor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record CdrQueryRequest(
        @JsonProperty("record_date_start")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime recordDateStart,

        @JsonProperty("record_date_end")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime recordDateEnd,

        String msisdn,
        String imsi
) {
}
