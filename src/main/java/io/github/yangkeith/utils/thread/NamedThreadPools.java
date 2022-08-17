package io.github.yangkeith.utils.thread;

import java.util.concurrent.*;

/**
 * <p>Title: NamedThreadPools</p>
 * <p>Description: </p>
 *
 * @author Keith
 * @date 2022/04/02 10:18
 */
public class NamedThreadPools {
    
    /**
     * 新固定线程池
     *
     * @param nThreads n个线程
     * @param name     名字
     * @return {@link ExecutorService }
     * @author Keith
     * @date 2022-08-17
     */
    public static ExecutorService newFixedThreadPool(int nThreads, String name) {
        return newFixedThreadPool(nThreads, new NamedThreadFactory(name));
    }
    
    
    /**
     * 新固定线程池
     *
     * @param nThreads      n个线程
     * @param threadFactory 线程工厂
     * @return {@link ExecutorService }
     * @author Keith
     * @date 2022-08-17
     */
    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
    }
    
    
    /**
     * 新缓存线程池
     *
     * @param name 名字
     * @return {@link ExecutorService }
     * @author Keith
     * @date 2022-08-17
     */
    public static ExecutorService newCachedThreadPool(String name) {
        return newCachedThreadPool(new NamedThreadFactory(name));
    }
    
    
    /**
     * 新缓存线程池
     *
     * @param threadFactory 线程工厂
     * @return {@link ExecutorService }
     * @author Keith
     * @date 2022-08-17
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                threadFactory);
    }
    
    
    /**
     * 新安排线程池
     *
     * @param corePoolSize 核心池大小
     * @param name         名字
     * @return {@link ScheduledExecutorService }
     * @author Keith
     * @date 2022-08-17
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, String name) {
        return newScheduledThreadPool(corePoolSize, new NamedThreadFactory(name));
    }
    
    
    /**
     * 新安排线程池
     *
     * @param corePoolSize  核心池大小
     * @param threadFactory 线程工厂
     * @return {@link ScheduledExecutorService }
     * @author Keith
     * @date 2022-08-17
     */
    public static ScheduledExecutorService newScheduledThreadPool(
            int corePoolSize, ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }
}
