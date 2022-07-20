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









