package com.CloseConnect.closeconnect.global.config.batch;

import com.CloseConnect.closeconnect.repository.chat.ChatMessageRepository;
import com.CloseConnect.closeconnect.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class ChatBatchConfig {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;


    @Bean
    public Job findDeletedRoomAndDeleteChatJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("findDeletedRoomAndDeleteChatJob", jobRepository)
                .start(deleteChatStep(jobRepository, platformTransactionManager, chatRoomReader(), chatMessageItemProcessor(), chatMessageItemWriter()))
                .build();
    }

    @Bean
    public Step deleteChatStep(JobRepository jobRepository,
                               PlatformTransactionManager platformTransactionManager,
                               ChatRoomReader reader,
                               ChatMessageItemProcessor processor,
                               ChatMessageItemWriter writer) {
        return new StepBuilder("deleteChatStep", jobRepository)
                .chunk(1, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public ChatRoomReader chatRoomReader() {
        return new ChatRoomReader(chatRoomRepository);
    }

    @Bean
    ChatMessageItemProcessor chatMessageItemProcessor() {
        return new ChatMessageItemProcessor(chatMessageRepository);
    }

    @Bean
    ChatMessageItemWriter chatMessageItemWriter() {
        return new ChatMessageItemWriter(chatMessageRepository);
    }

}
