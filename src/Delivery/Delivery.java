package Delivery;

import Customer.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Delivery {
    private int d_id;
    private int r_id;
    private int customer_id;
    private int order_id;
    private String delivery_status;

    public Delivery(int d_id, int r_id, int customer_id, int order_id, String delivery_status) {
        this.d_id = d_id;
        this.r_id = r_id;
        this.customer_id = customer_id;
        this.order_id = order_id;
        this.delivery_status = delivery_status;
    }

    public void insert_delivery(int order_id) throws Exception {
        PreparedStatement statement = Customer.getConnection().prepareStatement("SELECT * FROM orders WHERE order_id = " + order_id);
        ResultSet rs = statement.executeQuery();

        if(rs.next()){
            this.order_id = rs.getInt("order_id");
            r_id = rs.getInt("r_id");
            customer_id = rs.getInt("customer_id");
            delivery_status = "Completed";
        }

        PreparedStatement statement1 = Customer.getConnection().prepareStatement("INSERT INTO delivery(order_id,r_id,customer_id,delivery_status) VALUES(?,?,?,?)");
        statement1.setInt(1,this.order_id);
        statement1.setInt(2,r_id);
        statement1.setInt(3,customer_id);
        statement1.setString(4,delivery_status);
        statement1.executeUpdate();
    }
}