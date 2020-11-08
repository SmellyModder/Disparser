package net.smelly.disparser.concurrent;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link ThreadFactory} implementation for pooled named threads for processing commands.
 * <p>This is the default {@link ThreadFactory} used by Disparser.</p>
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class DisparsingThreadFactory implements ThreadFactory {
	private static final AtomicInteger POOL_NUMBER = new AtomicInteger(0);
	private static final String DISPARSER_WORKER = "Disparser-Worker-";
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final ThreadGroup threadGroup;
	private final String namePrefix;

	/**
	 * Constructor for a new {@link DisparsingThreadFactory}.
	 *
	 * @param workerSuffix The suffix to come after the "Disparser-Worker-" prefix for the naming of created threads with this factory.
	 */
	public DisparsingThreadFactory(String workerSuffix) {
		SecurityManager securityManager = System.getSecurityManager();
		this.threadGroup = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = DISPARSER_WORKER + workerSuffix + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
	}

	@Override
	public Thread newThread(@Nonnull Runnable runnable) {
		Thread thread = new Thread(this.threadGroup, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
		if (thread.isDaemon()) {
			thread.setDaemon(false);
		}
		if (thread.getPriority() != Thread.NORM_PRIORITY) {
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		return thread;
	}
}
