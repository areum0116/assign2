package com.example.assignment2.domain.company.service;

import com.example.assignment2.domain.company.dto.request.CompanySaveRequest;
import com.example.assignment2.domain.company.dto.response.CompanyDto;
import com.example.assignment2.domain.company.dto.response.CompanySaveResponse;
import com.example.assignment2.domain.company.dto.response.CrnoResponse;
import com.example.assignment2.domain.company.dto.response.JusoResponse;
import com.example.assignment2.domain.company.entity.Company;
import com.example.assignment2.domain.company.repository.CompanyRepository;
import com.example.assignment2.manager.StringCustomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private static final String REFERER_URL = "https://www.ftc.go.kr/www/selectBizCommOpenList.do?key=255";
    private static final String BASE_URL = "https://www.ftc.go.kr/www/downloadBizComm.do";
    private static final String DOWNLOAD_PATH = "C:/Users/arkim/Workspace/assignment2/src/main/resources/static/download/";
    private final CompanyRepository companyRepository;
    private final RestTemplate restTemplate;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Value("${api.fair-trade-commission.base-url}")
    private String fairTradeUrl;
    @Value("${api.fair-trade-commission.key}")
    private String fairTradeKey;
    @Value("${api.juso.base-url}")
    private String jusoUrl;
    @Value("${api.juso.key}")
    private String jusoKey;

    @Transactional
    public CompanySaveResponse saveCompany(CompanySaveRequest request) {
        File file = downloadBizComm(request);
        List<CompanyDto> companyDtoList;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String header = reader.readLine();
            if (StringCustomManager.isEmptyString(header)) {
                throw new RuntimeException("No header found");
            }
            String[] headers = header.split(",");
            int telSalesNumIdx = Arrays.asList(headers).indexOf("통신판매번호");
            int companyNameIdx = Arrays.asList(headers).indexOf("상호");
            int corpYNIdx = Arrays.asList(headers).indexOf("법인여부");
            int brnoIdx = Arrays.asList(headers).indexOf("사업자등록번호");
            int jusoIdx = Arrays.asList(headers).indexOf("사업장소재지");
            if (telSalesNumIdx < 0 || companyNameIdx < 0 || brnoIdx < 0 || jusoIdx < 0) {
                throw new RuntimeException("Invalid header");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String finalLine = line;
                threadPoolTaskExecutor.execute(() -> {
                    try {
                        String[] fields = finalLine.split(",");
                        if (fields.length <= telSalesNumIdx || fields.length <= companyNameIdx || fields.length <= brnoIdx
                                || fields.length <= jusoIdx || fields.length <= corpYNIdx) {
                            return;
                        }
                        if (fields[corpYNIdx].equals("개인")) {
                            return;
                        }
                        String telSalesNum = fields[telSalesNumIdx].trim();
                        String companyName = fields[companyNameIdx].trim();
                        String brno = fields[brnoIdx].replace("-", "").trim();
                        String[] jusoParts = fields[jusoIdx].split(" ");
                        if(jusoParts.length < 3) {
                            return;
                        }
                        String juso = jusoParts[0] + jusoParts[1] + jusoParts[2];

                        String crno = getCrnoMatchingBrno(brno);
                        String admCd = getAdmCdMatchingJuso(juso);

                        Company company = new Company(telSalesNum, companyName, brno, crno, admCd);
                        saveCompanyData(company);
                    } catch (Exception e) {
                        log.error("비동기 저장 중 예외 발생", e);
                    }
                });
            }
            threadPoolTaskExecutor.shutdown();
            if (threadPoolTaskExecutor.isRunning()) {
                throw new RuntimeException("Executor did not terminate");
            }
            List<Company> companyList = companyRepository.findAll();
            companyDtoList = companyList.stream().map(CompanyDto::new).collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return new CompanySaveResponse(200, "Saved Successfully", companyDtoList);
    }

    public HttpEntity<String> makeStringHttpEntity(String referer) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
        headers.set("Referer", referer);
        headers.set("Accept", "*/*");
        headers.set("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        return new HttpEntity<>(headers);
    }

    private File downloadBizComm(CompanySaveRequest requestDto) {
        String encodedFileName = URLEncoder.encode("통신판매사업자_" + requestDto.getCity() + "_" + requestDto.getDistrict() + ".csv", StandardCharsets.UTF_8);
        URI url = null;
        try {
            url = new URI(BASE_URL + "?atchFileUrl=dataopen&atchFileNm=" + encodedFileName);
        } catch (URISyntaxException ignored) {}
        HttpEntity<String> entity = makeStringHttpEntity(REFERER_URL);
        assert url != null;
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("파일 다운로드 실패 : " + response.getStatusCode());
        }
        String fileName = "company_" + requestDto.getCity() + "_" + requestDto.getDistrict() + ".csv";
        Path path = Paths.get(DOWNLOAD_PATH, fileName);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
            }

            String encodedFileData = new String(Objects.requireNonNull(response.getBody()), "euc-kr");
            Files.writeString(path, encodedFileData, StandardOpenOption.CREATE);
            return path.toFile();
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 중 오류: " + e.getMessage());
        }
    }

    private String getCrnoMatchingBrno(String brno) {
        String url = UriComponentsBuilder.fromUriString(fairTradeUrl)
                .queryParam("serviceKey", fairTradeKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1)
                .queryParam("resultType", "json")
                .queryParam("brno", brno)
                .build()
                .toUriString();
        CrnoResponse response = restTemplate.getForEntity(url, CrnoResponse.class).getBody();
        if (response == null || response.getItems().isEmpty()) {
            return null;
        }
        return response.getItems().get(0).getCrno();
    }

    private String getAdmCdMatchingJuso(String juso) {
        String url = UriComponentsBuilder.fromUriString(jusoUrl)
                .queryParam("confmKey", jusoKey)
                .queryParam("currentPage", 1)
                .queryParam("countPerPage", 1)
                .queryParam("keyword", juso)
                .queryParam("resultType", "json")
                .build()
                .toUriString();
        JusoResponse response = restTemplate.getForEntity(url, JusoResponse.class).getBody();
        if (response == null || response.getResults().getJuso() == null || response.getResults().getJuso().isEmpty()) {
            return "N/A";
        }
        return response.getResults().getJuso().get(0).getAdmCd();
    }

    @Transactional
    public void saveCompanyData(Company company) {
        companyRepository.save(company);
    }
}