package com.example.streaming.configuration;

import com.example.event.EventException;
import org.slf4j.Logger;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

public abstract class StreamProcessor implements ShardRecordProcessor {

    private final Logger logger;

    private String shardId;

    public StreamProcessor(Logger logger) {
        this.logger = logger;
    }

    public abstract void processRecord(final KinesisClientRecord records) throws EventException;

    @Override
    public void initialize(InitializationInput initializationInput) {
        this.shardId = initializationInput.shardId();
        logger.debug("Initializing record processor for shard {}", shardId);
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        logger.debug("Processing records for shard {}", shardId);

        final var records = processRecordsInput.records();
        for (final var record : records) {
            try {
                this.processRecord(record);
            } catch (EventException exception) {
                logger.error("Error processing records from shard {}", shardId, exception);
            }
        }

        try {
            processRecordsInput.checkpointer()
                .checkpoint();

            logger.debug("Record processor for shard {} processed {} records and checkpoint", shardId, records.size());
        } catch (InvalidStateException exception) {
            logger.error("Record processor for shard {} could not persist checkpoint in DynamoDB table while processing records", shardId, exception);
        } catch (ShutdownException exception) {
            logger.debug("Record processor for shard {} has shutdown while processing while processing records", shardId, exception);
        }
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        logger.debug("Record processor for shard {} has lost its lease", shardId);
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        try {
            shardEndedInput.checkpointer()
                .checkpoint();

            logger.debug("Record processor for shard {} checkpoint and ended successfully", shardId);
        } catch (InvalidStateException exception) {
            logger.error("Record processor for shard {} could not persist checkpoint in DynamoDB table", shardId, exception);
        } catch (ShutdownException exception) {
            logger.debug("Record processor for shard {} has shutdown", shardId, exception);
        }
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        try {
            shutdownRequestedInput.checkpointer()
                .checkpoint();

            logger.debug("Record processor for shard {} shutdown request processed", shardId);
        } catch (InvalidStateException exception) {
            logger.error("Record processor for shard {} could not persist checkpoint before shutdown", shardId, exception);
        } catch (ShutdownException exception) {
            logger.debug("Record processor for shard {} could not finish shutdown request", shardId, exception);
        }
    }
}
