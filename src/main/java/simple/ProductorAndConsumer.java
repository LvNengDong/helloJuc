package simple;

/**
 * @Author lnd
 * @Description
 * @Date 2022/8/26 20:38
 */
public class ProductorAndConsumer {

    private volatile int num = 0;

    public void consume() throws InterruptedException {
        // 判断
        if (num == 0) this.wait();
        // 干活
        num--;
        // 通知
        this.notifyAll();
    }

    public void product() throws InterruptedException {
        // 判断
        if (num == 10) this.wait();
        // 干活
        num++;
        // 通知
        this.notifyAll();
    }

    public static void main(String[] args) throws Exception{
        ProductorAndConsumer productorAndConsumer = new ProductorAndConsumer();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    productorAndConsumer.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    0-productorAndConsumer.product();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

