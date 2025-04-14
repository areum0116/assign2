package com.example.assignment2.domain.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrnoResponse {
    private String totalCount;
    private List<Item> items;
}
