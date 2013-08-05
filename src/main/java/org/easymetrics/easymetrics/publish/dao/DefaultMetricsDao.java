package org.easymetrics.easymetrics.publish.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.easymetrics.easymetrics.model.Aggregation;
import org.easymetrics.easymetrics.model.Bucket;
import org.easymetrics.easymetrics.model.CollectorUsage;
import org.easymetrics.easymetrics.model.HeapUsage;
import org.easymetrics.easymetrics.model.Measurement;
import org.easymetrics.easymetrics.model.NameValue;
import org.easymetrics.easymetrics.model.Record;
import org.easymetrics.easymetrics.model.ResourceUsage;
import org.easymetrics.easymetrics.model.ThreadUsage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.AbstractInterruptibleBatchPreparedStatementSetter;

/**
 * For saving performance measurements and aggregations into database.
 * 
 * @author Administrator
 * 
 */
public class DefaultMetricsDao {

	private static final String INSERT_METRICS_RECORD = "INSERT INTO METRICS_RECORD (RECORD_ID, SERVICE_GROUP, DOMAIN, HOST_NAME, SERVICE, APPLICATION_VERSION, HOST_USER, INSTANCE_PID, AGGREGATION_RANGES, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private static final String INSERT_MEASUREMENT = "INSERT INTO METRICS_MEASUREMENT (RECORD_ID, MEASUREMENT_ID,PARENT_ID,CORRELATION_ID,REQUESTER_ID,COMPONENT_NAME,FUNCTION_NAME,THREAD_NAME,REQUEST_USER,TIME,DURATION,WORK_UNITS,CREATE_ORDER, FAIL_STATUS, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private static final String INSERT_MEASUREMENT_METRICS = "INSERT INTO METRICS_MEASUREMENT_METRICS (MEASUREMENT_ID, NAME, VALUE, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT) VALUES (?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private static final String INSERT_AGGREGATION = "INSERT INTO METRICS_AGGREGATION (RECORD_ID, AGGREGATION_ID, COMPONENT_NAME, FUNCTION_NAME, START_TIME, DURATION, MAXIMUM, MINIMUM, AVERAGE, UNIT_MAXIMUM, UNIT_MINIMUM, UNIT_AVERAGE, COUNT, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private static final String INSERT_AGGREGATION_BUCKET = "INSERT INTO METRICS_AGGREGATION_BUCKET (AGGREGATION_ID, START_RANGE, COUNT, UNIT_COUNT, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT) VALUES (?, ?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private static final String INSERT_USAGE_RUNTIME = "INSERT INTO METRICS_USAGE_RUNTIME (RECORD_ID, USAGE_ID, CHECK_TIME, CPU_COUNT, THREAD_COUNT, UP_TIME, CPU_TIME, USER_TIME, HEAP_MAX, HEAP_USED, NON_HEAP_MAX, NON_HEAP_USED, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private static final String INSERT_USAGE_THREAD = "INSERT INTO METRICS_USAGE_THREAD (USAGE_ID, NAME, STATE, CPU_TIME, USER_TIME, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT) VALUES (?, ?, ?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private static final String INSERT_USAGE_HEAP = "INSERT INTO METRICS_USAGE_HEAP (USAGE_ID, NAME, MEM_MAX, MEM_USED, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT) VALUES (?, ?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private static final String INSERT_USAGE_COLLECTOR = "INSERT INTO METRICS_USAGE_GC (USAGE_ID, NAME, GC_COUNT, GC_TIME, CREATED_FROM, CREATED_AT, UPDATED_FROM, UPDATED_AT) VALUES (?, ?, ?, ?, 'SYSTEM', NOW(), 'SYSTEM', NOW())";

	private JdbcTemplate jdbcTemplate;

	public int saveRecord(final Record record) {
		int count = jdbcTemplate.update(INSERT_METRICS_RECORD,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, record.getId());
						ps.setString(2, record.getServiceGroup());
						ps.setString(3, record.getDomain());
						ps.setString(4, record.getHost());
						ps.setString(5, record.getService());
						ps.setString(6, record.getVersion());
						ps.setString(7, record.getUser());
						ps.setString(8, record.getPid());
						ps.setString(9, record.getAggregationRanges());
					}
				});
		if (count != 0) {
			saveMeasurements(record.getId(), record.getMeasurementList());
			saveAggregations(record.getId(), record.getAggregationList());
			saveRuntimeUsages(record.getId(), record.getUsageList());
		}
		return count;
	}

	/**
	 * Use batch mechanism to save all measurements.
	 * 
	 * @param recordId
	 * @param measurementList
	 * @return row affected
	 */
	public int saveMeasurements(final String recordId,
			final List<Measurement> measurementList) {
		int count = 0;

		if (!measurementList.isEmpty()) {
			int[] counts = jdbcTemplate.batchUpdate(INSERT_MEASUREMENT,
					new AbstractInterruptibleBatchPreparedStatementSetter() {

						@Override
						public boolean setValuesIfAvailable(
								PreparedStatement ps, int i)
								throws SQLException {
							if (i >= measurementList.size()) {
								return false;
							}

							Measurement measurement = measurementList.get(i);
							ps.setString(1, recordId);
							ps.setString(2, measurement.getId());
							ps.setString(3, measurement.getParentId());
							ps.setString(4, measurement.getCorrelationId());
							ps.setString(5,
									measurement.getCorrelationRequester());
							ps.setString(6, measurement.getComponentName());
							ps.setString(7, measurement.getFunctionName());
							ps.setString(8, measurement.getThreadName());
							ps.setString(9, measurement.getUser());
							ps.setTimestamp(10, new java.sql.Timestamp(
									measurement.getTimestamp().getTime()));
							ps.setLong(11, measurement.getDuration());
							ps.setLong(12, measurement.getWorkUnits());
							ps.setLong(13, measurement.getCreateOrder());
							ps.setBoolean(14, measurement.getFailStatus());

							return true;
						}

						@Override
						public int getBatchSize() {
							return measurementList.size();
						}

					});

			if (counts != null) {
				for (int rowAffected : counts) {
					count += rowAffected;
				}

				List<MeasurementMetrics> metricsList = new ArrayList<MeasurementMetrics>();
				for (Measurement measurement : measurementList) {
					for (NameValue metrics : measurement.getMetricsList()) {
						metricsList.add(new MeasurementMetrics(measurement
								.getId(), metrics));
					}
				}
				saveMeasurementMetrics(metricsList);
			}
		}

		return count;
	}

	private int saveMeasurementMetrics(
			final List<MeasurementMetrics> metricsList) {
		int count = 0;
		if (!metricsList.isEmpty()) {
			int[] counts = jdbcTemplate.batchUpdate(INSERT_MEASUREMENT_METRICS,
					new AbstractInterruptibleBatchPreparedStatementSetter() {
						@Override
						public boolean setValuesIfAvailable(
								PreparedStatement ps, int i)
								throws SQLException {
							if (i >= metricsList.size()) {
								return false;
							}

							MeasurementMetrics argument = metricsList.get(i);
							ps.setString(1, argument.getMeasurementId());
							ps.setString(2, argument.getMetrics().getName());
							ps.setString(3, argument.getMetrics().getValue());
							return true;
						}

						@Override
						public int getBatchSize() {
							return metricsList.size();
						}

					});

			if (counts != null) {
				for (int rowAffected : counts) {
					count += rowAffected;
				}
			}
		}

		return count;
	}

	public int saveAggregations(final String recordId,
			final List<Aggregation> aggregationList) {
		int count = 0;
		if (!aggregationList.isEmpty()) {
			int[] counts = jdbcTemplate.batchUpdate(INSERT_AGGREGATION,
					new AbstractInterruptibleBatchPreparedStatementSetter() {
						@Override
						public boolean setValuesIfAvailable(
								PreparedStatement ps, int i)
								throws SQLException {
							if (i >= aggregationList.size()) {
								return false;
							}

							Aggregation argument = aggregationList.get(i);
							ps.setString(1, recordId);
							ps.setString(2, argument.getId());
							ps.setString(3, argument.getComponentName());
							ps.setString(4, argument.getFunctionName());
							ps.setTimestamp(5, new java.sql.Timestamp(argument
									.getStartTime().getTime()));
							ps.setLong(6, argument.getDuration());
							ps.setLong(7, argument.getMaximum());
							ps.setLong(8, argument.getMinimum());
							ps.setDouble(9, argument.getAverage());
							ps.setLong(10, argument.getUnitMaximum());
							ps.setLong(11, argument.getUnitMinimum());
							ps.setDouble(12, argument.getUnitAverage());
							ps.setLong(13, argument.getCount());
							return true;
						}

						@Override
						public int getBatchSize() {
							return aggregationList.size();
						}

					});
			if (counts != null) {
				for (int rowAffected : counts) {
					count += rowAffected;
				}

				List<AggregationBucket> bucketList = new ArrayList<AggregationBucket>();
				for (Aggregation aggregation : aggregationList) {
					for (Bucket bucket : aggregation.getBucketList()) {
						bucketList.add(new AggregationBucket(aggregation
								.getId(), bucket));
					}
				}
				saveAggregationBuckets(bucketList);
			}

		}
		return count;
	}

	private int saveAggregationBuckets(final List<AggregationBucket> bucketList) {
		int count = 0;
		if (!bucketList.isEmpty()) {
			int[] counts = jdbcTemplate.batchUpdate(INSERT_AGGREGATION_BUCKET,
					new AbstractInterruptibleBatchPreparedStatementSetter() {
						@Override
						public boolean setValuesIfAvailable(
								PreparedStatement ps, int i)
								throws SQLException {
							if (i >= bucketList.size()) {
								return false;
							}

							AggregationBucket argument = bucketList.get(i);
							ps.setString(1, argument.getAggregationId());
							ps.setLong(2, argument.getBucket().getStartRange());
							ps.setLong(3, argument.getBucket().getCount());
							ps.setLong(4, argument.getBucket().getUnitCount());
							return true;
						}

						@Override
						public int getBatchSize() {
							return bucketList.size();
						}

					});

			if (counts != null) {
				for (int rowAffected : counts) {
					count += rowAffected;
				}
			}

		}
		return count;
	}

	public int saveRuntimeUsages(final String recordId,
			final List<ResourceUsage> usageList) {
		int count = 0;

		if (!usageList.isEmpty()) {
			int[] counts = jdbcTemplate.batchUpdate(INSERT_USAGE_RUNTIME,
					new AbstractInterruptibleBatchPreparedStatementSetter() {
						@Override
						public boolean setValuesIfAvailable(
								PreparedStatement ps, int i)
								throws SQLException {
							if (i >= usageList.size()) {
								return false;
							}

							ResourceUsage argument = usageList.get(i);
							ps.setString(1, recordId);
							ps.setString(2, argument.getUsageId());
							ps.setTimestamp(3, new java.sql.Timestamp(argument
									.getCheckTime().getTime()));
							ps.setLong(4, argument.getProcessorCount());
							ps.setLong(5, argument.getThreadCount());
							ps.setLong(6, argument.getUpTime());
							ps.setLong(7, argument.getCpuTime());
							ps.setLong(8, argument.getUserTime());
							ps.setLong(9, argument.getHeapMax());
							ps.setLong(10, argument.getHeapUsed());
							ps.setLong(11, argument.getNonHeapMax());
							ps.setLong(12, argument.getNonHeapUsed());
							return true;
						}

						@Override
						public int getBatchSize() {
							return usageList.size();
						}

					});

			if (counts != null) {
				for (int rowAffected : counts) {
					count += rowAffected;
				}

				List<ThreadUsage> threadUsageList = new ArrayList<ThreadUsage>();
				List<HeapUsage> heapUsageList = new ArrayList<HeapUsage>();
				List<CollectorUsage> collectorUsageList = new ArrayList<CollectorUsage>();

				for (ResourceUsage usage : usageList) {
					// make sure all entries have the resource IDs
					List<ThreadUsage> threadList = usage.getThreadList();
					if (!threadList.isEmpty()) {
						for (ThreadUsage threadUsage : threadList) {
							threadUsage.setUsageId(usage.getUsageId());
						}
						threadUsageList.addAll(threadList);
					}
					List<HeapUsage> heapList = usage.getHeapList();
					if (!heapList.isEmpty()) {
						for (HeapUsage heapUsage : heapList) {
							heapUsage.setUsageId(usage.getUsageId());
						}
						heapUsageList.addAll(heapList);
					}
					List<CollectorUsage> collectorList = usage
							.getCollectorList();
					if (!collectorList.isEmpty()) {
						for (CollectorUsage collectorUsage : collectorList) {
							collectorUsage.setUsageId(usage.getUsageId());
						}
						collectorUsageList.addAll(collectorList);
					}
				}

				if (!threadUsageList.isEmpty()) {
					saveRuntimeThreadUsage(threadUsageList);
				}

				if (!heapUsageList.isEmpty()) {
					saveRuntimeHeapUsage(heapUsageList);
				}

				if (!collectorUsageList.isEmpty()) {
					saveRuntimeCollectorUsage(collectorUsageList);
				}
			}
		}
		return count;
	}

	private int saveRuntimeThreadUsage(final List<ThreadUsage> threadUsageList) {
		int count = 0;
		if (!threadUsageList.isEmpty()) {
			int[] counts = jdbcTemplate.batchUpdate(INSERT_USAGE_THREAD,
					new AbstractInterruptibleBatchPreparedStatementSetter() {
						@Override
						public boolean setValuesIfAvailable(
								PreparedStatement ps, int i)
								throws SQLException {
							if (i >= threadUsageList.size()) {
								return false;
							}

							ThreadUsage argument = threadUsageList.get(i);
							ps.setString(1, argument.getUsageId());
							ps.setString(2, argument.getName());
							ps.setString(3, argument.getState());
							ps.setLong(4, argument.getCpuTime());
							ps.setLong(5, argument.getUserTime());
							return true;
						}

						@Override
						public int getBatchSize() {
							return threadUsageList.size();
						}

					});

			if (counts != null) {
				for (int rowAffected : counts) {
					count += rowAffected;
				}
			}
		}
		return count;
	}

	private int saveRuntimeHeapUsage(final List<HeapUsage> heapUsageList) {
		int count = 0;
		if (!heapUsageList.isEmpty()) {
			int[] counts = jdbcTemplate.batchUpdate(INSERT_USAGE_HEAP,
					new AbstractInterruptibleBatchPreparedStatementSetter() {
						@Override
						public boolean setValuesIfAvailable(
								PreparedStatement ps, int i)
								throws SQLException {
							if (i >= heapUsageList.size()) {
								return false;
							}

							HeapUsage argument = heapUsageList.get(i);
							ps.setString(1, argument.getUsageId());
							ps.setString(2, argument.getName());
							ps.setLong(3, argument.getMax());
							ps.setLong(4, argument.getUsed());
							return true;
						}

						@Override
						public int getBatchSize() {
							return heapUsageList.size();
						}

					});

			if (counts != null) {
				for (int rowAffected : counts) {
					count += rowAffected;
				}
			}
		}
		return count;
	}

	private int saveRuntimeCollectorUsage(
			final List<CollectorUsage> collectorUsageList) {
		int count = 0;
		if (!collectorUsageList.isEmpty()) {
			int[] counts = jdbcTemplate.batchUpdate(INSERT_USAGE_COLLECTOR,
					new AbstractInterruptibleBatchPreparedStatementSetter() {
						@Override
						public boolean setValuesIfAvailable(
								PreparedStatement ps, int i)
								throws SQLException {
							if (i >= collectorUsageList.size()) {
								return false;
							}

							CollectorUsage argument = collectorUsageList.get(i);
							ps.setString(1, argument.getUsageId());
							ps.setString(2, argument.getName());
							ps.setLong(3, argument.getCount());
							ps.setLong(4, argument.getTime());
							return true;
						}

						@Override
						public int getBatchSize() {
							return collectorUsageList.size();
						}

					});

			if (counts != null) {
				for (int rowAffected : counts) {
					count += rowAffected;
				}
			}
		}
		return count;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}

class MeasurementMetrics {
	private String measurementId;
	private NameValue metrics;

	public MeasurementMetrics(String measurementId, NameValue metrics) {
		this.measurementId = measurementId;
		this.metrics = metrics;
	}

	public String getMeasurementId() {
		return measurementId;
	}

	public void setMeasurementId(String measurementId) {
		this.measurementId = measurementId;
	}

	public NameValue getMetrics() {
		return metrics;
	}

	public void setMetrics(NameValue metrics) {
		this.metrics = metrics;
	}

}

class AggregationBucket {
	private String aggregationId;
	private Bucket bucket;

	public AggregationBucket(String aggregationId, Bucket bucket) {
		this.aggregationId = aggregationId;
		this.bucket = bucket;
	}

	public String getAggregationId() {
		return aggregationId;
	}

	public void setAggregationId(String aggregationId) {
		this.aggregationId = aggregationId;
	}

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}

}
