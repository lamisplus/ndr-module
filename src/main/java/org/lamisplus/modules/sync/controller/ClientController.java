package org.lamisplus.modules.sync.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import liquibase.pro.packaged.O;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.sync.service.ObjectSerializer;
import org.lamisplus.modules.sync.utility.HttpConnectionManager;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync")
public class ClientController {
    public enum Tables { patient, visit, encounter, form_data, appointment, biometric };;
    private final ObjectSerializer objectSerializer;
    private final ObjectMapper mapper = new ObjectMapper();

    public ResponseEntity<String> sender() throws JSONException {
        long facilityId = 1L;
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            for (Tables table : Tables.values()) {
                List<Object> objects = objectSerializer.serialize(table.name(), facilityId);
                // Convert object to JSON string and post to the server url
                String pathVariable = table.name().concat("/").concat(Long.toString(facilityId));
                String response = new HttpConnectionManager().post(mapper.writeValueAsString(objects), "org.lamisplus/api/sync/" + pathVariable);
                System.out.println("Response from server: "+response);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Successful");
    }
}