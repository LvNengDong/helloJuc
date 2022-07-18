package utils;

import java.util.StringJoiner;

/**
 * @Author lnd
 * @Description 工具类
 * @Date 2022/7/18 10:50
 */
public class SmallTool {

    /**
     * 让当前线程睡眠 millis 毫秒
     * @param millis
     */
    public static void sleepMillis(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印当前线程相关的信息
     *
     * @param tag
     */
    public static void printTimeAndThread(String tag){
        String result = new StringJoiner("\t|\t")
                .add(String.valueOf(System.currentTimeMillis()))
                .add(String.valueOf(Thread.currentThread().getId()))
                .add(Thread.currentThread().getName())
                .add(tag)
                .toString();

        System.out.println(result);
    }

}
