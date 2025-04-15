package com.example.assignment2.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import static org.assertj.core.api.Assertions.assertThat;

class ThreadPoolTaskConfigTest {

    ThreadPoolTaskConfig config = new ThreadPoolTaskConfig();

    @Test
    void threadPoolTaskExecutorConfiguredSuccessfully() {
        // when
        ThreadPoolTaskExecutor executor = config.threadPoolTaskExecutor();

        // then
        assertThat(executor.getCorePoolSize()).isEqualTo(30);
        assertThat(executor.getMaxPoolSize()).isEqualTo(50);
        assertThat(executor.getThreadPoolExecutor().getQueue().remainingCapacity()).isEqualTo(100);
        assertThat(executor.getThreadPoolExecutor().getRejectedExecutionHandler())
                .isInstanceOf(java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.class);
    }
}
