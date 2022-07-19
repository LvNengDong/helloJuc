package future.completableFuture.handleException;

import utils.SmallTool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 18:58
 */
public class TimeoutHandler {
    /**
     * 程序超时时间
     */
    private Long timeout;

    /**
     * 程序执行时间
     */
    private Long executeTime;

    public TimeoutHandler(Long timeout, Long executeTime) {
        this.timeout = timeout;
        this.executeTime = executeTime;
    }

    /**
     * 线程池
     */
    public ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            0,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public void start() {
        CompletableFuture<Boolean> cf1 = CompletableFuture
                // 开启异步线程执行任务
                .supplyAsync(() -> {
                    boolean flag = false;
                    SmallTool.printTimeAndThread("数据传输中，预估耗时" + executeTime + "毫秒");
                    // 任务执行时间
                    SmallTool.sleepMillis(executeTime);

                    // 模拟异常
                    //int i = 1 / 0;

                    // 任务执行成功
                    flag = true;
                    return flag;
                })
                // 同时开启一个“守护线程”
                .applyToEither(
                        CompletableFuture.supplyAsync(() -> {
                            // 守护线程什么也不干，只设置允许等待的最大时间
                            SmallTool.printTimeAndThread("我是守护线程，我最多只等" + timeout + "毫秒");
                            SmallTool.sleepMillis(timeout);
                            return false;
                        }), value -> value
                )
                .exceptionally(ex -> {
                    // 处理异常
                    System.out.println("Exception" + ex.getMessage());
                    return null;
                });

        if (cf1.join()){
            SmallTool.printTimeAndThread("程序执行成功");
        }else {
            SmallTool.printTimeAndThread("程序执行超时");
        }
    }


    public static void main(String[] args) {
        //TimeoutHandler timeoutHandler = new TimeoutHandler(1000L, 2000L);
        TimeoutHandler timeoutHandler = new TimeoutHandler(2000L, 2000L);
        timeoutHandler.start();

        /* 执行超时的情况：
        --------------------------------------
        1658232274797	|	12	|	ForkJoinPool.commonPool-worker-1	|	数据传输中，预估耗时2000毫秒
        1658232274797	|	13	|	ForkJoinPool.commonPool-worker-2	|	我是守护线程，我最多只等1000毫秒
        1658232275810	|	1	|	main	|	程序执行超时
        ----------------------------------- */

        /* 执行成功的情况：
        --------------------------------------
        1658232292422	|	13	|	ForkJoinPool.commonPool-worker-2	|	我是守护线程，我最多只等5000毫秒
        1658232292422	|	12	|	ForkJoinPool.commonPool-worker-1	|	数据传输中，预估耗时2000毫秒
        1658232294427	|	1	|	main	|	程序执行成功
        ----------------------------------- */
    }
}
