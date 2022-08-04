package future.adaptor;

import com.google.common.util.concurrent.*;
import utils.SmallTool;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/22 10:58
 */
public class MainAdaptor {

    public static void main(String[] args) {

        MainAdaptor main = new MainAdaptor();

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("执行分支任务");
            return "OK";
        });
        // CompletableFuture to ListenableFuture
        main.completableFutureToListenableFuture(completableFuture);

        // ListenableFuture to CompletableFuture
        ListenableFutureTask<String> listenableFutureTask = ListenableFutureTask.create(new Callable<String>() {
            @Override
            public String call() {
                SmallTool.printTimeAndThread("执行分支任务");
                return "OK";
            }
        });
        new Thread(listenableFutureTask).start();
        main.listenableFutureToCompletableFuture(listenableFutureTask);

    }

    public void listenableFutureToCompletableFuture(ListenableFuture<String> listenableFuture) {
        CompletableFuture<String> completableFuture = new ListenableFutureAdaptor<>(listenableFuture);
        SmallTool.printTimeAndThread("通过CompletableFuture获取结果：" + completableFuture.join());
        /* 执行以上代码，输出结果为：
        --------------------------------------
            1658478742505	|	14	|	Thread-1	|	执行分支任务
            1658478742505	|	1	|	main	|	通过CompletableFuture获取结果：OK
        -----------------------------------
        分析：
        */
    }


    public void completableFutureToListenableFuture(CompletableFuture<String> completableFuture) {

        ListenableFuture<String> listenableFuture = new CompletableFutureAdaptor<>(completableFuture);

        listenableFuture.addListener(() -> {
            SmallTool.printTimeAndThread("触发监听器");
        }, MoreExecutors.directExecutor());

        // 回调
        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                SmallTool.printTimeAndThread("执行成功，结果为：" + result);
            }

            @Override
            public void onFailure(Throwable t) {
                SmallTool.printTimeAndThread("异常信息：" + t.getMessage());
            }
        }, MoreExecutors.directExecutor());

        /* 执行以上代码，输出结果为：
        --------------------------------------
            1658477890769	|	12	|	ForkJoinPool.commonPool-worker-1	|	执行分支任务
            1658477890773	|	1	|	main	|	触发监听器
            1658477890779	|	1	|	main	|	执行成功，结果为：OK
        -----------------------------------
        分析：
        */
    }
}
