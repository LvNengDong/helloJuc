package future.adaptor;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.*;

/**
 * @Author lnd
 * @Description 用于适配 ListenableFuture
 * <p>
 * 使用 ListenableFuture 的 API，底层调用的是 CompletableFuture 中的方法
 * @Date 2022/7/21 20:47
 */
public class CompletableFutureAdaptor<V> implements ListenableFuture<V> {

    private final CompletableFuture<V> completableFuture;

    public CompletableFutureAdaptor(CompletableFuture<V> completableFuture) {
        this.completableFuture = completableFuture;
    }

    /**
     * addListener 方法有两个参数，一个是当任务完成后监听器要执行的处理逻辑（即监听器任务）。另一个是线程池对象
     * @param listener  监听器任务
     * @param executor  线程池对象
     */
    @Override
    public void addListener(Runnable listener, Executor executor) {
        /**
         * whenComplete(BiFunction) 的参数是 BiFunction，意味着如果前面的程序正常执行，那么就会收到正常执行的结果；
         * 如果前面的程序出现异常，就会接收异常结果。并且 whenComplete 方法时没有返回值的，与 addListener 方法很类似
         * 无论前面的程序是正常还是异常，后面的程序都可以继续执行。
         */
        // x 和 y 是两个无用参数，只是为了符合 BiFunction 的编码规则
        completableFuture.whenComplete((x, y) -> {
            // 当任务完成时，执行监听器中的任务
            executor.execute(listener);
        });
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return completableFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return completableFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return completableFuture.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return completableFuture.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return completableFuture.get(timeout, unit);
    }
}
