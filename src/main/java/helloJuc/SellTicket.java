package helloJuc;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/17 21:07
 */
public class SellTicket {

    public static void main(String[] args) {
        Ticket ticket = new Ticket(100);
        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                while (true){
                    boolean b = ticket.sellTicket();
                    if (!b){
                        break;
                    }
                }
            }, String.valueOf(i)).start();
        }
    }
}
