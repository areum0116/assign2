package com.example.assignment2.domain.company.dto.response;

import com.example.assignment2.domain.company.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyDto {
    private String telSalesNum;
    private String companyName;
    private String busRegistrationNum;
    private String corRegistrationNum;
    private String adDistrictCode;

    public CompanyDto(Company company) {
        telSalesNum = company.getTelSalesNum();
        companyName = company.getCompanyName();
        busRegistrationNum = company.getBusRegistrationNum();
        corRegistrationNum = company.getCorRegistrationNum();
        adDistrictCode = company.getAdDistrictCode();
    }
}