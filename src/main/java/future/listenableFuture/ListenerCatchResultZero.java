package future.listenableFuture;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.google.common.util.concurrent.*;
import utils.SmallTool;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * @Author lnd
 * @Description <p>
 * 问题： 对于多线程异步处理的逻辑，一直存在一个疑问，就是如果最终通过主线程获取分支线程的执行结果的话，那么在执行到“获取结果”这一步时，
 * 还是会存在阻塞。那么该如何解决这个阻塞问题呢？
 * <p>
 * 解决：通过组会中讨论的方式，我们最终得出的一个解决方案就是：如果主线程中的任务对分支线程中的任务不具有强依赖性的话，我们可以把获取
 * 分支线程执行结果的代码放到监听器中去作，这样主线程就永远不会被阻塞了。
 * @Date 2022/7/21 16:48
 */
public class ListenerCatchResultZero {
    /**
     * 普通线程池
     */
    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            0,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

    /**
     * 带有回调机制的线程池
     * （装饰器模式）
     */
    private static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(threadPool);



    public static void main(String[] args) {

        SmallTool.printTimeAndThread("主线任务开始执行");
        //
        ListenableFuture<String> listenableFuture = listeningExecutorService.submit(() -> {
            SmallTool.printTimeAndThread("开始执行支线任务");
            SmallTool.sleepMillis(3000); // 分支线程执行任务消耗的时间
            SmallTool.printTimeAndThread("支线任务执行完成");
            return "OK";
        });


        // 监听器和支线任务用的同一个线程，因为只有在支线任务执行结束后监听器才会被触发，所以共用一个线程没有任何问题
        listenableFuture.addListener(() -> {
            SmallTool.printTimeAndThread("监听器启动");
        }, MoreExecutors.directExecutor());


        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                SmallTool.printTimeAndThread("支线任务返回结果是：" + result);
            }

            @Override
            public void onFailure(Throwable t) {
                SmallTool.printTimeAndThread("支线任务出现异常，异常情况为：" + t.getMessage());
            }
        }, listeningExecutorService);

        // 经过测试发现，这句输出在监听器之前被执行，所以说Main线程根本不会被阻塞，可以一直执行。
        // 至于监听结果，也即 Futures.addCallback 中的方法，会分配一个新的线程去执行，
        // 直到所有线程中的任务执行结束后，进程被关闭
        SmallTool.printTimeAndThread("主线任务继续执行");
    }
    /* 执行以上代码，输出结果为：
    --------------------------------------
        1658396447914	|	1	|	main	|	主线任务开始执行
        1658396447983	|	13	|	pool-1-thread-1	|	开始执行支线任务
        1658396447988	|	1	|	main	|	主线任务继续执行
        1658396450986	|	13	|	pool-1-thread-1	|	支线任务执行完成
        1658396450988	|	13	|	pool-1-thread-1	|	监听器启动
        1658396450989	|	15	|	pool-1-thread-2	|	支线任务返回结果是：OK
    -----------------------------------
    分析：
    */
}
