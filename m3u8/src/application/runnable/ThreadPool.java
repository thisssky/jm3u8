package application.runnable;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	private static ThreadPoolExecutor executor;
	private static int core;
	static {
		core = Runtime.getRuntime().availableProcessors();
		executor = new ThreadPoolExecutor(core * 10, core * 11, 1, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		executor.allowCoreThreadTimeOut(true);
	}

	public static void execute(Runnable runnable) {
		executor.execute(runnable);
	}

	public static void info() {
		int activeCount = ThreadPool.executor.getActiveCount();
		System.out.println("activeCount:" + activeCount);
		long completedTaskCount = ThreadPool.executor.getCompletedTaskCount();
		System.out.println("completedTaskCount:" + completedTaskCount);

//		int corePoolSize = ThreadPool.executor.getCorePoolSize();
//		System.out.println("corePoolSize:" + corePoolSize);
//		int maximumPoolSize = ThreadPool.executor.getMaximumPoolSize();
//		System.out.println("maximumPoolSize:" + maximumPoolSize);

		int poolSize = ThreadPool.executor.getPoolSize();
		System.out.println("poolSize:" + poolSize);
		int size = ThreadPool.executor.getQueue().size();
		System.out.println("queuesize:" + size);
		long taskCount = ThreadPool.executor.getTaskCount();
		System.out.println("taskCount:" + taskCount);

	}
}
