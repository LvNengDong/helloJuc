JDK原生的future已经提供了异步操作，但是不能直接回调。（如果想要得到返回结果必须阻塞等待）
guava对future进行了增强，支持回调，在分支线程运行结束得到结果后通知主线程，核心接口就是ListenableFuture。
如果已经开始使用了jdk8，可以直接学习使用原生的CompletableFuture，这是jdk从guava中吸收了精华新增的类。

Guava对jdk的异步增强可以通过看 MoreExecutor 和 Futures 两个类的源码入手，写的并不复杂，没有一层一层的调用，逻辑很清晰，建议读完本文通过这两个类由点到面的理解guava到底做了什么


ListenableFuture继承了Future，额外新增了一个方法，**listener 是任务结束后的回调方法，executor 是执行回调方法的执行器(通常是线程池)**。
guava中对future的增强就是在addListener这个方法上进行了各种各样的封装，所以addListener是核心方法：
`void addListener(Runnable listener, Executor executor);`

jdk原生FutureTask类是对Future接口的实现，guava中 ListenableFutureTask 继承了 FutureTask 并实现了 ListenableFuture，guava异步回调最简单的使用：
见：src/main/java/future/listenableFuture/ListenableFutureDemo.java


一般使用异步模式的时候，都会用一个线程池来提交任务，不会像上面那样简单的开一个线程去做，那样效率太低下了，所以需要说说guava对jdk原生线程池的封装。
guava对原生线程池的增强都在MoreExecutor类中，guava对ExecutorService和 ScheduledExecutorService 的 增强类似，这里只介绍ExecutorService的增强



# Future之allAsList与successfulAsList

1. **transform**：对ListenableFuture 的返回值进行转换
2. **allAsList**：对多个ListenableFuture结果的合并，当所有Future都成功时返回多个Future返回结果组成的 List 对象。
   - 注：当其中一个Future失败或者取消的时候，将会进入失败或者取消。
3. **successfulAsList**：和allAsList相似，唯一差别是对于失败或取消的Future返回值用null代替。不会进入失败或者取消流程。





**可复现**









# Guava 之 ListenableFuture

## 接口

【JDK】

`Future` 代表了异步执行的结果：一个可能还没有产生结果的执行过程。 `Future` 可以正在被执行，但是会保证返回一个结果。



【Guava】

`ListenableFuture` 允许使用者注册一个监听器，在监听器中有一个回调函数，如果你注册了监听器，当任务完成的时候监听器就会调用这个回调函数。

- 回调的时机：当任务执行完成后，会立即触发回调。

这个功能使得 `ListenableFuture` 可以完成很多 `Future` 支持不了的操作。



`ListenableFuture` 添加监听器的 API 是 [`addListener(Runnable, Executor)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/ListenableFuture.html#addListener-java.lang.Runnable-java.util.concurrent.Executor-)。

这个方法的作用是：当当 `Future` 中的任务执行完成时，会触发这个监听器，这个监听器会从线程池中得到一个线程执行自己的 `Runnable` 任务。

这个方法有两个参数：

- Runnable：监听器需要执行的任务
- Executor：线程池







## 添加回调函数



开发人员可以使用 [`Futures.addCallback(ListenableFuture, FutureCallback, Executor)`](https://google.github.io/guava/releases/snapshot/api/docs/com/google/common/util/concurrent/Futures.html#addCallback-com.google.common.util.concurrent.ListenableFuture-com.google.common.util.concurrent.FutureCallback-java.util.concurrent.Executor-) 方法获取到任务的执行结果。

或者可以使用默认为 `MoreExecutors.directExecutor()` 的版本。

`FutureCallback<V>` 实现了两个方法:

- `onSuccess(V)`：当 future 执行成功时的处理逻辑。
- `onFailure(Throwable)`：当 future 执行失败时的处理逻辑。





## 创建

与 JDK 中 通过 `ExecutorService.submit(Callable)` 来开启一个异步的任务相似，Guava 提供了一个 `ListeningExecutorService` 接口，这个接口可以返回一个 `ListenableFuture`（`ExecutorService` 只是返回一个普通的 `Future`）。

如果需要将一个 `ExecutorService` 转换为 `ListeningExecutorService`，可以使用 `MoreExecutors.listeningDecorator(ExecutorService)`『装饰器模式』。一个使用示例如下：

```java
// 将线程池包装为可以监听的线程池，这样就可以对池中的每一个线程进行监听
ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

// 从线程池中分配线程去执行任务，但是主线程不会等待任务执行结束，它会立即得到一个凭证，即 ListenableFuture
ListenableFuture<Explosion> explosion = service.submit(new Callable<Explosion>() {
    public Explosion call() {
        return pushBigRedButton();
    }`
});

// explosion：凭证
// FutureCallback：通过凭证得到结果后（得到结果一定是任务已经执行结束了，要么成功，要么失败），就会触发 FutureCallback 里面的 onSuccess 或 onFailure 事件
Futures.addCallback(explosion, new FutureCallback<Explosion>() {
    // we want this handler to run immediately after we push the big red button!
    public void onSuccess(Explosion explosion) {
        walkAwayFrom(explosion);
    }
    public void onFailure(Throwable thrown) {
        battleArchNemesis(); // escaped the explosion!
    }
});
```

