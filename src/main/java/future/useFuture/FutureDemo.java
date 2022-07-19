package future.useFuture;

import future.myFuture.RealData;
import utils.SmallTool;

import java.util.concurrent.*;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 17:27
 */
public class FutureDemo {
    // 异步操作会使用多个线程，可以创建一个线程池
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 执行 FutureTask
        Future<String> future = executor.submit(() -> {
            RealData realData = new RealData("Quner2");
            return realData.getResult();
        });

        //这里依然可以做额外的数据操作，这里使用sleep代替其他业务逻辑的处理
        SmallTool.sleepMillis(1000);

    //如果此时call()方法没有执行完成，则依然会等待
        System.out.println("数据 = " + future.get());
    }
}
