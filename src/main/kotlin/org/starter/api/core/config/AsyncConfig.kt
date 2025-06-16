package org.starter.api.core.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

/**
 * 자바에서 비동기 프로그래밍(멀티스레드) 해주는 Config
 */
@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 20 // 기본 실행 대기 쓰레드 숫자
        executor.maxPoolSize = 40 // 동시 동작하는 쓰레드 숫자
        executor.queueCapacity = 100 // 쓰레드가 넘쳤을때 자동으로 요청을 큐에 저장하는데 최대 수용되는 큐 숫자
        executor.setThreadNamePrefix("ASYNC-") // 쓰레드 접두사
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy()) // 처리 실패 시 상위 스레드에서 직접 처리
        executor.initialize()
        return executor
    }
}
