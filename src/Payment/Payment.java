package Payment;

import Customer.*;
import Delivery.Delivery;
import Orders.Orders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Payment{
    private int payment_id;
    private int order_id;
    private LocalDateTime payment_date;
    private String payment_method;
    private double payment_amount;
    private String payment_status;
    private int Customer_id;

    public Payment(int payment_id, int order_id, LocalDateTime payment_date, String payment_method, double payment_amount, String payment_status, int Customer_id) {
        this.payment_id = payment_id;
        this.order_id = order_id;
        this.payment_date = payment_date;
        this.payment_method = payment_method;
        this.payment_amount = payment_amount;
        this.payment_status = payment_status;
        this.Customer_id = Customer_id;
    }

    Scanner sc = new Scanner(System.in);
    Delivery delivery = new Delivery(0,0,0,0,null);
    int counter;
    public boolean CardPayment(double totalAmount, int order_id, int Customer_id) throws Exception {

        System.out.print("  Enter Card Number (16 digits): ");
        String cardNumber = sc.nextLine();

        System.out.print("  Enter Expiry Date (MM/YY): ");
        String expiry = sc.nextLine();

        System.out.print("  Enter CVV (3 digits): ");
        String cvv = sc.nextLine();

        System.out.print("  Enter Name on Card: ");
        String name = sc.nextLine();

        System.out.print("  Enter OTP (1234 for demo): ");
        String otp = sc.nextLine();

        System.out.print("\n  Processing payment");
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1000);
            System.out.print(".");
        }
        System.out.println();

        if (validateCard(cardNumber, expiry, cvv) && otp.equals("1234")) {
            completePayment(order_id, totalAmount, "Credit/Debit Card",Customer_id);
            System.out.println("\n  ╔════════════════════════════════════════════╗");
            System.out.println("  ║            ✅ PAYMENT SUCCESSFUL!          ║");
            System.out.println("  ╠════════════════════════════════════════════╣");
            System.out.println("  ║  Thank you for your order!                 ║");
            System.out.println("  ╚════════════════════════════════════════════╝");

            if(counter == 0){
                System.out.println("Wait for order to arrive");
                for (int i = 0; i < 5; i++){
                    Thread.sleep(500);
                    System.out.print(".");
                }

                System.out.println("\n  ╔════════════════════════════════════════════╗");
                System.out.println("  ║           *==ORDER HAS ARRIVED!==*         ║");
                System.out.println("  ╠════════════════════════════════════════════╣");
                System.out.printf("  ║  Order ID: %-31s ║\n", order_id);
                System.out.printf("  ║  Amount Due: %-29s ║\n", "₹" + totalAmount * 1.05);
                System.out.println("  ╟────────────────────────────────────────────╢");
                System.out.println("  ║  Thank you for shopping with us!           ║");
                System.out.println("  ╚════════════════════════════════════════════╝");
            }

            return false;

        } else {
            System.out.println("  ❌ PAYMENT FAILED  ");
        }
        return true;
    }

    public boolean validateCard(String cardNumber, String expiry, String cvv) {
        return cardNumber.matches("\\d{16}") &&
                expiry.matches("(0[1-9]|1[0-2])/\\d{2}") &&
                cvv.matches("\\d{3}");
    }

    public boolean cash_on_delivery(double totalAmount, int order_id, int Customer_id) throws Exception {
        System.out.println("\n  ╔════════════════════════════════════════════╗");
        System.out.println("  ║           *==CASH ON DELIVERY==*           ║");
        System.out.println("  ╠════════════════════════════════════════════╣");
        System.out.printf("  ║  Order Total: %-29s║\n", "₹" + totalAmount * 1.05);
        System.out.println("  ╟────────────────────────────────────────────╢");
        System.out.println("  ║  ► Payment Status: Pending                 ║");
        System.out.println("  ╚════════════════════════════════════════════╝");

        if(counter == 0) {
            System.out.println("Wait for order to arrive");
            for (int i = 0; i < 5; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }

            System.out.println("\n  ╔════════════════════════════════════════════╗");
            System.out.println("  ║           *==ORDER HAS ARRIVED!==*         ║");
            System.out.println("  ╠════════════════════════════════════════════╣");
            System.out.printf("  ║  Order ID: %-31s ║\n", order_id);
            System.out.printf("  ║  Amount Due: %-29s ║\n", "₹" + totalAmount * 1.05);
            System.out.println("  ╟────────────────────────────────────────────╢");
            System.out.println("  ║  Please pay the amount to the delivery     ║");
            System.out.println("  ╟────────────────────────────────────────────╢");
            System.out.println("  ║  Thank you for shopping with us!           ║");
            System.out.println("  ╚════════════════════════════════════════════╝");
            counter++;
        }
        while (true) {
            System.out.println("\n  Confirm COD Order:");
            System.out.println("  1. Pay");
            System.out.println("  2. Cancel");

            System.out.print("  Enter choice (1-2): ");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                int amount = (int)Math.round(totalAmount * 1.05);
                System.out.print("Enter Amount(₹" + amount +  ") : ");

                try {
                    int Cash_Amount = Integer.parseInt(sc.nextLine());
                    if(Cash_Amount != amount){
                        System.out.println("Please pay exact amount (₹" + amount + ")");
                    }
                    else{
                        payment_date = LocalDateTime.now();
                        payment_method = "Cash on Delivery";
                        payment_amount = totalAmount * 1.05;
                        payment_status = "Completed";
                        this.order_id = order_id;
                        this.Customer_id = Customer_id;

                        PreparedStatement stmt = Customer.getConnection().prepareStatement(
                                "INSERT INTO payment_details(order_id, payment_date, payment_method, payment_amount, payment_status, Customer_id) VALUES (?, ?, ?, ?, ?, ?)");
                        stmt.setInt(1, order_id);
                        stmt.setTimestamp(2, Timestamp.valueOf(payment_date));
                        stmt.setString(3, payment_method);
                        stmt.setDouble(4, payment_amount);
                        stmt.setString(5, payment_status);
                        stmt.setInt(6, this.Customer_id);
                        stmt.executeUpdate();

                        PreparedStatement orderStmt = Customer.getConnection().prepareStatement("UPDATE orders SET payment_status = 'Paid' , order_status = 'Delivered' WHERE order_id = ?");
                        orderStmt.setInt(1, order_id);
                        orderStmt.executeUpdate();

                        delivery.insert_delivery(order_id);

                        System.out.println("\n  ╔════════════════════════════════════════════╗");
                        System.out.println("  ║            ✅ PAYMENT SUCCESSFUL!          ║");
                        System.out.println("  ╠════════════════════════════════════════════╣");
                        System.out.println("  ║  Thank you for your order!                 ║");
                        System.out.println("  ╚════════════════════════════════════════════╝");
                        return false;
                    }
                }
                catch (Exception e) {
                    System.out.println(" Enter only number without decimal and letters.");
                }
            }
            else if (choice.equals("2")) {
                return true;
            }
            else {
                System.out.println("  ❌ Invalid choice. Please enter 1 or 2.");
            }
        }
    }
    public boolean UPIPayment(double totalAmount, int order_id, int Customer_id) throws Exception {
        System.out.println("\n  Select UPI Platform:");
        System.out.println("  1. Google Pay");
        System.out.println("  2. PhonePe");
        System.out.println("  3. Paytm");
        System.out.println("  4. Cancel Payment");

        boolean Check_PlateForm_Choice = true;
        String platform = "";
        while (Check_PlateForm_Choice) {
            System.out.print("  Enter choice (1-4): ");
            String platformChoice = sc.nextLine();
            switch (platformChoice) {
                case "1": {
                    platform = "Google Pay";
                    Check_PlateForm_Choice = false;
                    break;
                }
                case "2": {
                    platform = "PhonePe";
                    Check_PlateForm_Choice = false;
                    break;
                }
                case "3": {
                    platform = "Paytm";
                    Check_PlateForm_Choice = false;
                    break;
                }
                case "4": {
                    System.out.print("\n Canceling payment");
                    for (int i = 0; i < 3; i++) {
                        Thread.sleep(1000);
                        System.out.print(".");
                    }
                    return true;
                }
                default: {
                    System.out.println("  ❌ Invalid choice. Please enter 1-4.");
                    break;
                }
            }
        }

        System.out.println("\n ╔════════════════════════════════════════════╗");
        System.out.println(" ║          UPI PAYMENT REQUEST               ║");
        System.out.println(" ╠════════════════════════════════════════════╣");
        System.out.println(" ║  Merchant: Bitezy Restaurant               ║");
        System.out.println(" ║  UPI ID: bitezy@examplebank                ║");
        System.out.printf(" ║  Amount: ₹%-33s║\n", totalAmount * 1.05);
        System.out.println(" ╚════════════════════════════════════════════╝");

        System.out.println("\n  Select Your Bank:");
        System.out.println("  1. State Bank of India (SBI)");
        System.out.println("  2. HDFC Bank");
        System.out.println("  3. ICICI Bank");
        System.out.println("  4. Go Back");

        boolean Check_Bank_Choice = true;
        String bank = "";
        while (Check_Bank_Choice) {
            System.out.print("  Enter choice (1-4): ");
            String bankChoice = sc.nextLine();
            switch (bankChoice) {
                case "1": {
                    bank = "SBI";
                    Check_Bank_Choice = false;
                    break;
                }
                case "2": {
                    bank = "HDFC";
                    Check_Bank_Choice = false;
                    break;
                }
                case "3": {
                    bank = "ICICI";
                    Check_Bank_Choice = false;
                    break;
                }
                case "4": {
                    return UPIPayment(totalAmount, order_id, Customer_id);
                }
                default: {
                    System.out.println("  ❌ Invalid choice. Please enter 1-4.");
                    break;
                }
            }
        }

        int attempts = 3;
        while (attempts > 0) {
            System.out.print("  Enter 6-digit UPI PIN (" + attempts + " attempts left): ");
            String upiPin = sc.nextLine();

            if (!upiPin.equals("123456")) {
                attempts--;
                System.out.println("  ❌ Incorrect PIN. " + (attempts > 0 ? "Please try again." : ""));
                continue;
            }

            System.out.print("\n  Processing payment");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                System.out.print(".");
            }

            completePayment(order_id, totalAmount, platform + " (UPI)",Customer_id);

            System.out.println("\n ╔════════════════════════════════════════════╗");
            System.out.println(" ║          *==PAYMENT SUCCESSFUL!==*         ║");
            System.out.println(" ╠════════════════════════════════════════════╣");
            System.out.printf(" ║  Platform: %-31s ║\n", platform + " (UPI)");
            System.out.printf(" ║  Bank: %-35s ║\n", bank);
            System.out.printf(" ║  Amount: ₹%-32s ║\n", totalAmount * 1.05);
            System.out.println(" ║                                            ║");
            System.out.println(" ║  Thank you for your order!                 ║");
            System.out.println(" ╚════════════════════════════════════════════╝");

            if(counter == 0){
                System.out.println("Wait for order to arrive");
                for (int i = 0; i < 5; i++){
                    Thread.sleep(500);
                    System.out.print(".");
                }

                System.out.println("\n  ╔════════════════════════════════════════════╗");
                System.out.println("  ║           *==ORDER HAS ARRIVED!==*         ║");
                System.out.println("  ╠════════════════════════════════════════════╣");
                System.out.printf("  ║  Order ID: %-31s ║\n", order_id);
                System.out.printf("  ║  Amount Due: %-29s ║\n", "₹" + totalAmount * 1.05);
                System.out.println("  ╟────────────────────────────────────────────╢");
                System.out.println("  ║  Thank you for shopping with us!           ║");
                System.out.println("  ╚════════════════════════════════════════════╝");
            }

            return false;
        }

        System.out.println("  ❌ Payment failed. Too many incorrect attempts.");
        return true;
    }
    private void completePayment(int order_id, double amount, String method, int Customer_id) throws Exception {
        payment_date = LocalDateTime.now();
        payment_method = method;
        payment_amount = amount * 1.05;
        payment_status = "Completed";
        this.order_id = order_id;
        this.Customer_id = Customer_id;

        PreparedStatement stmt = Customer.getConnection().prepareStatement("INSERT INTO payment_details(order_id, payment_date, payment_method, payment_amount, payment_status, Customer_id) VALUES (?, ?, ?, ?, ?, ?)");

        stmt.setInt(1, this.order_id);
        stmt.setTimestamp(2, Timestamp.valueOf(payment_date));
        stmt.setString(3, payment_method);
        stmt.setDouble(4, payment_amount);
        stmt.setString(5, payment_status);
        stmt.setInt(6, this.Customer_id);

        stmt.executeUpdate();

        PreparedStatement orderStmt = Customer.getConnection().prepareStatement("UPDATE orders SET payment_status = 'Paid' , order_status = 'Delivered' WHERE order_id = ?");
        orderStmt.setInt(1, order_id);
        orderStmt.executeUpdate();
        delivery.insert_delivery(order_id);
    }

    public void payment(double totalAmount, int order_id, int Customer_id) throws Exception {
        counter = 0;
        System.out.println("\n");
        System.out.println("  ╔════════════════════════════════════════════╗");
        System.out.println("  ║         *==SELECT PAYMENT METHOD==*        ║");
        System.out.println("  ╠════════════════════════════════════════════╣");
        System.out.printf ("  ║  Subtotal: ₹%-30.2f ║\n", totalAmount);
        System.out.printf ("  ║  GST (5%%): ₹%-30.2f ║\n", totalAmount * 0.05);
        System.out.println("  ╟────────────────────────────────────────────╢");
        System.out.printf ("  ║  Total: ₹%-30.2f    ║\n", totalAmount * 1.05);
        System.out.println("  ╟────────────────────────────────────────────╢");
        System.out.println("  ║  1. 💳 Credit/Debit Card                   ║");
        System.out.println("  ║  2. 📱 UPI Payment                         ║");
        System.out.println("  ║  3. 💵 Cash on Delivery                    ║");
        System.out.println("  ║  4. 🚪 Exit to Main Menu                   ║");
        System.out.println("  ╚════════════════════════════════════════════╝");


        boolean payment_choice_ = true;
        while(payment_choice_){
            System.out.print("\n  👉 Enter your choice (Payment Method): ");
            String payment_choice = sc.nextLine();
            switch (payment_choice){
                case "1": {
                    payment_choice_ = CardPayment(totalAmount, order_id, Customer_id);
                    break;
                }
                case "2": {
                    payment_choice_ = UPIPayment(totalAmount, order_id, Customer_id);
                    break;
                }
                case "3": {
                    payment_choice_ = cash_on_delivery(totalAmount, order_id, Customer_id) ;
                    break;
                }
                case "4": {
                    System.out.println("\n  ╔════════════════════════════════════════════╗");
                    System.out.println("  ║         *==ORDER CANCELLATION==*           ║");
                    System.out.println("  ╠════════════════════════════════════════════╣");
                    System.out.println("  ║                                            ║");
                    System.out.println("  ║  Are you sure you want to cancel?          ║");
                    System.out.println("  ║  1. Yes, cancel my order                   ║");
                    System.out.println("  ║  2. No, continue payment                   ║");
                    System.out.println("  ║                                            ║");
                    System.out.println("  ╚════════════════════════════════════════════╝");
                    while (true) {
                        System.out.print("\n  👉 Enter your choice (Y/N): ");

                        String confirm = sc.nextLine();
                        if (confirm.equalsIgnoreCase("Y")) {
                            try (Connection conn = Customer.getConnection();
                                 PreparedStatement st = conn.prepareStatement(
                                         "DELETE FROM orders WHERE order_id = ?")) {
                                st.setInt(1, order_id);
                                int rowsDeleted = st.executeUpdate();

                                if (rowsDeleted > 0) {
                                    System.out.println("\n  🗑️ Your Order has been cancelled");
                                    System.out.print("  🚪 Returning to main menu");
                                    for (int i = 0; i < 3; i++) {
                                        Thread.sleep(1000);
                                        System.out.print(".");
                                    }
                                    System.out.println();
                                    payment_choice_ = false;
                                    break;
                                }
                            } catch (SQLException e) {
                                System.out.println("\n  ❌ Error cancelling order: " + e.getMessage());
                            }
                        } else if (confirm.equalsIgnoreCase("N")) {
                            System.out.println("\n  ↪ Continuing with payment...");
                            break;
                        } else {
                            System.out.println("Please enter valid choice (Y/N)");
                        }
                    }
                    break;
                }
                default:{
                    System.out.println("Enter Valid choice (1-4)");
                    break;
                }
            }
        }
    }
}