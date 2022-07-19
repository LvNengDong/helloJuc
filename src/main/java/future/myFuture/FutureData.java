package future.myFuture;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 16:58
 */
public class FutureData implements Data{

    // 内部需要维护 RealData
    protected RealData realData = null;
    protected boolean isReady = false;

    public synchronized void setRealData(RealData realData){
        // 如果 realData 已经有了数据，不做任何处理
        if (isReady){
            return;
        }

        // 如果 realData 还没有被设置进去数据，则注入数据
        this.realData = realData;
        isReady = true;

        // 注入完成后，通知 getResult
        notifyAll();
    }
    @Override
    public synchronized String getResult(){
        // 如果数据没有准备好，阻塞；如果数据准备好了，任务会被唤醒
        while (!isReady){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 从 realData 中获得真正需要的数据
        return realData.result;
    }

}
