package future.adaptor;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import javax.annotation.Nullable;
import java.util.concurrent.*;

/**
 * @Author lnd
 * @Description
 *      用 CompletableFuture 中的 API，底层调用的是 ListenableFuture 中的方法
 * @Date 2022/7/21 20:51
 */
public class ListenableFutureAdaptor<V> extends CompletableFuture<V> {


    /**
     * 接收的传入参数是 listenableFuture，这是一个凭证，凭借这个可以取获取任务的执行结果。
     * 获取结果的逻辑使用 ListenableFuture 中的方法
     *
     * @param listenableFuture
     */
    public ListenableFutureAdaptor(ListenableFuture<V> listenableFuture) {
        Futures.addCallback(listenableFuture, new FutureCallback<V>() {
            @Override
            public void onSuccess(@Nullable V result) {
                complete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                completeExceptionally(t);
            }
        }, MoreExecutors.directExecutor());

    }
}
