package future.listenableFuture;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.google.common.util.concurrent.*;
import utils.SmallTool;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
public class ListenerCatchResult {

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


        // 通过监听器的线程完成可能会阻塞的获取结果的操作，这样Main线程永远不会被阻塞
        listenableFuture.addListener(() -> {
            Futures.addCallback(listenableFuture, new FutureCallback<String>() {
                @Override
                public void onSuccess(@Nullable String result) {
                    SmallTool.printTimeAndThread("监听器发现：" + result);
                }

                @Override
                public void onFailure(Throwable t) {
                    SmallTool.printTimeAndThread("监听器发现异常，异常情况为：" + t.getMessage());
                }
            }, MoreExecutors.directExecutor());
        }, MoreExecutors.directExecutor());

        // Main线程继续执行，且不会被阻塞
        SmallTool.printTimeAndThread("主线任务继续执行");
    }
}
