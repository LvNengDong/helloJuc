package future.useFuture;

import utils.SmallTool;
import java.util.concurrent.*;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 17:27
 */
public class FutureDemo {
    // 异步操作会使用多个线程来执行任务，可以创建一个线程池用于线程的分配
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SmallTool.printTimeAndThread("主线任务开始执行");

        // 启动异步线程，执行 FutureTask
        Future<String> future = executor.submit(() -> {
            SmallTool.printTimeAndThread("支线任务开始执行");
            SmallTool.sleepMillis(2000); // 执行任务耗时
            SmallTool.printTimeAndThread("支线任务执行成功");
            return "OK";
        });

        // 主线程继续执行任务，这里使用sleep模拟业务处理逻辑消耗的时间
        SmallTool.printTimeAndThread("主线任务执行中...");
        SmallTool.sleepMillis(1000);
        SmallTool.printTimeAndThread("主线任务执行完成...");

        // 如果此时支线任务没有执行完成，get 方法会一直阻塞，直至获取到结果
        SmallTool.printTimeAndThread("支线任务执行结果：" + future.get());

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658375091940	|	1	|	main	|	主线任务开始执行
        1658375091976	|	1	|	main	|	主线任务执行中...
        1658375091976	|	12	|	pool-1-thread-1	|	支线任务开始执行
        1658375092978	|	1	|	main	|	主线任务执行完成...
        1658375093988	|	12	|	pool-1-thread-1	|	支线任务执行成功
        1658375093988	|	1	|	main	|	支线任务执行结果：OK
        -----------------------------------
        分析：
        */
    }
}
