package future.listenableFuture;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import utils.SmallTool;

import java.util.concurrent.*;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 20:36
 */
public class ListenableFutureDemo2 {

    public static void main(String[] args) {
        //真正干活的线程池
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                5,
                5,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.DiscardPolicy());

        // guava 的接口 ListeningExecutorService 继承了 jdk 原生 ExecutorService 接口，重写了 submit 方法，修改返回值类型为 ListenableFuture
        // 装饰器模式，对普通的线程池进行封装
        ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(poolExecutor);

        // 获得一个随着 jvm 关闭而关闭的线程池，通过 Runtime.getRuntime().addShutdownHook(hook) 实现
        // 修改ThreadFactory为创建守护线程，默认jvm关闭时最多等待120秒关闭线程池，重载方法可以设置时间
        ExecutorService newPoolExecutor = MoreExecutors.getExitingExecutorService(poolExecutor);

        //只增加关闭线程池的钩子，不改变ThreadFactory
        MoreExecutors.addDelayedShutdownHook(poolExecutor, 120, TimeUnit.SECONDS);
    }
}
