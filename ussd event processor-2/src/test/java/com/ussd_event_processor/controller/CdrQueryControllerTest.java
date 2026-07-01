package com.ussd_event_processor.controller;

import com.ussd_event_processor.dto.CdrQueryResponse;
import com.ussd_event_processor.repository.CallDetailRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CdrQueryController.class)
class CdrQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CallDetailRecordRepository repository;

    @Test
    void queryByDateRangeOnly() throws Exception {
        var start = LocalDateTime.of(2023, 8, 18, 10, 30, 0);
        var end = LocalDateTime.of(2023, 8, 18, 10, 31, 0);
        var records = List.of(
                new CdrQueryResponse(start, "573228550000", "1234567890")
        );
        when(repository.findRecords(eq(start), eq(end), eq(null), eq(null))).thenReturn(records);

        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "record_date_start": "2023-08-18 10:30:00",
                                    "record_date_end": "2023-08-18 10:31:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].RECORD_DATE").value("2023-08-18 10:30:00"))
                .andExpect(jsonPath("$[0].MSISDN").value("573228550000"))
                .andExpect(jsonPath("$[0].IMSI").value("1234567890"));
    }

    @Test
    void queryByDateRangeOnlyIsoFormat() throws Exception {
        var start = LocalDateTime.of(2023, 8, 18, 10, 30, 0);
        var end = LocalDateTime.of(2023, 8, 18, 10, 31, 0);
        var records = List.of(
                new CdrQueryResponse(start, "573228550000", "1234567890")
        );
        when(repository.findRecords(eq(start), eq(end), eq(null), eq(null))).thenReturn(records);

        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "record_date_start": "2023-08-18T10:30:00",
                                    "record_date_end": "2023-08-18T10:31:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void queryByDateRangeOnlyIsoWithMillisAndZ() throws Exception {
        var start = LocalDateTime.of(2023, 8, 18, 10, 30, 0);
        var end = LocalDateTime.of(2023, 8, 18, 10, 31, 0);
        var records = List.of(
                new CdrQueryResponse(start, "573228550000", "1234567890")
        );
        when(repository.findRecords(eq(start), eq(end), eq(null), eq(null))).thenReturn(records);

        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "record_date_start": "2023-08-18T10:30:00.000Z",
                                    "record_date_end": "2023-08-18T10:31:00.000Z"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void queryWithMsisdn() throws Exception {
        var start = LocalDateTime.of(2023, 8, 18, 10, 30, 0);
        var end = LocalDateTime.of(2023, 8, 18, 10, 31, 0);
        var records = List.of(
                new CdrQueryResponse(start, "573228550000", "1234567890")
        );
        when(repository.findRecords(eq(start), eq(end), eq("573228550000"), eq(null))).thenReturn(records);

        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "record_date_start": "2023-08-18 10:30:00",
                                    "record_date_end": "2023-08-18 10:31:00",
                                    "msisdn": "573228550000"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].MSISDN").value("573228550000"));
    }

    @Test
    void queryWithImsi() throws Exception {
        var start = LocalDateTime.of(2023, 8, 18, 10, 30, 0);
        var end = LocalDateTime.of(2023, 8, 18, 10, 31, 0);
        var records = List.of(
                new CdrQueryResponse(start, "573228550000", "1234567890")
        );
        when(repository.findRecords(eq(start), eq(end), eq(null), eq("1234567890"))).thenReturn(records);

        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "record_date_start": "2023-08-18 10:30:00",
                                    "record_date_end": "2023-08-18 10:31:00",
                                    "imsi": "1234567890"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].IMSI").value("1234567890"));
    }

    @Test
    void queryWithAllParams() throws Exception {
        var start = LocalDateTime.of(2023, 8, 18, 10, 30, 0);
        var end = LocalDateTime.of(2023, 8, 18, 10, 31, 0);
        var records = List.of(
                new CdrQueryResponse(start, "573228550000", "1234567890")
        );
        when(repository.findRecords(eq(start), eq(end), eq("573228550000"), eq("1234567890"))).thenReturn(records);

        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "record_date_start": "2023-08-18 10:30:00",
                                    "record_date_end": "2023-08-18 10:31:00",
                                    "msisdn": "573228550000",
                                    "imsi": "1234567890"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].RECORD_DATE").value("2023-08-18 10:30:00"))
                .andExpect(jsonPath("$[0].MSISDN").value("573228550000"))
                .andExpect(jsonPath("$[0].IMSI").value("1234567890"));
    }

    @Test
    void queryReturnsEmptyArrayWhenNoRecords() throws Exception {
        var start = LocalDateTime.of(2023, 8, 18, 10, 30, 0);
        var end = LocalDateTime.of(2023, 8, 18, 10, 31, 0);
        when(repository.findRecords(eq(start), eq(end), eq(null), eq(null))).thenReturn(List.of());

        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "record_date_start": "2023-08-18 10:30:00",
                                    "record_date_end": "2023-08-18 10:31:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void returnsBadRequestWhenRecordDateStartIsNull() throws Exception {
        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"record_date_end": "2023-08-18 10:31:00"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestWhenRecordDateEndIsNull() throws Exception {
        mockMvc.perform(post("/api/cdrs/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"record_date_start": "2023-08-18 10:30:00"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
