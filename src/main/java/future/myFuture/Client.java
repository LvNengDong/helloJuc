package future.myFuture;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 17:09
 */
public class Client {

    // 这是一个异步方法，返回的 Data 接口是一个 Future
    public Data request(String queryStr){
        FutureData futureData = new FutureData();
        new Thread(()->{
            // realData构建得很慢，所以在单独的线程中进行
            RealData realData = new RealData(queryStr);
            // 将 realData 传递给 futureData
            //setRealData()的时候会notify()等待在这个future上的对象
            futureData.setRealData(realData);
        }).start();
        /*
        * futureData 会立即被返回，但第一时间返回的里面 realData 是没有数据的，
        * realData 数据是子线程执行完成后才被 set 到 futureData 中的
        * */
        return futureData;
    }
}
