package future.completableFuture;

import utils.SmallTool;

import java.util.concurrent.*;
import java.util.function.BiFunction;

/**
 * @Author lnd
 * @Description 增加难度：
 * 小白进入餐厅点菜后：
 * 1、厨师开始炒菜
 * 2、服务员开始蒸饭
 * 3、小白开始打王者
 * 4、以上三件事都做完后，小白开始吃饭
 * @Date 2022/7/18 11:51
 */
public class _03_thenCombine {

    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            0,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public static void main(String[] args) {
        //supplyAsyncCase();
        thenCombineCase();
        /*
         * 任务之间的关系：
         *   1、并发：两个任务可同时执行
         *   2、依赖：一个任务必须依赖另一个任务的结果（这就会导致任务是串行执行的）
         *   3、互斥：两个任务只能有一个执行成功
         * */
    }

    /**
     * 方法 {@link CompletableFuture#thenCombine(CompletionStage, BiFunction)} 的作用是开启两个异步线程，
     * 让上一个任务和这个任务一起执行，当两个任务都执行完成后，得到两个结果，再把两个结果拼接起来
     */
    private static void thenCombineCase() {
        SmallTool.printTimeAndThread("小白进入餐厅");
        SmallTool.printTimeAndThread("小白点了 番茄炒蛋 + 一碗米饭");

        // 开启一个异步线程让厨师去炒饭
        CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> {
                    SmallTool.printTimeAndThread("厨师炒菜");
                    SmallTool.sleepMillis(200);
                    return "番茄炒蛋";
                })
                // 同时，开启另一个异步线程去蒸饭
                .thenCombine(CompletableFuture.supplyAsync(() -> {
                            SmallTool.printTimeAndThread("服务员蒸饭");
                            SmallTool.sleepMillis(300);
                            return "米饭";
                        }),
                        // 当饭和菜都好了后，服务员打饭
                        (dish, rice) -> {
                            SmallTool.printTimeAndThread("服务员打饭");
                            SmallTool.sleepMillis(100);
                            return String.format("%s + %s 好了", dish, rice);
                        });

        SmallTool.printTimeAndThread("小白在打王者");
        SmallTool.printTimeAndThread(String.format("%s, 小白开吃", cf1.join()));
    }


    /**
     * 使用上一个例子中的 supplyAsync 方法：
     * 小白点完菜后，同时开启两个异步任务，等这两个任务都执行完了，再继续往下走。
     * <p>
     * 但是，在 {@link CompletableFuture} 中，为这种场景也提供了支持。详见 {@link _03_thenCombine#thenCombineCase()}
     */
    private static void supplyAsyncCase() {
        SmallTool.printTimeAndThread("小白进入餐厅");
        SmallTool.printTimeAndThread("小白点了 番茄炒蛋 + 一碗米饭");

        // 开启一个新的线程让厨师去炒饭
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("厨师炒菜");
            SmallTool.sleepMillis(200);
            return "番茄炒蛋";
        });

        // 开启一个新的线程让服务员去蒸饭
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("服务员蒸饭");
            SmallTool.sleepMillis(200);
            SmallTool.printTimeAndThread("服务员打饭");
            SmallTool.sleepMillis(100);
            return "米饭";
        });

        // 小白一直在打王者
        SmallTool.printTimeAndThread("小白在打王者");
        // 等饭和菜都好了后，小白开吃
        String result = String.format("%s + %s 好了", cf1.join(), cf2.join());
        SmallTool.printTimeAndThread(String.format("%s, 小白开吃", result));

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658148685149	|	1	|	main	|	小白进入餐厅
        1658148685149	|	1	|	main	|	小白点了 番茄炒蛋 + 一碗米饭
        1658148685189	|	12	|	ForkJoinPool.commonPool-worker-1	|	厨师炒菜
        1658148685190	|	1	|	main	|	小白在打王者
        1658148685190	|	13	|	ForkJoinPool.commonPool-worker-2	|	服务员蒸饭
        1658148685392	|	13	|	ForkJoinPool.commonPool-worker-2	|	服务员打饭
        1658148685516	|	1	|	main	|	番茄炒蛋 + 米饭 好了, 小白开吃
        ----------------------------------- */
    }
}
