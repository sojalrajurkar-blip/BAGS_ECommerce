package com.rora.backend.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimelineEventDto {

    private Long id;
    private String stepName;

    @JsonProperty("step")
    public String getStep() {
        return stepName;
    }

    private boolean completed;
    private String eventTime;

    @JsonProperty("time")
    public String getTime() {
        return eventTime;
    }

    @JsonProperty("date")
    public String getDate() {
        return eventTime;
    }

    private String title;
    private String description;
    private int displayOrder;
}
