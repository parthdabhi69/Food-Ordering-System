package Customer;

import Restaurant.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class Customer {
    private int userid;
    private String username;
    private String password;
    private String useremail;
    private String phone_no;
    private String address;
    Scanner sc = new Scanner(System.in);

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Customer(int userid, String username, String password, String useremail, String phone_no, String address) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.useremail = useremail;
        this.phone_no = phone_no;
        this.address = address;
    }

    private final HashMap<String, Customer> users = new HashMap<>();
    Customer users_;
    Restaurant restaurant_ = new Restaurant(0,null,null,0,null,null,null,0.0);

    private static final String url = "jdbc:postgresql://localhost:5432/Bitezy";
    private static final String user = "username";
    private static final String password_ = "your password";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password_);
    }

    public void fetchUserData() throws Exception {
        users.clear();
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM customer");
        while (rs.next()) {
            userid = rs.getInt("customer_id");
            username = rs.getString("customer_name");
            password = rs.getString("customer_password");
            useremail = rs.getString("customer_email");
            phone_no = String.valueOf(rs.getLong("customer_phone_no"));
            address = rs.getString("customer_address");

            users_ = new Customer(userid, username, password, useremail, phone_no, address);
            users.put(username, users_);
        }
    }

    public void SignUp() throws Exception {
        fetchUserData();

        boolean validUsername = false, validEmail = false, validPhone = false, validPassword = false, validAddress = false;
        String houseNumber, street, city, state, pinCode;

        while (!validUsername) {
            System.out.print("Username : ");
            username = sc.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("⚠️ Username must not be empty.\n");
            } else if (users.containsKey(username)) {
                System.out.println("⚠️ This username is already taken. Try a different one.\n");
            } else {
                validUsername = true;
            }
        }

        while (!validEmail) {
            System.out.print("Email : ");
            useremail = sc.nextLine().trim();
            if (useremail.isEmpty()) {
                System.out.println("📧 Email must not be empty.\n");
            } else if (!useremail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                System.out.println("📧 Invalid email format (e.g., name@example.com).\n");
            } else {
                validEmail = true;
            }
        }

        while (!validPhone) {
            System.out.print("Phone_no : ");
            phone_no = sc.nextLine().trim();
            if (phone_no.isEmpty()) {
                System.out.println("📱 Phone number must not be empty.\n");
            } else if (!phone_no.matches("^[6-9]\\d{9}$")) {
                System.out.println("📱 Invalid phone number. Must be 10 digits starting with 6-9.\n");
            } else {
                validPhone = true;
            }
        }

        while (!validPassword) {
            System.out.print("Password : ");
            password = sc.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("🔒 Password must not be empty.\n");
            } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
                System.out.println("❌ Invalid Password format!");
                System.out.println("🔒 Must contain:");
                System.out.println("• Minimum 8 characters");
                System.out.println("• At least 1 uppercase letter [A-Z]");
                System.out.println("• At least 1 lowercase letter [a-z]");
                System.out.println("• At least 1 digit [0-9]");
                System.out.println("• At least 1 special character [@ # $ % ^ & + = !]\n");
            } else {
                validPassword = true;
            }
        }

        while (!validAddress) {
            System.out.println("🏠 Please enter your address details:");

            System.out.print("• House/Flat Number: ");
            houseNumber = sc.nextLine().trim();

            System.out.print("• Street/Locality: ");
            street = sc.nextLine().trim();

            System.out.print("• City: ");
            city = sc.nextLine().trim();

            System.out.print("• State: ");
            state = sc.nextLine().trim();

            System.out.print("• PIN Code (6 digits): ");
            pinCode = sc.nextLine().trim();

            if (houseNumber.isEmpty() || street.isEmpty() || city.isEmpty() || state.isEmpty() || pinCode.isEmpty()) {
                System.out.println("❌ All address fields are required.\n");
            } else if (!houseNumber.matches(".*\\d.*")) {
                System.out.println("❌ Invalid House/Flat Number. Must contain at least one digit (e.g., 52, 201 - D, B/12).\n");
            } else if (!houseNumber.matches("^[\\w\\s\\-/]+$")) {
                System.out.println("❌ Invalid characters used. Only letters, numbers, space, hyphen (-), and slash (/) are allowed.\n");
            } else if (!pinCode.matches("\\d{6}")) {
                System.out.println("❌ Invalid PIN Code. It must be exactly 6 digits.\n");
            } else {
                address = houseNumber + ", " + street + ", " + city + ", " + state + " - " + pinCode;
                validAddress = true;
            }
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO customer(customer_name, customer_phone_no, customer_address, customer_password, customer_email) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, username);
            stmt.setLong(2, Long.parseLong(phone_no));
            stmt.setString(3, address);
            stmt.setString(4, password);
            stmt.setString(5, useremail);
            stmt.executeUpdate();

            System.out.println("\n✅ Account created successfully!");
        } catch (SQLException e) {
            System.out.println("!Insert Valid Details");
        }
    }

    public void LogIn() throws Exception {
        fetchUserData();
        System.out.print("Username : ");
        String username = sc.nextLine();
        System.out.print("Password : ");
        String password = sc.nextLine();

        boolean found = false;
        for (Customer u : users.values()) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                System.out.println("==========* Welcome to Bitzy ==========*");
                System.out.println("🕒 Guaranteed Delivery in Under 30 Minutes.\n");
                restaurant_.RestaurantDetails(u.userid);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("⚠️ Invalid Username or Password. Try again!");
        }
    }
}
