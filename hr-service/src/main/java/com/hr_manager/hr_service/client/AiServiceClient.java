package com.hr_manager.hr_service.client;

import com.hr_manager.hr_service.dto.PolishRequest;
import com.hr_manager.hr_service.dto.PolishResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service", url = "${ai-service.url}")
public interface AiServiceClient {

    @PostMapping("/polish")
    PolishResponse polishText(@RequestBody PolishRequest request);
}

