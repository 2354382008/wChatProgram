package com.wChartProgram.dataCache.gwList.service;

import com.wChartProgram.dataCache.gwList.dto.GatewayLog;
import com.wChartProgram.dataCache.gwList.dao.GatewayLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class GatewayLogService {
    
    private final RealtimeStatsAggregatorService aggregator;
    private final GatewayLogMapper gatewayLogMapper;
    private final ExecutorService virtualThreadExecutor;
    private final LogBuffer logBuffer;
    
    public GatewayLogService(RealtimeStatsAggregatorService aggregator, GatewayLogMapper gatewayLogMapper) {
        ExecutorService tempExecutor;
        this.aggregator = aggregator;
        this.gatewayLogMapper = gatewayLogMapper;
        try{
            tempExecutor = Executors.newVirtualThreadPerTaskExecutor();
        }catch (Exception e) {
            log.warn("虚拟线程不可用，使用普通线程池", e);
            tempExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
        this.virtualThreadExecutor = tempExecutor;
        this.logBuffer = new LogBuffer();
    }

    public void processLog(GatewayLog gateLog) {
        aggregator.onLog(gateLog);
        virtualThreadExecutor.submit(() -> {
            if (!logBuffer.offer(gateLog)) {
                log.warn("日志缓冲队列已满，丢弃日志: {}", gateLog.requestId());
            }
        });
    }
    
    private class LogBuffer {
        private final LinkedBlockingQueue<GatewayLog> buffer = new LinkedBlockingQueue<>(10000);
        
        LogBuffer() {
            try {
                Thread.ofVirtual().start(this::processLoop);
            } catch (Exception e) {
                log.warn("虚拟线程不可用，使用普通线程", e);
                new Thread(this::processLoop, "LogBuffer-Writer").start();
            }
        }
        
        boolean offer(GatewayLog log) {
            return buffer.offer(log);
        }
        
        private void processLoop() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    var batch = new java.util.ArrayList<GatewayLog>(2000);
                    buffer.drainTo(batch, 2000);
                    if (!batch.isEmpty()) {
                        gatewayLogMapper.batchInsert(batch);
                        log.debug("批量写入 {} 条日志", batch.size());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("批量写入日志失败", e);
                }
            }
        }
    }
}
