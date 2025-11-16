package io.proj3ct.SpringDemoBot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // Включаем асинхронную обработку
public class AsyncConfig {

    /**
     * Определяем наш пул потоков для обработки логики бота.
     * Мы назвали его "taskExecutor", как и в @Async("taskExecutor")
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);   // 5 потоков будут жить всегда
        executor.setMaxPoolSize(20);  // до 20 потоков может быть создано под нагрузкой
        executor.setQueueCapacity(100); // 100 задач могут ждать в очереди
        executor.setThreadNamePrefix("BotAsync-");
        executor.initialize();
        System.out.println("SHA KONCHU");
        return executor;
    }
}