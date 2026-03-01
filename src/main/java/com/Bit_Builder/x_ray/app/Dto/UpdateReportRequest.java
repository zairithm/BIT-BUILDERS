package com.Bit_Builder.x_ray.app.Dto;

import com.Bit_Builder.x_ray.app.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReportRequest {
    private String doctorNotes;
    private String diagnosis;
    private Status status;
}
