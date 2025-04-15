package com.example.assignment2.domain.company.controller;

import com.example.assignment2.domain.company.dto.request.CompanySaveRequest;
import com.example.assignment2.domain.company.dto.response.CompanySaveResponse;
import com.example.assignment2.domain.company.service.CompanyService;
import com.example.assignment2.domain.company.service.CompanyService2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyService2 companyService2;

    @PostMapping("/companies")
    public CompanySaveResponse saveCompany(@RequestBody @Valid CompanySaveRequest requestDto) {
        return companyService.saveCompany(requestDto);
    }

    @PostMapping("/companies2")
    public CompanySaveResponse saveCompany2(@RequestBody @Valid CompanySaveRequest requestDto) {
        return companyService2.saveCompany(requestDto);
    }
}
