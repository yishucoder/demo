package com.example.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AlipayConfig {
    @Value("${alipay.serverUrl}")
    private String serverUrl;
    @Value("${alipay.appId}")
    private String appId;
    @Value("${alipay.privateKey}")
    private String privateKey;
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;
    @Value("${alipay.alipaypProductCode}")
    private String alipaypProductCode;
    @Value("${alipay.return_url}")
    private String returnUrl;
    @Value("${alipay.notify_url}")
    private String notifyUrl;


}
