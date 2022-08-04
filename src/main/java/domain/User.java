package domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/27 20:15
 */
@AllArgsConstructor
@Data
public class User {
    private int id;
    private String name;
    private int age;
}
