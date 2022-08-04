package future.future;

import domain.User;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author lnd
 * @Description 主线程与子线程之间操作共享变量
 * @Date 2022/7/27 20:14
 */
public class SubmitTask {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        User user = new User(1, "张三", 23);

        Future<User> future = executor.submit(() -> {
            user.setName("张三丰");
        }, user);

        User user2 = future.get();

        System.out.println(user == user2);  // true
        System.out.println(user.getName()); // 张三丰
    }

    /* 执行以上代码，输出结果为：
    --------------------------------------
        因为主线程和子线程可以操作堆中的同一个共享变量，所以我们可以将主线程中定义好的一个对象
        通过 submit(Runnable task, T result) 方法传入分支线程中，在分支线程中对这个对象
        进行各种计算，最后将这个对象传回主线程，从而完成主线程和分支线程操作同一个共享变量的任务。
    -----------------------------------
    分析：
    */
}
