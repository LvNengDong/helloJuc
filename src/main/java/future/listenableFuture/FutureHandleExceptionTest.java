package future.listenableFuture;

import com.google.common.util.concurrent.*;
import utils.SmallTool;

import java.util.concurrent.Executors;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/20 10:30
 */
public class FutureHandleExceptionTest {
    /**
     * 线程池中线程的个数
     */
    private static final int POOL_SIZE = 50;

    /**
     * 带有回调机制的线程池
     * （装饰器模式）
     */
    private static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(POOL_SIZE));

    public static void main(String[] args) {
        SmallTool.printTimeAndThread("主线任务开始执行");

        /**
         * 将实现了 Callable 的任务放到线程池中，得到一个带有回调机制的 ListenableFuture 实例（获取线程执行结果的凭据）。
         * 通过 Futures.addCallback 方法对得到的 ListenableFuture 实例进行监听，一旦得到结果（正常执行结果/异常执行结果）.
         * 正常结果进入 onSuccess 方法中，异常结果进入 onFailure 方法处理。
         */
        ListenableFuture<String> listenableFuture = listeningExecutorService
                // 通过（带监听器的）线程池执行任务
                .submit(() -> {
                    SmallTool.printTimeAndThread("支线任务开始执行");
                    // 执行任务耗时
                    SmallTool.sleepMillis(3000);
                    // 模拟异常情况
                    int i = 1 / 0;
                    // 执行任务成功
                    SmallTool.printTimeAndThread("支线任务执行成功");
                    return "OK";
                });

        SmallTool.printTimeAndThread("主线任务继续运行");

        // 添加监听器对 listenableFuture 实例进行监听（运行监听器需要一个额外的线程，由线程池进行分配）
        listenableFuture.addListener(() -> SmallTool.printTimeAndThread("监听器正在监听任务的执行情况"), listeningExecutorService);

        // 通过 listenableFuture 凭据获取执行结果
        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            /**
             * 处理正常结果
             * @param result
             */
            public void onSuccess(String result) {
                SmallTool.printTimeAndThread("程序执行完成，执行结果为：" + result);
            }

            /**
             * 处理异常结果
             * @param t
             */
            public void onFailure(Throwable t) {
                SmallTool.printTimeAndThread("程序出现异常，异常信息为：" + t.getMessage());
                //throw new RuntimeException(t);

            }
        });

        // 同步执行主线任务
        SmallTool.sleepMillis(100);
        SmallTool.printTimeAndThread("主线任务结束");

        /* 执行以上代码，输出结果为：
        --------------------------------------
            1658309255518	|	1	|	main	|	主线任务开始执行
            1658309255577	|	1	|	main	|	主线任务继续运行
            1658309255578	|	13	|	pool-1-thread-1	|	支线任务开始执行
            1658309258594	|	15	|	pool-1-thread-2	|	监听器正在监听任务的执行情况
            1658309258594	|	13	|	pool-1-thread-1	|	程序出现异常，异常信息为：/ by zero
        -----------------------------------
        分析：
        */
    }
}
