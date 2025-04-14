package com.example.assignment2.domain.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanySaveRequest {
    @NotBlank
    private String city;
    @NotBlank
    private String district;
}
