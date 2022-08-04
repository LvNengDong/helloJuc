# HashedWheelTimer

`HashedWheelTimer` 来自于 Netty 的工具类，在 `netty-common` 包中。它用于实现**延时任务**。

在 Dubbo 的源码会大量的使用到 `HashedWheelTimer`。在需要**失败重试的场景中**，它是一个非常方便好用的工具。



## 接口&相关接口

### Timer

在正式学习 `HashedWheelTimer` 之前，我们先来看一下他的接口定义。

`HashedWheelTimer` 是接口 `io.netty.util.Timer` 的实现，从面向接口编程的角度，我们其实不需要关心 HashedWheelTimer，只需要关心接口类 Timer 就可以了。

```java
package io.netty.util;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 开启一个后台线程一次性处理所有的 TimerTasks
 * Schedules {@link TimerTask}s for one-time future execution in a background
 * thread.
 */
public interface Timer {

    /**
     * 创建一个指定的 TimerTask 在指定的延时后执行，也就是创建一个定时任务。
     * @return 与任务关联的句柄
     * 
     */
    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);

    /**
     * 释放此Timer获取的所有资源（在这里，资源包括线程、CPU等），并取消所有已计划但尚未执行的任务。
     * return：与此方法取消的任务关联的句柄
     */
    Set<Timeout> stop();
}
```

从接口的定义中可以看出，`Timer` 接口的作用就是：开启一个后台线程一次性处理所有的 TimerTasks。可以看到，这是一个负数，也就是说，大部分的时候，task 任务都是有多个的，但是它只会被一个线程处理。



这个接口中只有两个方法，第一个方法是：开启一个延时任务（TimerTask），返回一个 Timeout 实例。第二个方法是：取消所有未执行的任务并释放资源，返回值也是 Timeout 实例，当然，由于可能一次性取消多个，所以返回是一个 Set 集合。



那么显然，这个接口相关的类就是 `Timeout` 和 `TimerTask` 了。

### TimeTask

TimerTask 接口非常简单，就一个 `run()` 方法用于执行任务。

```java
package io.netty.util;

import java.util.concurrent.TimeUnit;

public interface TimerTask {
    
    void run(Timeout timeout) throws Exception;
}
```

当然，它这里把 Timeout 实例也传进去了。一般我们 Task 中的 run() 方法，都是习惯于不传入任何参数。当然，这样做的好处是：我们可以提前拿到 timeout，并利用 timeout 来做一些其它的事儿。



### Timeout

Timeout 也是一个接口。

```java
package io.netty.util;

/**
 * 返回 TimerTask 与 Timer 关联的句柄。
 */
public interface Timeout {

    /**
     * 返回与此句柄关联的 Timer 
     */
    Timer timer();

    /**
     * 返回与此句柄关联的 TimerTask
     */
    TimerTask task();

    /**
     * 当且仅当与此句柄关联的 TimerTask 过期时，返回true 。
     */
    boolean isExpired();

    /**
     * 当且仅当与此句柄关联的 TimerTask 被取消时，才返回true 。
     */
    boolean isCancelled();

    /**
     * 尝试取消与此句柄关联的TimerTask 。如果任务已经执行或取消，它将返回而没有副作用。
     *
     * @return 如果取消成功完成，则为 true，否则为 false
     */
    boolean cancel();
}

```

Timeout 接口的作用是：返回 TimerTask 与 Timer 关联的句柄。从这个角度理解，Timeout 是关联 TimerTask 与 Timer 的中间对象。通过这个句柄对象既能拿到 Timer，也能拿到 TimerTask。并可以对这两个关联的对象做一些处理。



----

## HashedWheelTimer

我们先来看一个简单的 HashedWheelTimer 的使用 Demo。

```java
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
```

