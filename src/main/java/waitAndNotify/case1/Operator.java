package waitAndNotify.case1;

/**
 * @Author lnd
 * @Description 宗旨：线程 操作 资源类
 * 当前类是一个资源类
 * <p>
 * 线程执行体内流程：判断 -> 干活 -> 唤醒
 * <p>
 * *      实现：两个线程可以操作初始为 0 的一个变量，
 * *      一个线程对变量进行 +1 操作，另一个线程对变量进行 -1 操作。
 * *      加减操作交替执行 10 轮。最后结果仍为 0。
 * @Date 2022/7/18 10:02
 */
public class Operator {
    private int num;

    public int getNum() {
        return num;
    }

    public synchronized void increase() {
        // 判断 -> 干活 -> 唤醒
        // 判断+干活
        if (num == 0) {
            num++;
            System.out.println(Thread.currentThread().getName()+"："+num);
            // 唤醒
            this.notifyAll();
        } else {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void decrease() {
        if (num == 1) {
            num--;
            System.out.println(Thread.currentThread().getName()+"："+num);
            // 唤醒
            this.notifyAll();
        } else {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
