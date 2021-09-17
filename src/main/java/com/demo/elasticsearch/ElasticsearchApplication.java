package com.demo.elasticsearch;

import com.demo.elasticsearch.Model.CictecLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@Slf4j
public class ElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
        log.info(CictecLogUtil.logResult("esService", "服务已经启动..."));
    }

}
