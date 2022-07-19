package future.completableFuture;

import utils.SmallTool;

import java.util.concurrent.CompletableFuture;

/**
 * @Author lnd
 * @Description 小白坐上了700路公交，又拿起电话跟朋友聊天，聊得正起劲，
 * 哐当一声，公交撞树上了。
 * 小白碰上这事儿丝毫不慌，从车上下来，从路边拦了个出租车，叫到出租车后顺利回家
 * @Date 2022/7/18 23:54
 */
public class _06_exceptionally {

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
                        }), firstComeBus -> {
                            SmallTool.printTimeAndThread(firstComeBus);
                            if (firstComeBus.startsWith("700")) {
                                throw new RuntimeException(firstComeBus + "，撞树了");
                            }
                            return firstComeBus;
                        })
                /**
                 * 由于我们是在尾部加的 exceptionally ，所以上面链式调用的任何一段出现问题，都会进入 exceptionally 中。
                 * 当然，exceptionally 并不是只能加在尾部，也可以在链式操作的中间加。
                 */
                .exceptionally(e -> {
                    // 出现异常后捕获异常
                    SmallTool.printTimeAndThread(e.getMessage());
                    // 在处理异常的代码中编写出现异常后的备用方案
                    SmallTool.printTimeAndThread("小白叫出租车");
                    return "出租车 叫到了";
                });

        // 700路公交率先赶来，所以小白肯定会上700路
        SmallTool.printTimeAndThread(String.format("%s, 小白坐车回家", cf1.join()));

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658160240092	|	1	|	main	|	小白走出餐厅，来到公交车站
        1658160240093	|	1	|	main	|	等待 700路 或者 800路 公交到来
        1658160240131	|	20	|	ForkJoinPool.commonPool-worker-9	|	700路公交正在赶来
        1658160240131	|	21	|	ForkJoinPool.commonPool-worker-2	|	800路公交正在赶来
        1658160240235	|	20	|	ForkJoinPool.commonPool-worker-9	|	700路公交到了
        1658160240235	|	20	|	ForkJoinPool.commonPool-worker-9	|	java.lang.RuntimeException: 700路公交到了，撞树了
        1658160240235	|	20	|	ForkJoinPool.commonPool-worker-9	|	小白叫出租车
        1658160240249	|	1	|	main	|	出租车 叫到了, 小白坐车回家
        ----------------------------------- */
    }
}
