package future.exceptionHandler;

import org.junit.Test;
import utils.SmallTool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/20 11:44
 */
public class OldCase {

    /**
     * 线程池
     */
    ExecutorService threadPool = Executors.newFixedThreadPool(3);

    /**
     * 模拟异常被分支线程吞掉的情况
     */
    @Test
    public void testSingleThread() {

        SmallTool.printTimeAndThread("主线任务开始执行");
        SmallTool.printTimeAndThread("创建一个分支线程");

        // 开启支线任务
        new Thread(() -> {
            SmallTool.printTimeAndThread("支线任务开始执行");
            // 任务执行时间
            SmallTool.sleepMillis(1000);
            // 模拟异常
            int i = 1 / 0;
            SmallTool.printTimeAndThread("支线任务执行成功");
        }).start();
        // 同步执行主线任务
        SmallTool.sleepMillis(10);
        SmallTool.printTimeAndThread("主线任务结束");

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658289136963	|	1	|	main	|	主线任务开始执行
        1658289136963	|	1	|	main	|	创建一个分支线程
        1658289137003	|	12	|	Thread-0	|	支线任务开始执行
        1658289137115	|	1	|	main	|	主线任务结束
        -----------------------------------
        分析：
            我们可以看到，虽然上面这段代码出现了异常，由于异常发生在支线任务中，并不会影响主线任务，所以主线正常结束。
            但对于支线任务来说，虽然启动了任务，但是由于异常导致任务未能成功。正常情况下，异常应该抛出错误信息让程序员
            进行排查，出现这种情况就是我们所说的『异常被吞掉了』
        */
    }

    /**
     * 同理，我们可以用线程池来测试一下，异常是否会被吞掉
     */
    @Test
    public void testExecute() {
        SmallTool.printTimeAndThread("主线任务开始执行");
        SmallTool.printTimeAndThread("创建一个分支线程");
        // 开启支线任务
        threadPool.execute(() -> {
            SmallTool.printTimeAndThread("支线任务开始执行");
            int i = 1 / 0;
            SmallTool.printTimeAndThread("支线任务执行成功");
        });
        // 同步执行主线任务
        SmallTool.sleepMillis(10);
        SmallTool.printTimeAndThread("主线任务结束");
        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658289746344	|	1	|	main	|	主线任务开始执行
        1658289746344	|	1	|	main	|	创建一个分支线程
        1658289746384	|	12	|	pool-1-thread-1	|	支线任务开始执行
        Exception in thread "pool-1-thread-1" java.lang.ArithmeticException: / by zero
            at future.exceptionHandler.OldCase.lambda$test$1(OldCase.java:67)
            at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
            at java.lang.Thread.run(Thread.java:748)
        1658289746409	|	1	|	main	|	主线任务结束
        -----------------------------------
        分析：
            同样，测试用例可以正常执行成果。虽然这次由于线程池默认配置的异常处理策略会将子线程的异常打印在控制台上，但是
            并未对日志做保存、输出等操作
        */
    }

    @Test
    public void testSubmit() {
        SmallTool.printTimeAndThread("主线任务开始执行");
        SmallTool.printTimeAndThread("创建一个分支线程");
        // 开启支线任务
        threadPool.submit(() -> {
            SmallTool.printTimeAndThread("支线任务开始执行");
            int i = 1 / 0;
            SmallTool.printTimeAndThread("支线任务执行成功");
            return i;
        });
        // 同步执行主线任务
        SmallTool.sleepMillis(10);
        SmallTool.printTimeAndThread("主线任务结束");
        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658289946838	|	1	|	main	|	主线任务开始执行
        1658289946838	|	1	|	main	|	创建一个分支线程
        1658289946875	|	12	|	pool-1-thread-1	|	支线任务开始执行
        1658289946887	|	1	|	main	|	主线任务结束
        -----------------------------------
        分析：同样，测试用例执行成功，并且控制台不打印任何异常信息。异常被吞得干干净净。
        */
    }


    /**
     * 通过复写Thread对象的异常处理器UncaughtExceptionHandler处理分支线程的异常
     */
    @Test
    public void test() {
        SmallTool.printTimeAndThread("主线任务开始执行");
        SmallTool.printTimeAndThread("创建一个分支线程");

        // 开启支线任务
        Thread t = new Thread(() -> {
            SmallTool.printTimeAndThread("支线任务开始执行");
            // 模拟异常
            int i = 1 / 0;
            SmallTool.printTimeAndThread("支线任务执行成功");
        });

        // 自定义异常处理器
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                SmallTool.printTimeAndThread(String.format("输出错误日志：线程【%s】出现了【%s】错误", t.getId(), e.getMessage()));
            }
        });

        t.start();
        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658308391426	|	1	|	main	|	主线任务开始执行
        1658308391426	|	1	|	main	|	创建一个分支线程
        1658308391462	|	12	|	Thread-0	|	支线任务开始执行
        1658308391462	|	12	|	Thread-0	|	输出错误日志：线程【12】出现了【/ by zero】错误
        -----------------------------------
        分析：
        */
    }

    @Test
    public void test2() {
        SmallTool.printTimeAndThread("主线任务开始执行");
        SmallTool.printTimeAndThread("创建一个分支线程");

        // 开启支线任务
        new MyThread(() -> {
            SmallTool.printTimeAndThread("支线任务开始执行");
            // 模拟异常
            int i = 1 / 0;
            SmallTool.printTimeAndThread("支线任务执行成功");
        }).start();

        // 同步执行主线任务
        SmallTool.sleepMillis(10);
        SmallTool.printTimeAndThread("主线任务结束");

        /* 执行以上代码，输出结果为：
        --------------------------------------
        1658308689156	|	1	|	main	|	主线任务开始执行
        1658308689156	|	1	|	main	|	创建一个分支线程
        1658308689197	|	12	|	Thread-0	|	支线任务开始执行
        1658308689197	|	12	|	Thread-0	|	输出错误日志：线程【12】出现了【/ by zero】错误
        1658308689216	|	1	|	main	|	主线任务结束
        -----------------------------------
        分析：
        */
    }
}

class MyThread extends Thread {
    public MyThread(Runnable target) {
        super(target);
        this.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                SmallTool.printTimeAndThread(String.format("输出错误日志：线程【%s】出现了【%s】错误", t.getId(), e.getMessage()));
            }
        });
    }
}
