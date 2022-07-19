package future.completableFuture;

import utils.SmallTool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * @Author lnd
 * @Description 小白走出餐厅后，来到公交车站，准备坐车回家。
 * 有两个路线都能回家，一个是700路，一个是800路。
 * 现在小白决定，哪路先来就坐哪路。
 * <p>
 * 这件事情，如何用代码描述呢？
 * @Date 2022/7/18 23:31
 */
public class _05_applyToEither {
    /**
     * 方{@link CompletableFuture#applyToEither(CompletionStage, Function)}
     * 作用：上个任务和这个任务一起运行，哪个任务先运行完成就把哪个任务的结果交给 Function
     */
    public static void main(String[] args) {
        SmallTool.printTimeAndThread("小白走出餐厅，来到公交车站");
        SmallTool.printTimeAndThread("等待 700路 或者 800路 公交到来");

        CompletableFuture<String> cf1 = CompletableFuture
                .supplyAsync(() -> {
                    SmallTool.printTimeAndThread("700路公交正在赶来");
                    SmallTool.sleepMillis(100);
                    return "700路公交到了";
                })
                .applyToEither(
                        CompletableFuture.supplyAsync(() -> {
                            SmallTool.printTimeAndThread("800路公交正在赶来");
                            SmallTool.sleepMillis(200);
                            return "800路公交到了";
                        }), firstComeBus -> firstComeBus);

        // 700路公交率先赶来，所以小白肯定会上700路
        SmallTool.printTimeAndThread(String.format("%s, 小白坐车回家", cf1.join()));

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658159016676	|	1	|	main	|	小白走出餐厅，来到公交车站
        1658159016676	|	1	|	main	|	等待 700路 或者 800路 公交到来
        1658159016744	|	20	|	ForkJoinPool.commonPool-worker-9	|	700路公交正在赶来
        1658159016745	|	21	|	ForkJoinPool.commonPool-worker-2	|	800路公交正在赶来
        1658159016861	|	1	|	main	|	700路公交到了, 小白坐车回家
        ----------------------------------- */
    }
}
/**
 * 引申场景：
 * 我们在打电话的时候，如果对方 30s 没有接电话，就会自动挂掉。如果对方在 30s 内接通了，就与对方通话。
 * <p>
 * 解决思路：
 * 前一个代码打电话不设定睡眠时间，如果 30s 接通了，就进行通话
 * 后一个代码设置睡眠时间为 30s，如果 30s 没有接通，就执行挂机操作。
 */
