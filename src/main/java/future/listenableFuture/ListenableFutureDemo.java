package future.listenableFuture;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import utils.SmallTool;

import java.util.concurrent.Callable;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 20:36
 */
public class ListenableFutureDemo {

    public static void main(String[] args) {
        /**
         * ListenableFutureTask 不提供构造方法，通过静态工厂方法
         * {@link ListenableFutureTask#create(Callable)} 提供对象实例
         */
        ListenableFutureTask<String> task = ListenableFutureTask.create(new Callable<String>() {
            // 分支线程要执行的任务
            @Override
            public String call() {
                SmallTool.printTimeAndThread("子线程开始执行任务");
                SmallTool.sleepMillis(3000); // 分支线程执行任务消耗的时间
                SmallTool.printTimeAndThread("子线程完成任务");
                return "OK";
            }
        });
        // 增加回调方法
        task.addListener(new Runnable() {
            @Override
            public void run() {
                SmallTool.printTimeAndThread("子线程回调");
            }
        }, MoreExecutors.directExecutor());
        /**
         * {@link MoreExecutors#directExecutor()} 返回Guava默认的线程池对象Executor。
         * 即执行回调方法不会新开线程，所有的回调方法都在由前线程执行。（在这里就是main线程）
         */
        /**
         * Guava 异步模块中一般会各有一个有 Executor 和没有 Executor 的重载方法，如果不显式添加，
         * 默认使用的就是 MoreExecutors.directExecutor()
         */

        SmallTool.printTimeAndThread("开启子线程");
        // 使用子线程执行任务
        new Thread(task).start();

        // 主线程继续执行m
        SmallTool.printTimeAndThread("主线程继续执行");

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658235960775	|	1	|	main	|	开启子线程
        1658235960775	|	1	|	main	|	主线程继续执行
        1658235960775	|	13	|	Thread-1	|	子线程开始执行任务
        1658235963785	|	13	|	Thread-1	|	子线程完成任务
        1658235963785	|	13	|	Thread-1	|	子线程回调
        ----------------------------------- */
    }
}
