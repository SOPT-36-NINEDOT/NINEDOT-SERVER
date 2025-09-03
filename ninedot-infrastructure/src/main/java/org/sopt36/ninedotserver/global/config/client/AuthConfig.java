package org.sopt36.ninedotserver.global.config.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AuthConfig {

    @Bean(name = "authRestClient")
    public RestClient restClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 연결 타임아웃 5초 (5000ms) - 서버랑 연결하는데 5초 준다.
        factory.setReadTimeout(10000);   // 읽기 타임아웃 10초 (10000ms) - 데이터 불러오는데 10초 준다.
        return RestClient.builder()
            .requestFactory(factory)
            .build();
    }
}
