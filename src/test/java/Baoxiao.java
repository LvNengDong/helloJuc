import java.util.Random;

/**
 * @Author lnd
 * @Description
 * @Date 2022/8/16 11:16
 */
public class Baoxiao {
    public static void main(String[] args) {
        int count = 10;
        for (int i = 1; i <= count; i++) {
            Random random = new Random();
            int z = random.nextInt(49)%(50-45+1) + 45;
            int x = random.nextInt(100);
            System.out.println(z + "." + x);
        }

        System.out.println(322 + 110 + 26 + 27);
    }
}
