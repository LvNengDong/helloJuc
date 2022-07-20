## 作业详情：

在 JDK1.8 问世之前，大部分的异步场景都使用了 Listenablefuture 作为传递介质，我们的代码中也存在大量的 Listenablefuture 定义，无论是作为参数还是作为返回结果。1.8 之后有了CompletableFuture，我们更推荐使用该类作为异步操作的介质，同时大量的开源组件，比如 Spring 在支持异步 mvn 的时候，还有一些 RPC 组件，比如异步 HTTP 客户端在支持异步请求的时候，包括Dubbo 在处理异步请求的时候，都大量使用了 CompletableFuture。

那么我们就面临着一个问题，我们如何将我们旧代码中的 Listenablefuture 平滑迁移为CompletableFuture？

这里有两种需求：

- 一种是，我们需要将新返回的 CompletableFuture 适配为我们的 Listenablefuture，兼容我们之前的一些方法定义。
- 还有一种是，我们要升级我们功能模块，将原来定义为 Listenablefuture 返回值的方法转换为 CompletableFuture 为返回值的方法。

基于这种诉求，我们需要编写一个适配器工具，来实现两种类的转换。



