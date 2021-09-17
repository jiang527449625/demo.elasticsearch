package com.demo.elasticsearch.config;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyJestClient {
    @Bean
    public JestClient getJestCline(){
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://192.168.1.40:9200")//自己elasticsearch安装位置的IP地址
                .connTimeout(60000)
                .readTimeout(60000)
                .multiThreaded(true)
                .build());
        return factory.getObject();
    }

}
