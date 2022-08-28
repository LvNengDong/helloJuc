package future.completableFuture;

import utils.SmallTool;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @Author lnd
 * @Description 一般餐厅里厨师只负责炒菜，像打饭这种事情，一般都会交给服务员。所以这里会出现第三个线程（服务员线程）
 * @Date 2022/7/18 11:16
 */
public class _02_thenCompose {

    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            0,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());


    public static void main(String[] args) {
        //supplyAsyncCase();
        //thenComposeCase();
        thenComposeCaseNew();
    }


    /**
     * 使用线程池对上一个例子进行改写
     */
    private static void thenComposeCaseNew() {
        SmallTool.printTimeAndThread("小白进入餐厅");
        SmallTool.printTimeAndThread("小白点了 番茄炒蛋 + 一碗米饭");

        // 开启一个新的线程让厨师去炒饭
        CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> {
                    SmallTool.printTimeAndThread("厨师炒菜");
                    SmallTool.sleepMillis(200);
                    return "番茄炒蛋";
                }, threadPool)
                .thenCompose( // 串行
                        // 开启一个新的线程让服务员去打饭（在前一个任务执行完后，下一个任务才会触发）
                        preResult -> CompletableFuture.supplyAsync(() -> {
                            SmallTool.printTimeAndThread("服务员打饭");
                            SmallTool.sleepMillis(100);
                            return preResult + " + 米饭";
                        }));

        SmallTool.printTimeAndThread("小白在打王者");
        SmallTool.printTimeAndThread(String.format("%s, 小白开吃", cf1.join()));

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658148306133	|	1	|	main	|	小白进入餐厅
        1658148306133	|	1	|	main	|	小白点了 番茄炒蛋 + 一碗米饭
        1658148306184	|	12	|	pool-1-thread-1	|	厨师炒菜
        1658148306184	|	1	|	main	|	小白在打王者
        1658148306385	|	13	|	ForkJoinPool.commonPool-worker-1	|	服务员打饭
        1658148306507	|	1	|	main	|	番茄炒蛋 + 米饭, 小白开吃
        ----------------------------------- */
    }


    /**
     * 对于这种『第一个任务结束后再开启第二个』的场景（即两个任务间有依赖关系），{@link CompletableFuture} 提供了更方便的方式，
     * <p>
     * 方法 {@link CompletableFuture#thenCompose(Function)} 可以轻松实现这种场景。
     * <p>
     * 这个方法的作用是：等待第一个阶段的任务完成后，将第一阶段的返回结果传给下一个阶段，并开始执行下一个阶段的任务
     */
    private static void thenComposeCase() {
        SmallTool.printTimeAndThread("小白进入餐厅");
        SmallTool.printTimeAndThread("小白点了 番茄炒蛋 + 一碗米饭");

        // 开启一个新的异步线程让厨师去炒饭
        CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> {
                    SmallTool.printTimeAndThread("厨师炒菜");
                    SmallTool.sleepMillis(200);
                    return "番茄炒蛋";
                })
                .thenCompose( // 异步线程执行串行任务
                        // 开启一个新的异步线程让服务员去打饭（在前一个任务执行完后，下一个任务才会触发）
                        preResult -> CompletableFuture.supplyAsync(() -> {
                            SmallTool.printTimeAndThread("服务员打饭");
                            SmallTool.sleepMillis(100);
                            return preResult + " + 米饭";
                        }));

        SmallTool.printTimeAndThread("小白在打王者");
        SmallTool.printTimeAndThread(String.format("%s, 小白开吃", cf1.join()));
        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658147771061	|	1	|	main	|	小白进入餐厅
        1658147771061	|	1	|	main	|	小白点了 番茄炒蛋 + 一碗米饭
        1658147771098	|	12	|	ForkJoinPool.commonPool-worker-1	|	厨师炒菜
        1658147771098	|	1	|	main	|	小白在打王者
        1658147771304	|	12	|	ForkJoinPool.commonPool-worker-1	|	服务员打饭
        1658147771425	|	1	|	main	|	番茄炒蛋 + 米饭, 小白开吃
        ----------------------------------- */

        /**
         * {@link CompletableFuture} 底层应该有默认的线程池，因此对于这段代码来说，可能存在线程复用的问题。
         * 即厨师线程在炒完菜后，这个线程ID又被服务员线程拿到了，这样就会出现炒菜的线程和打饭的线程是同一个线程的问题，
         * 但代码是OK的，只是造成展示输出的效果不明显。
         *
         * 为了解决这个问题，我们可以引入自定义的线程池，这个线程池中只有一个线程，并且用完后立即销毁，这样每次处理任务的线程号就是不同的了。
         * 具体代码见 {@link _02_thenCompose#thenComposeCaseNew()}
         */
    }


    /**
     * 利用上一个例子中的知识，我们可以在厨师线程中再开启一个『异步』的服务员线程用于打饭。
     */
    private static void supplyAsyncCase() {
        SmallTool.printTimeAndThread("小白进入餐厅");
        SmallTool.printTimeAndThread("小白点了 番茄炒蛋 + 一碗米饭");

        // 开启一个新的异步线程让厨师去炒饭
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            SmallTool.printTimeAndThread("厨师炒菜");
            SmallTool.sleepMillis(200); // 厨师炒菜花了200ms

            // 厨师炒完菜后，再开启一个新的异步线程让服务员去打饭（注意：厨师炒完菜后服务员才去打饭，虽然使用了两个不同的线程，但任务的顺序是串行的）
            CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
                SmallTool.printTimeAndThread("服务员打饭");
                SmallTool.sleepMillis(100); // 服务员打饭花了100ms
                return " + 米饭";
            });

            // 厨师准备好『番茄炒蛋』，等待服务员打好『米饭』后，就可以端给小白吃了
            return "番茄炒蛋 + " + cf2.join() + "做好了";
        });

        // 在准备菜和饭期间，小白一直在打王者荣耀
        SmallTool.printTimeAndThread("小白在打王者");
        // 等饭和菜上来后（注意：这里饭和菜是一起上来的），小白开吃
        SmallTool.printTimeAndThread(String.format("%s, 小白开吃", cf1.join()));

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658114566242	|	1	|	main	|	小白进入餐厅
        1658114566244	|	1	|	main	|	小白点了 番茄炒蛋 + 一碗米饭
        1658114566293	|	1	|	main	|	小白在打王者
        1658114566293	|	12	|	ForkJoinPool.commonPool-worker-1	|	厨师炒菜
        1658114566507	|	13	|	ForkJoinPool.commonPool-worker-2	|	服务员打饭
        1658114566636	|	1	|	main	|	番茄炒蛋 +  + 米饭做好了, 小白开吃
        ----------------------------------- */
    }
}
