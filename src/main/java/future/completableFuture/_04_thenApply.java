package future.completableFuture;

import utils.SmallTool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Author lnd
 * @Description 1、小白在餐厅吃饭，吃完饭需要买单，这时小白就把钱给了服务员，并且要求服务员开一张发票。
 * 2、服务员收到款后，就去开发票
 * 3、服务员在开发票的时候，小白也没闲着，接了个电话，准备回去开黑。
 * 4、刚打完电话，服务员就把发票送了过来，于是小白走出餐厅，准备回家。
 * @Date 2022/7/18 21:26
 */
public class _04_thenApply {

    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            0,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public static void main(String[] args) {
        //oldCase();
        // 扩展：如果收款的服务员和开发票的服务员不是同一个人，代码应该怎么写？
        //oldCase2();
        // 使用新API实现
        //thenApplyCase();
        // 如果收款和开票是两个不同服务员的情况
        thenApplyAsyncCase();
    }


    private static void thenApplyAsyncCase() {
        SmallTool.printTimeAndThread("小白吃好了");
        SmallTool.printTimeAndThread("小白：结账 -> 把钱给服务员 -> 要求开发票");
        // 开启新的异步线程（服务员线程）
        CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> {
                    SmallTool.printTimeAndThread("服务员收款500元");
                    SmallTool.sleepMillis(100); // 收款花了100ms
                    return "500";
                })
                .thenApplyAsync((money) -> {
                    // 得到500元收款后，另一个线程（服务员）去给小白开发票
                    SmallTool.printTimeAndThread("服务员去开发票");
                    SmallTool.sleepMillis(200); // 开票花了200ms
                    return String.format("%s元发票", money);
                }, threadPool);
        // 在服务员开发票期间，小白一直在打电话约朋友开黑
        SmallTool.printTimeAndThread("小白：一直在打电话约朋友开黑");

        // 等待发票开好后，小白离开餐厅
        SmallTool.printTimeAndThread(String.format("小白得到%s，", cf1.join() + "，拿着发票离开餐厅"));

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658158018519	|	1	|	main	|	小白吃好了
        1658158018519	|	1	|	main	|	小白：结账 -> 把钱给服务员 -> 要求开发票
        1658158018556	|	20	|	ForkJoinPool.commonPool-worker-9	|	服务员收款500元
        1658158018556	|	1	|	main	|	小白：一直在打电话约朋友开黑
        1658158018666	|	21	|	pool-1-thread-1	|	服务员去开发票
        1658158018887	|	1	|	main	|	小白得到500元发票，拿着发票离开餐厅，
        ----------------------------------- */

        /**
         * thenApplyAsync 和 thenCompose 比较像，所以把 thenApplyAsync 换成 thenCompose 也是可以的。
         * 有些场景可能有多种解决方案
         */
    }

    /**
     * 方法 {@link CompletableFuture#thenApply(Function)}
     * 作用：把上一个异步任务的结果交给当前任务的 Function 中
     * 入参：上一个异步任务的执行结果
     * 出参：处理结果
     */
    private static void thenApplyCase() {
        SmallTool.printTimeAndThread("小白吃好了");
        SmallTool.printTimeAndThread("小白：结账 -> 把钱给服务员 -> 要求开发票");

        // 开启新的异步线程（服务员线程）
        CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> {
                    SmallTool.printTimeAndThread("服务员收款500元");
                    SmallTool.sleepMillis(100); // 收款花了100ms
                    return "500";
                })
                .thenApply((money) -> {
                    // 得到500元收款后，另一个线程（服务员）去给小白开发票
                    SmallTool.printTimeAndThread("服务员去开发票");
                    SmallTool.sleepMillis(200); // 开票花了200ms
                    return String.format("%s元发票", money);
                });
        // 在服务员开发票期间，小白一直在打电话约朋友开黑
        SmallTool.printTimeAndThread("小白：一直在打电话约朋友开黑");

        // 等待发票开好后，小白离开餐厅
        SmallTool.printTimeAndThread(String.format("小白得到%s，", cf1.join() + "，拿着发票离开餐厅"));

        /* 执行以上代码，输出结果为：（这是一个服务员的情况）
        --------------------------------------
        1658157796866	|	1	|	main	|	小白吃好了
        1658157796866	|	1	|	main	|	小白：结账 -> 把钱给服务员 -> 要求开发票
        1658157796908	|	20	|	ForkJoinPool.commonPool-worker-9	|	服务员收款500元
        1658157796909	|	1	|	main	|	小白：一直在打电话约朋友开黑
        1658157797015	|	20	|	ForkJoinPool.commonPool-worker-9	|	服务员去开发票
        1658157797236	|	1	|	main	|	小白得到500元发票，拿着发票离开餐厅，
        ----------------------------------- */
    }


    private static void oldCase2() {
        SmallTool.printTimeAndThread("小白吃好了");
        SmallTool.printTimeAndThread("小白：结账 -> 把钱给服务员 -> 要求开发票");
        // 开启新的异步线程（服务员线程）
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("服务员收款500元");
            SmallTool.sleepMillis(100); // 收款花了100ms

            // 得到500元收款后，另一个线程（服务员）去给小白开发票
            CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
                SmallTool.printTimeAndThread("服务员去开发票");
                SmallTool.sleepMillis(200); // 开票花了200ms
                return "500元发票";
            });
            return cf2.join();
        });

        // 在服务员开发票期间，小白一直在打电话约朋友开黑
        SmallTool.printTimeAndThread("小白：一直在打电话约朋友开黑");

        // 等待发票开好后，小白离开餐厅
        SmallTool.printTimeAndThread(String.format("小白得到%s，", cf1.join() + "，拿着发票离开餐厅"));

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658157384195	|	1	|	main	|	小白吃好了
        1658157384195	|	1	|	main	|	小白：结账 -> 把钱给服务员 -> 要求开发票
        1658157384238	|	1	|	main	|	小白：一直在打电话约朋友开黑
        1658157384238	|	20	|	ForkJoinPool.commonPool-worker-9	|	服务员收款500元
        1658157384343	|	21	|	ForkJoinPool.commonPool-worker-2	|	服务员去开发票
        1658157384563	|	1	|	main	|	小白得到500元发票，拿着发票离开餐厅，
        ----------------------------------- */

        /**
         * 存在的问题：第二个服务员的代码居然在第一个服务员内部
         */
    }

    /**
     * 使用之前学过的 API 实现
     */
    private static void oldCase() {
        SmallTool.printTimeAndThread("小白吃好了");
        SmallTool.printTimeAndThread("小白：结账 -> 把钱给服务员 -> 要求开发票");
        // 开启新的异步线程（服务员线程）
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("服务员收款500元");
            SmallTool.sleepMillis(100); // 收款花了100ms
            SmallTool.printTimeAndThread("服务员开发票，面额 500元");
            SmallTool.sleepMillis(200); // 开票花了200ms
            return "500元发票";
        });

        // 在服务员开发票期间，小白一直在打电话约朋友开黑
        SmallTool.printTimeAndThread("小白：一直在打电话约朋友开黑");

        // 等待发票开好后，小白离开餐厅
        SmallTool.printTimeAndThread(String.format("小白得到%s，", cf1.join() + "，拿着发票离开餐厅"));

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658156466024	|	1	|	main	|	小白吃好了
        1658156466024	|	1	|	main	|	小白：结账 -> 把钱给服务员 -> 要求开发票
        1658156466069	|	1	|	main	|	小白：一直在打电话约朋友开黑
        1658156466069	|	20	|	ForkJoinPool.commonPool-worker-9	|	服务员收款500元
        1658156466169	|	20	|	ForkJoinPool.commonPool-worker-9	|	服务员开发票，面额 500元
        1658156466389	|	1	|	main	|	小白得到500元发票，拿着发票离开餐厅，
        ----------------------------------- */

        /**
         * 存在的问题：『收款』和『开发票』是两个操作，我们写在了一起。
         */
    }


}
