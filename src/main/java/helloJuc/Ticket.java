package helloJuc;

/**
 * @Author lnd
 * @Description
 * @Date 2022/7/17 21:01
 */
public class Ticket {

    /**
     * 车票余量
     */
    private int ticketNum;

    public Ticket(int ticketNum) {
        this.ticketNum = ticketNum;
    }

    /**
     * 如果不使用 synchronized 关键字，会出现重复卖、超卖等情况
     */
    public /*synchronized*/ boolean sellTicket(){
        if (ticketNum < 0){
            System.out.println("票已售罄");
            return false;
        }
        System.out.println("线程" + Thread.currentThread().getName() + "正在卖出第" + ticketNum + "张票");
        ticketNum--;
        return true;
    }
}
