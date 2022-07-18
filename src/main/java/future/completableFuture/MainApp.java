package future.completableFuture;

import utils.SmallTool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/18 10:46
 */
public class MainApp {

    /**
     * 1、小白点菜
     * 2、厨师做饭
     * 3、小白吃饭
     * <p>
     * 典型的 生产者-消费者 模型
     *
     * @param args
     */
    public static void main(String[] args) {
        SmallTool.printTimeAndThread("小白进入餐厅");
        SmallTool.printTimeAndThread("小白点了 番茄炒蛋 + 一碗米饭");

        /**
         * 新开启一个厨师线程去做饭
         *
         * 当我们调用 {@link CompletableFuture#supplyAsync(Supplier)} 方法后，
         * 就会启动一个新的线程去执行 {@link Supplier#get()} 方法中的代码。
         *
         * 注意：这种方法启动的是一个异步的线程
         */
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(
                () -> {
                    SmallTool.printTimeAndThread("厨师炒菜");
                    SmallTool.sleepMillis(200); // 厨师炒菜花了200ms
                    SmallTool.printTimeAndThread("厨师打饭");
                    SmallTool.sleepMillis(100); // 厨师打饭花了100ms
                    return "番茄炒蛋 + 米饭 做好了";
                });

        // 小白线程点完餐后，继续打游戏
        SmallTool.printTimeAndThread("小白在打王者");

        /**
         * 这里 {@link CompletableFuture#join()} 方法的返回值类型就是上面
         * CompletableFuture 的泛型，它的返回值就是 {@link Supplier#get()} 方法的返回值
         *
         * 这个 join 方法就是旧版 Future 接口中 get 方法的升级版，join 方法也会等待任务结束后返回任务的执行结果。
         * 但是，与 get 方法相比，这个 join 方法不需要我们手动 catch 异常。因为其抛出的都是运行期异常，当然，
         * 对于可能出现的运行时异常我们也要做处理，这个我们后续再说。
         */
        SmallTool.printTimeAndThread(String.format("%s, 小白开吃", cf1.join()));
    }

    /* 执行以上代码，输出结果为：
    --------------------------------------
    1658146574640	|	1	|	main	|	小白进入餐厅
    1658146574640	|	1	|	main	|	小白点了 番茄炒蛋 + 一碗米饭
    1658146574700	|	1	|	main	|	小白在打王者
    1658146574700	|	12	|	ForkJoinPool.commonPool-worker-1	|	厨师炒菜
    1658146574907	|	12	|	ForkJoinPool.commonPool-worker-1	|	厨师打饭
    1658146575040	|	1	|	main	|	番茄炒蛋 + 米饭 做好了, 小白开吃
    ----------------------------------- */
}
