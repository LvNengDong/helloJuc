package transform;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/20 20:01
 */
public class FutureAdapter<V> {

    private ListenableFuture<V> listenableFuture;
    private CompletableFuture<V> completableFuture;

    public FutureAdapter(ListenableFuture<V> listenableFuture) {
        this.listenableFuture = listenableFuture;
    }

    public FutureAdapter(ListenableFuture<V> listenableFuture, Executor executor) {
        this.listenableFuture = listenableFuture;
        this.completableFuture = new CompletableFuture<>();
        listenableFuture.addListener(()->{
            V value = null;
            // 从监听器 listenableFuture 中得到任务的执行结果，将这个结果设置到 completableFuture 中
            try {
                // 处理正常的返回结果
                value = listenableFuture.get();
                completableFuture.complete(value);
            } catch (InterruptedException | ExecutionException e) {
                // 处理异常的返回结果
                e.printStackTrace();
                completableFuture.completeExceptionally(e);
            }
            // 如果程序正常执行，但是没有返回孩子，走到这一步
            completableFuture.complete(value);
        }, executor);
    }

    public static void main(String[] args) {

    }

}
