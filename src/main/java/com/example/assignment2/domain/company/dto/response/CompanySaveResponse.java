package com.example.assignment2.domain.company.dto.response;

import com.example.assignment2.domain.company.dto.request.CompanySaveRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanySaveResponse {
    private int statusCode;
    private String message;
    private List<CompanyDto> body;
}
