package com.pls.ltlrating.batch.analysis;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pls.ltlrating.batch.analysis.model.AnalysisItem;
import com.pls.ltlrating.dao.analysis.FAFinancialAnalysisDao;
import com.pls.ltlrating.domain.enums.FinancialAnalysisStatus;
import com.pls.ltlrating.service.analysis.FreightAnalysisImportExportService;

/**
 * Implementation of {@link AnalysisJobOperator}.
 * TODO generate file for paused analysis which is not processed currently
 *
 * @author Aleksandr Leshchenko
 */
@Component("analysisJobOperator")
public class AnalysisJobOperatorImpl implements InitializingBean, JobExecutionListener, AnalysisJobOperator,
        ApplicationListener<ContextClosedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(AnalysisJobOperatorImpl.class);
    private static final String JOB_NAME = "analysePricing";

    @Value("${analysis.maxParallelJobsCount}")
    private volatile long maxParallelJobsCount;

    @Qualifier(JOB_NAME)
    @Autowired
    private Job job;

    @Qualifier("analysisJobLauncher")
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private FAFinancialAnalysisDao dao;

    @Autowired
    FreightAnalysisImportExportService service;

    private static volatile ScheduledFuture<?> schedule;

    @Override
    @Transactional
    public void afterPropertiesSet() throws Exception {
        LOG.info("Analysis Job Operator is initialized");
        Set<JobExecution> executions = jobExplorer.findRunningJobExecutions(JOB_NAME);
        if (!executions.isEmpty()) {
            LOG.warn("Found {} executions that need to be marked as failed: {}", executions.size(),
                    executions.stream().map(item -> item.getId().toString()).collect(Collectors.joining(", ")));
            executions.forEach(e -> {
                e.setStatus(BatchStatus.FAILED);
                e.setExitStatus(ExitStatus.FAILED);
                e.setEndTime(new Date());
                jobRepository.update(e);
            });
        }
        
        LOG.info("Delete previous Incomplete Analysis Details");
        dao.deleteIncorrectOutputDetails();
        
        LOG.info("Start Anlysis Jobs with Delay");
        runAnalysisDelayed(120); // launch analysis jobs after application has started, initialized caches etc.
    }

    @PreDestroy
    private void shutdown() {
        // changing this setting will allow not to run any new analysis jobs
        maxParallelJobsCount = Long.MIN_VALUE;
    }

    private static final Set<String> RUNNING_JOBS = new HashSet<String>();

    @Override
    public synchronized void startAnalysis() throws Exception {
        schedule = null;
        List<AnalysisItem> analysisItems = getNextAnalysisItemsForProcessing();
        if (analysisItems.isEmpty()) {
        	LOG.info("No Analysis Jobs to Start");
            return;
        }
        
        ExecutorService threadPool = Executors.newFixedThreadPool(analysisItems.size());
        LOG.info("Currently Running {} jobs. Starting {} more", RUNNING_JOBS.size(), analysisItems.size());
        for (AnalysisItem item: analysisItems) {
            RUNNING_JOBS.add(getJobsParametersString(item));
            threadPool.execute(() -> {
                JobParameters jobParameters = new JobParametersBuilder().addLong("pricing.analysis.rowId", item.getRowId())
                        .addLong("pricing.analysis.id", item.getAnalysisId()).addLong("pricing.analysis.tariffId", item.getTariffId())
                        .addLong("pricing.analysis.customerId", item.getCustomerId()).toJobParameters();
                LOG.info("Running job: " + jobParameters.toString());
                try {
                    jobLauncher.run(job, jobParameters);
                } catch (Exception e) {
                    RUNNING_JOBS.remove(getJobsParametersString(item));
                }
            });
        }
        threadPool.shutdown();
        
    }

    private List<AnalysisItem> getNextAnalysisItemsForProcessing() {
        List<AnalysisItem> analysisItems = dao.getNextAnalysisForProcessing((int) maxParallelJobsCount);
        return analysisItems.parallelStream().filter(item -> !RUNNING_JOBS.contains(getJobsParametersString(item))).collect(Collectors.toList());
    }

    private String getJobsParametersString(JobParameters jobParameters) {
        return getJobsParametersString(jobParameters.getLong("pricing.analysis.rowId"), jobParameters.getLong("pricing.analysis.tariffId"),
                jobParameters.getLong("pricing.analysis.customerId"));
    }

    private String getJobsParametersString(AnalysisItem item) {
        return getJobsParametersString(item.getRowId(), item.getTariffId(), item.getCustomerId());
    }

    private String getJobsParametersString(Object... parameters) {
        return Stream.of(parameters).map(p -> ObjectUtils.defaultIfNull(p, 0).toString()).collect(Collectors.joining("_"));
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // nothing to do here yet
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Long rowId = jobExecution.getJobParameters().getLong("pricing.analysis.rowId");
        Long analysisId = jobExecution.getJobParameters().getLong("pricing.analysis.id");
        synchronized (rowId) {
            boolean rowAnalysisComplete = dao.markInputDetailAsCompleted(rowId);
            synchronized (analysisId) {
                boolean jobComplete = false;
                if (rowAnalysisComplete) {
                    jobComplete = dao.markAnalysisAsCompleted(analysisId);
                }
                if (jobComplete || isJobPaused(analysisId)) {
                    service.createOutputExcelFile(analysisId);
                }
            }
        }
        RUNNING_JOBS.remove(getJobsParametersString(jobExecution.getJobParameters()));
        runAnalysisDelayed(5); // let java collect garbage before starting next analysis job.
    }

    /**
     * All jobs should be completed for specified analysis ID to be able to generate a file.
     *
     * @param analysisId
     *            Analysis ID to be checked.
     * @return <code>true</code> if analysis processing is paused and there is no more running executions for
     *         specified Analysis ID.
     */
    private boolean isJobPaused(Long analysisId) {
        boolean paused = dao.getAnalysisStatus(analysisId) == FinancialAnalysisStatus.Stopped;
        if (paused) {
            Set<JobExecution> executions = jobExplorer.findRunningJobExecutions(JOB_NAME);
            long count = executions.stream().filter(JobExecution::isRunning).map(e -> e.getJobParameters().getLong("pricing.analysis.id"))
                    .filter(analysisId::equals).count();
            return count < 2;
        }
        return false;
    }

    private void runAnalysisDelayed(long delayInSeconds) {
        if (schedule != null) {
            return;
        }
        schedule = scheduler.schedule(() -> {
            try {
                startAnalysis();
            } catch (Exception e) {
                LOG.error("Failed scheduling {} job. {}", JOB_NAME, e.getMessage(), e);
                runAnalysisDelayed(5); // let java collect garbage before starting next analysis job.
            }
        }, Date.from(new Date().toInstant().plusSeconds(delayInSeconds)));
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        shutdown();
    }
}
