package com.example.assignment2.domain.company.service;

import com.example.assignment2.domain.company.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CompanyService companyService;

    @Test
    void makeStringHttpEntitySuccess() {
        // given
        String referer = "https://example.com";

        // when
        HttpEntity<String> httpEntity = companyService.makeStringHttpEntity(referer);
        HttpHeaders headers = httpEntity.getHeaders();

        // then
        assertThat(headers.getFirst("User-Agent")).isEqualTo("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
        assertThat(headers.getFirst("Referer")).isEqualTo(referer);
        assertThat(headers.getFirst("Accept")).isEqualTo("*/*");
        assertThat(headers.getFirst("Accept-Encoding")).isEqualTo("gzip, deflate, br, zstd");
        assertThat(headers.getFirst("Accept-Language")).isEqualTo("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
    }

}