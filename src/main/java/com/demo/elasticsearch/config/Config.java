package com.demo.elasticsearch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author jky
 * @Time 2019/8/5 16:43
 * @Description
 */

@Component
@ConfigurationProperties(prefix = "config")
@Data
public class Config {

    private int maxPageSize;

    private int defaultPageNum = 1;

}
