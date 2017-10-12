package de.jotschi.vertx.ferma.ext.orientdb;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory;

import com.orientechnologies.common.concur.ONeedRetryException;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.syncleus.ferma.ext.orientdb.impl.OrientTransactionFactoryImpl;
import com.syncleus.ferma.tx.Tx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class OrientDBTxVertexFactory extends OrientTransactionFactoryImpl implements TxVertxFactory {

	private static final Logger log = LoggerFactory.getLogger(OrientDBTxVertexFactory.class);

	protected Vertx vertx;

	private int maxRetry = 25;

	public OrientDBTxVertexFactory(OrientGraphFactory factory, Vertx vertx, String... basePaths) {
		super(factory, false, basePaths);
		this.vertx = vertx;
	}

	@Override
	public <T> void asyncTx(AsyncTxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler) {
		vertx.executeBlocking(bh -> {
			tx(txHandler, rh -> {
				if (rh.succeeded()) {
					bh.complete(rh.result());
				} else {
					bh.fail(rh.cause());
				}
			});
		}, false, resultHandler);
		return;
	}

	@Override
	public <T> Future<T> tx(AsyncTxHandler<Future<T>> txHandler) {
		Future<T> future = Future.future();
		try (Tx tx = tx()) {
			txHandler.handle(future);
		} catch (Exception e) {
			log.error("Error while handling no-transaction.", e);
			return Future.failedFuture(e);
		}
		return future;
	}

	@Override
	public <T> void tx(AsyncTxHandler<Future<T>> txHandler, Handler<AsyncResult<T>> resultHandler) {
		/**
		 * OrientDB uses the MVCC pattern which requires a retry of the code that manipulates the graph in cases where for example an
		 * {@link ONeedRetryException} is thrown.
		 */
		Future<T> currentTransactionCompleted = null;
		for (int retry = 0; retry < maxRetry; retry++) {
			currentTransactionCompleted = Future.future();
			try (Tx tx = tx()) {
				// TODO FIXME get rid of the countdown latch
				CountDownLatch latch = new CountDownLatch(1);
				currentTransactionCompleted.setHandler(rh -> {
					if (rh.succeeded()) {
						tx.success();
					} else {
						tx.failure();
					}
					latch.countDown();
				});
				txHandler.handle(currentTransactionCompleted);
				latch.await(30, TimeUnit.SECONDS);
				break;
			} catch (OSchemaException e) {
				log.error("OrientDB schema exception detected.");
				// TODO maybe we should invoke a metadata getschema reload?
				// factory.getTx().getRawGraph().getMetadata().getSchema().reload();
				// Database.getThreadLocalGraph().getMetadata().getSchema().reload();
			} catch (ONeedRetryException e) {
				if (log.isTraceEnabled()) {
					log.trace("Error while handling transaction. Retrying " + retry, e);
				}
			} catch (Exception e) {
				log.error("Error handling transaction", e);
				resultHandler.handle(Future.failedFuture(e));
				return;
			}
			if (log.isDebugEnabled()) {
				log.debug("Retrying .. {" + retry + "}");
			}
		}
		if (currentTransactionCompleted != null && currentTransactionCompleted.isComplete()) {
			resultHandler.handle(currentTransactionCompleted);
			return;
		}
		resultHandler.handle(Future.failedFuture("retry limit for tx exceeded"));
		return;

	}
}
