package pk.backend.infrastructure.restClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AirQualityRestClient {

    @Value("${air-quality.api.base-url}")
    private String baseUrl;

    @Bean
    public RestClient airQualityRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
