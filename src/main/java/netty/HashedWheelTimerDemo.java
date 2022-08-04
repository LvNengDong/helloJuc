package netty;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import utils.SmallTool;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @Author lnd
 * @Description Hash轮学习
 * @Date 2022/8/4 19:02
 */
public class HashedWheelTimerDemo {

    public static void main(String[] args) {
        // 创建一个 Timer 实例
        Timer timer = new HashedWheelTimer();
        System.out.println(LocalDateTime.now());
        // 提交一个任务，让它在 5s 后执行
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println(LocalDateTime.now());
                SmallTool.printTimeAndThread("5s后执行该任务");
            }
        };
        Timeout timeout1 = timer.newTimeout(task, 5, TimeUnit.SECONDS);

        // 再提交一个任务，让它在 10s 后执行
        TimerTask task2 = new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println(LocalDateTime.now());
                SmallTool.printTimeAndThread("10s后执行该任务");
            }
        };
        Timeout timeout2 = timer.newTimeout(task2, 10, TimeUnit.SECONDS);

        // 取消那个 5s 后执行的任务
        if (!timeout1.isExpired()){
            timeout1.cancel();
        }

        // 原来那个 5s 后执行的任务，已经取消了。但是我们又返回了，想要让这个任务在 3s 后执行。
        timer.newTimeout(timeout1.task(), 3, TimeUnit.SECONDS);
        // 我们知道，timeout 既关联了 timer，也关联了 timeTask，所以这里也可以写成 timeout1.timer()

        /* 执行以上代码，输出结果为：
        --------------------------------------
        2022-08-04T20:04:48.064
        2022-08-04T20:04:51.171
        1659614691173	|	13	|	pool-1-thread-1	|	5s后执行该任务
        2022-08-04T20:04:58.167
        1659614698167	|	13	|	pool-1-thread-1	|	10s后执行该任务
        -----------------------------------
        分析：可以看到，在48s左右任务开始执行，本来正常应该是53s左右执行task1，这里先执行了取消，再重新开启任务，并设置
        3s之后执行，所以任务执行的时间应该在51s左右；另一个任务在58s完成，OK。
        */
    }
}
