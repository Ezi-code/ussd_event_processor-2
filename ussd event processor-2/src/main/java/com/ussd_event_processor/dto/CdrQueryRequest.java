package com.ussd_event_processor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CdrQueryRequest(
        @JsonProperty("record_date_start")
        String recordDateStart,

        @JsonProperty("record_date_end")
        String recordDateEnd,

        String msisdn,
        String imsi
) {
}
