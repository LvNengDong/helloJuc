package threadLocal.before;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/17 23:18
 */
public class ThreadLocalTest {
    public static void main(String[] args) {

        Person person = new Person();  // 堆中

        new Thread(()->{
            person.setName("张学友");
            try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println("线程T1=========="+ person.getName());
        }, "T1").start();



        new Thread(()->{
            person.setName("乌蝇哥");
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println("线程T2=========="+ person.getName());
        }, "T2").start();
    }
}
