package qiu.spider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *  创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程.
 * */
public class SpiderThreadPool {
    /**
     * 可缓存的线程池
     * */
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    /**
     * 丢进来线程并执行
     * */
    public void execute(Runnable runnable){
        cachedThreadPool.execute(runnable);
    }
}
