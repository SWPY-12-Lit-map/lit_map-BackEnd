package com.lit_map_BackEnd.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 시간 Entity를 상속받아 사용할 수 있도록 config 설정 가능
 */
@Configuration
@EnableJpaAuditing
public class BaseTimeConfig {
}
