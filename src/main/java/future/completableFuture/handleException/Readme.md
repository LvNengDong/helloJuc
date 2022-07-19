# 题目：
异步流程里最难处理也最容易忘记处理的是一些**异常情况**，比如超时。
所以当我们在使用诸如CompletableFuture和Listenablefuture等带有异步属性和功能的API的时候，要注意对于超时的处理。
以CompletableFuture为例，如何保证方法在使用该类的异步API的基础上（不包括使用同步get等方法），对于超时能够做出有效的响应和处理。

# 分析
对于程序中某个线程运行超时，主要存在两种情况：
- Case1：线程正常运行，只是确实这个线程正在执行的任务耗时非常长；
- Case2：线程遇到异常情况，已经挂了。

# 解决方案：
## Case1
1. 我们可以额外添加一个“守护线程”（假设为线程B），这个守护线程执行的时间是我们可以容忍正常线程（假设为线程A）运行的最大时间。 
2. 同时开启这两个异步线程： 
   1. 正常情况下，线程A的运行时间肯定要短于线程B，所以它的执行结果一定先于线程返回；
   2. 在特殊情况下，比如线程由于网络、或出现异常等原因超时了，那么线程B的执行结果就会先于线程A返回。
3. 如果我们从代码中得到了线程B的返回结果，那我们就知道线程A出现问题了。

### 优/缺点：
- 优点：这个方案的优点是：可能有些时候，程序确实是正常运行的，只是处理得比较慢，这个时候程序是不会抛出异常的，只能通过这种方式来进行处理。
- 缺点：但是这个方案的缺点就是：我们只能知道线程A出现问题了，却并不能知道线程A出现了什么问题。


## Case2
对于真正出现异常的情况，可以利用 CompletableFuture 提供的异常处理机制。使用 exceptionally、handle 和 whenComplete 三种方法之中的任一即可。

