package future.myFuture;

import utils.SmallTool;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/19 16:59
 */
public class RealData implements Data {

    protected final String result;

    public RealData(String para) {
        StringBuffer sb = new StringBuffer();
        sb.append(para);
        SmallTool.sleepMillis(3000);
        // 假设这里很慢很慢，构建 realData 不是一件容易的事
        this.result = sb.toString();
    }

    @Override
    public String getResult() {
        return result;
    }
}
