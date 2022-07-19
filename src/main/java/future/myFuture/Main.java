package future.myFuture;

import utils.SmallTool;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 17:14
 */
public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        Data data = client.request("Qunar");
        System.out.println("请求完毕");
        /*
        * 这里用一个 sleep 代替对其他业务的处理时间。
        * 在处理这些业务的过程中，realData 被创建完成，从而充分利用了等待时间
        * */
        SmallTool.sleepMillis(1000);
        System.out.println("主业务已经完成了，就等realData回来了");

        // 使用真实的数据，如果此时 realData 还没有准备好，getResult 方法会等待数据准备完毕，再返回
        System.out.println("Result =" + data.getResult());
    }
}
