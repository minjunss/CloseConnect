package com.CloseConnect.closeconnect.global.config.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Slf4j
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    @Autowired
    @Qualifier("findDeletedRoomAndDeleteChatJob")
    private Job findDeletedRoomAndDeleteChatJob;

    @Scheduled(cron = "0 45 3 * * *")
    public void chatBatchRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters parameters = new JobParametersBuilder()
                .addString("jobName", "findDeletedRoomAndDeleteChatJob" + System.currentTimeMillis())
                        .toJobParameters();
        jobLauncher.run(findDeletedRoomAndDeleteChatJob, parameters);

    }
}
