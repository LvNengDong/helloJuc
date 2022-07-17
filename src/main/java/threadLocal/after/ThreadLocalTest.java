package threadLocal.after;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/17 23:12
 */
public class ThreadLocalTest {

    public static void main(String[] args) {
        Person person = new Person();   // 堆中

        new Thread(() ->{
            person.setName("张学友");  // T1线程的栈中
            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println("线程T1=========="+ person.getName());
            // 防止内存泄露
            person.getThreadLocal().remove();
        }, "T1").start();

        new Thread(() ->{
            person.setName("乌蝇哥");  // T2线程的栈中
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println("线程T2=========="+ person.getName());

            person.getThreadLocal().remove();
        }, "T2").start();
    }
}
