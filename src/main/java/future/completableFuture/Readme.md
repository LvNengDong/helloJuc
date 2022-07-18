- CompletableFuture#supplyAsync(Supplier)  开启异步线程
- CompletableFuture#thenCompose(Function)  连接两个有依赖关系的任务，结果由后一个任务返回
- CompletableFuture#thenCombine(CompletionStage, BiFunction)  用来合并两个并发执行任务的结果，结果由合并函数 BiFunction 返回

![img.png](img.png)