package threadLocal.after;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/17 23:04
 */
public class Person {

    private ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public String getName() {
        return threadLocal.get();
    }

    public void setName(String name) {
        threadLocal.set(name);
    }

    public ThreadLocal<String> getThreadLocal() {
        return threadLocal;
    }
}

