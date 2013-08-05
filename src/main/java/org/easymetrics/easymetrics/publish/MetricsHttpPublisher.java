/**
 * 
 */
package org.easymetrics.easymetrics.publish;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.concurrent.FutureCallback;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.easymetrics.easymetrics.exception.MetricsException;
import org.easymetrics.easymetrics.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class MetricsHttpPublisher implements MetricsPublisher {

	private static final Logger	LOGGER				= LoggerFactory.getLogger(MetricsHttpPublisher.class);

	private static final String	PUBLISH_URL_LOG		= "log";

	private HttpAsyncClient		httpClient			= null;
	private String				publishUrl			= PUBLISH_URL_LOG;
	private int					socketTimeout		= 5000;
	private int					connectionTimeout	= 10000;
	private String				userAgent			= "MetricsHttpPublisher";

	public void start() {
		try {
			HttpParams params = new SyncBasicHttpParams();

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			HttpConnectionParams.setTcpNoDelay(params, true);
			HttpConnectionParams.setSocketBufferSize(params, 8192);
			HttpConnectionParams.setSoTimeout(params, socketTimeout);
			HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
			HttpProtocolParams.setUserAgent(params, userAgent);

			httpClient = new DefaultHttpAsyncClient(params);
			httpClient.start();
		} catch (IOReactorException e) {
			LOGGER.error("failed to startup http client.", e);
			throw new MetricsException(e.getMessage(), e);
		}
	}

	public void stop() {
		try {
			httpClient.shutdown();
		} catch (InterruptedException e) {
			LOGGER.error("", e);
		}
	}

	@Override
	public boolean publish(Record record) {
		boolean succeed = false;

		try {
			String json = JSON.toJSONString(record);

			if (PUBLISH_URL_LOG.equals(publishUrl)) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(json);
				}
				succeed = true;
			} else {
				postContent(json);
				succeed = true;
			}
		} catch (UnsupportedEncodingException e) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Unsupported encoding", e);
			}
		}
		return succeed;
	}

	/**
	 * Post the content to the HTTP server.
	 * 
	 * @param content
	 * @throws UnsupportedEncodingException
	 * 
	 */
	private void postContent(final String content) throws UnsupportedEncodingException {

		HttpPost post = new HttpPost(publishUrl);

		HttpEntity entity = new StringEntity(content);
		post.setEntity(entity);

		httpClient.execute(post, new FutureCallback<HttpResponse>() {

			@Override
			public void completed(final HttpResponse response) {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("Response: " + response.getStatusLine());
				}
			}

			@Override
			public void failed(final Exception ex) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Post content failed.", ex);
				}
			}

			@Override
			public void cancelled() {
			}

		});

	}

	public void setPublishUrl(String publishUrl) {
		this.publishUrl = publishUrl;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}
