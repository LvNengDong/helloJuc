package transform;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/20 19:46
 */
public class Transform<V> {

    private ListenableFuture<V> listenableFuture;
    private CompletableFuture<V> completableFuture;


    public ListenableFuture CompletableFutureToListenableFuture(){

        return null;
    }

    public CompletableFuture<V> listenableFutureToCompletableFuture(ListenableFuture<V> listenableFuture){

        return null;
    }

}
