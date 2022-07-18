package waitAndNotify.case2;

import waitAndNotify.case1.Operator;

/**
 * @Author lnd
 * @Description 我们在这里修改一下上面的代码，新增两个线程 C 和 D，
 * 让线程 C 负责加操作，线程 D 负责减操作。
 * ABCD 交替执行 10 次。（只修改测试类的代码）
 * @Date 2022/7/18 10:23
 */
public class MainApp2 {
    public static void main(String[] args) {

        // 资源类，所有线程共享
        Operator operator = new Operator();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                operator.increase();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                operator.decrease();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                operator.increase();
            }
        }, "C").start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                operator.decrease();
            }
        }, "D").start();

        // 等待子线程执行完所有操作后，查看结果
        try {
            Thread.sleep(1000);
            System.out.println(operator.getNum());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

