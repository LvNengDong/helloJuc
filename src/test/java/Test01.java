import java.util.HashMap;
import java.util.Objects;

/**
 * @Author lnd
 * @Description 可变类做Map的key的问题
 * 写一个Demo，用可变类做 map 的 key，然后修改 map 的属性，看看会发生什么
 * @Date 2022/7/20 9:45
 */
public class Test01 {

    public static void main(String[] args) {
        // 使用可变对象做Map的key
        HashMap<User, String> map = new HashMap<>();
        User user1 = new User(1);
        User user2 = new User(2);
        User user3 = new User(3);
        map.put(user1, "a");
        map.put(user2, "b");
        map.put(user3, "c");
        System.out.println(user1.hashCode());
        String res = map.get(user1);
        System.out.println(res);

        // 当 user 发生变化时，其 hashCode 也随之发生变化
        user1.setUserId(99);
        System.out.println(user1.hashCode());

        // 重新获取 key=user1 的对象
        String resNew = map.get(user1);
        System.out.println(resNew);

        /* 执行以上代码，输出结果为：
        --------------------------------------
        32
        a
        130
        null
        -----------------------------------
        存在的问题：当修改了可变对象的属性时，其HashCode也随之改变，而HashMap是通过key对象的HashCode来获取对应的value的，
        这就会造成获取到的值为null。
        */
    }
}

class User{
    Integer userId;

    public User(Integer userId) {
        this.userId = userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

}
