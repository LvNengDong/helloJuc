package waitAndNotify.case1;

/**
 * @Author lnd
 * @Description
 *      实现：两个线程可以操作初始为 0 的一个变量，
 *      一个线程对变量进行 +1 操作，另一个线程对变量进行 -1 操作。
 *      加减操作交替执行 10 轮。最后结果仍为 0。
 * @Date 2022/7/18 9:57
 *
 */
public class MainApp {
   // 宗旨：线程 操作 资源类

    public static void main(String[] args) {
        // 资源类，所有线程共享
        Operator operator = new Operator();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                operator.increase();
            }
        },"A").start();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                operator.decrease();
            }
        },"B").start();

        // 等待子线程执行完所有操作后，查看结果
        try {
            Thread.sleep(1000);
            System.out.println(operator.getNum());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
