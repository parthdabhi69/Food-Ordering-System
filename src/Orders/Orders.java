package Orders;

import Customer.*;
import Payment.Payment;
import Restaurant.Menu_Items;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Orders{
    private int order_id;
    private int r_id;
    private int customer_id;
    private LocalDateTime order_date;
    private double total_amount;
    private String order_status;
    private String payment_status;

    public Orders(int order_id, int r_id, int customer_id, LocalDateTime order_date, double total_amount, String order_status, String payment_status) {
        this.order_id = order_id;
        this.r_id = r_id;
        this.customer_id = customer_id;
        this.order_date = order_date;
        this.total_amount = total_amount;
        this.order_status = order_status;
        this.payment_status = payment_status;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    Payment payment = new Payment(0,0,null,null,0.0,null,0);

    public void insertOrderDetails(ArrayList<Menu_Items> selecteditems, int R_id, int userid) throws Exception {
        if(!selecteditems.isEmpty()) {
            System.out.print("✅ Done ordering! Proceeding to checkout");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                System.out.print(".");
            }
        }
        else {
            System.out.println("No items selected for ordering.");
            return;
        }

        customer_id = userid;
        r_id = R_id;
        order_date = LocalDateTime.now();
        total_amount = 0.0;

        for (Menu_Items item : selecteditems) {
            total_amount += item.getItem_price();
        }

        order_status = "Pending";
        payment_status = "Pending";
        PreparedStatement stmt = Customer.getConnection().prepareStatement("INSERT INTO orders(r_id,customer_id,order_date,total_amount,order_status,payment_status) VALUES (?,?,?,?,?,?)");
        stmt.setInt(1,r_id);
        stmt.setInt(2,customer_id);
        stmt.setTimestamp(3, Timestamp.valueOf(order_date));
        stmt.setDouble(4,total_amount);
        stmt.setString(5,order_status);
        stmt.setString(6,payment_status);
        stmt.executeUpdate();

        PreparedStatement stmt1 = Customer.getConnection().prepareStatement("SELECT order_id FROM orders where order_date = ?");
        stmt1.setTimestamp(1,Timestamp.valueOf(order_date));
        ResultSet rs = stmt1.executeQuery();
        if (rs.next()){
            this.order_id = rs.getInt("order_id");
        }
        payment.payment(total_amount, order_id, customer_id);
    }
}