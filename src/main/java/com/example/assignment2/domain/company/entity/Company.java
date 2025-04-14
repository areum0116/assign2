package com.example.assignment2.domain.company.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="company_id")
    private Long id;

    private String telSalesNum;     // 통신 판매 번호
    private String companyName;     // 상호
    private String busRegistrationNum;  // 사업자등록번호
    private String corRegistrationNum;  // 법인등록번호
    private String adDistrictCode;      // 행정구역코드

    public Company(String telSalesNum, String companyName, String busRegistrationNum, String corRegistrationNum, String adDistrictCode) {
        this.telSalesNum = telSalesNum;
        this.companyName = companyName;
        this.busRegistrationNum = busRegistrationNum;
        this.corRegistrationNum = corRegistrationNum;
        this.adDistrictCode = adDistrictCode;
    }
}
