import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

class Customer {
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
    private static final String user = "postgres";
    private static final String password_ = "System.out.print";

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
            }
            else if (!houseNumber.matches(".*\\d.*")) {
                System.out.println("❌ Invalid House/Flat Number. Must contain at least one digit (e.g., 52, 201 - D, B/12).\n");
            } else if (!houseNumber.matches("^[\\w\\s\\-/]+$")) {
                System.out.println("❌ Invalid characters used. Only letters, numbers, space, hyphen (-), and slash (/) are allowed.\n");
            }
            else if (!pinCode.matches("\\d{6}")) {
                System.out.println("❌ Invalid PIN Code. It must be exactly 6 digits.\n");
            }
            else {
                address = houseNumber + ", " + street + ", " + city + ", " + state + " - " + pinCode;
                validAddress = true;
            }
        }

        PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO customer(customer_name, customer_phone_no, customer_address, customer_password, customer_email) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, username);
        stmt.setLong(2, Long.parseLong(phone_no));
        stmt.setString(3, address);
        stmt.setString(4, password);
        stmt.setString(5, useremail);
        stmt.executeUpdate();

        System.out.println("\n✅ Account created successfully!");
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
class Restaurant {
    private int R_id;
    private String R_name;
    private String R_address;
    private long R_number;
    private Time R_opening_time;
    private Time R_closing_time;
    private String R_category;
    private Double R_ratings;

    Scanner sc = new Scanner(System.in);

    public Restaurant(int R_id, String R_name, String R_address, long R_number, Time R_openning_time, Time R_closing_time, String R_category, Double R_ratings) {
        this.R_id = R_id;
        this.R_name = R_name;
        this.R_number = R_number;
        this.R_address = R_address;
        this.R_opening_time = R_openning_time;
        this.R_closing_time = R_closing_time;
        this.R_category = R_category;
        this.R_ratings =R_ratings;
    }

    private final ArrayList<Restaurant> restaurant = new ArrayList<>();
    Restaurant restaurant_;
    Menu_Items menu_items = new Menu_Items(0,0,null,null,0.0,null,null);

    public Restaurant getRestaurantChoice(ArrayList<Restaurant> tempList) {
        Restaurant selected = null;

        while (true) {
            System.out.print("👉 Pick a restaurant from the list above using its menu number (1-5) : ");
            String choice = sc.nextLine();

            if (choice.equalsIgnoreCase("Exit")){
                break;
            }
            else if(!choice.matches("^[0-9]+$")){
                System.out.println("⚠️ Invalid choice! Please enter a number between 1 and 5 or enter Exit.");
            }
            else if (Integer.parseInt(choice) >= 1 && Integer.parseInt(choice) <= 5) {
                selected = tempList.get(Integer.parseInt(choice) - 1);
                break;
            }
            else {
                System.out.println("⚠️ Invalid choice! Please enter a number between 1 and 5 or enter Exit.");
            }
        }
        return selected;
    }

    public void fetchRestaurantData() throws Exception {
        restaurant.clear();
        Statement stmt = Customer.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Restaurant");
        while (rs.next()) {
            R_id = rs.getInt("R_id");
            R_name = rs.getString("R_name");
            R_address = rs.getString("R_address");
            R_number = rs.getLong("R_number");
            R_opening_time = Time.valueOf(String.valueOf(rs.getTime("R_opening_time")));
            R_closing_time = Time.valueOf(String.valueOf(rs.getTime("R_closing_time")));
            R_category = rs.getString("R_category");
            R_ratings = rs.getDouble("R_ratings");

            restaurant_ = new Restaurant(R_id, R_name, R_address, R_number, R_opening_time, R_closing_time, R_category, R_ratings);
            restaurant.add(restaurant_);
        }
    }

    public void handleCuisineSelection(String cuisineName, String emoji, int userid, ArrayList<Restaurant> tempList) throws Exception {
        tempList.clear();
        int count = 1;

        System.out.println(emoji + " --- " + cuisineName + " Restaurants Menu --- " + emoji);
        System.out.println("--------------------------------------------------------------");

        for (Restaurant restaurant : restaurant) {
            if (restaurant.R_category.equalsIgnoreCase(cuisineName)) {
                tempList.add(restaurant);
                System.out.printf("🍽️  %d. %s  📍 %s  📞 %d  ⭐ %.1f\n",
                        count++, restaurant.R_name, restaurant.R_address, restaurant.R_number, restaurant.R_ratings);
                System.out.printf("    ⏰ Open: %s - %s\n", restaurant.R_opening_time, restaurant.R_closing_time);
                System.out.println("--------------------------------------------------------------");
            }
        }

        Restaurant selectedRestaurant = getRestaurantChoice(tempList);

        if (selectedRestaurant != null) {
            menu_items.Menu_Items_Details(selectedRestaurant.R_id, userid);
        } else {
            System.out.print("\nReturning to cuisine category menu");
            for (int i = 0; i < 5; i++){
                Thread.sleep(500);
                System.out.print(".");
            }
            System.out.println();
        }
    }

    ArrayList<Restaurant> tempList;

    public void RestaurantDetails(int userid) throws Exception {
        fetchRestaurantData();

        tempList = new ArrayList<>();
        boolean checkCuisineChoice = true;

        while (checkCuisineChoice) {
            displayCuisineCategories();
            System.out.print("\nEnter Choice (Category) : ");
            String CuisineChoice = sc.nextLine();

            switch (CuisineChoice) {
                case "1":  handleCuisineSelection("Indian", "🍛", userid, tempList); break;
                case "2":  handleCuisineSelection("Italian", "🍝", userid, tempList); break;
                case "3":  handleCuisineSelection("Mexican", "🌮", userid, tempList); break;
                case "4":  handleCuisineSelection("Chinese", "🥡", userid, tempList); break;
                case "5":  handleCuisineSelection("Japanese", "🍣", userid, tempList); break;
                case "6":  handleCuisineSelection("Thai", "🍜", userid, tempList); break;
                case "7":  handleCuisineSelection("American", "🍔", userid, tempList); break;
                case "8":  handleCuisineSelection("Mediterranean", "🫒", userid, tempList); break;
                case "9":  handleCuisineSelection("French", "🥖", userid, tempList); break;
                case "10": handleCuisineSelection("Korean", "🍲", userid, tempList); break;
                case "11": handleCuisineSelection("Middle Eastern", "🥙", userid, tempList); break;
                case "12": handleCuisineSelection("Continental", "🧀", userid, tempList); break;
                case "13": handleCuisineSelection("Fast Food", "🍕", userid, tempList); break;
                case "14": handleCuisineSelection("Dessert", "🍨", userid, tempList); break;
                case "15": handleCuisineSelection("Beverages", "🥤", userid, tempList); break;
                case "16": {
                    System.out.print("Exiting");
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(500);
                        System.out.print(".");
                    }
                    System.out.println();
                    checkCuisineChoice = false;
                    break;
                }
                default: {
                    System.out.println("❌ Invalid choice! Please select a valid option from the menu.");
                    System.out.println("💡 Enter the number next to the category to explore restaurants.");
                    break;
                }
            }
        }
    }

    public void displayCuisineCategories() {
        System.out.println("🌍 Explore Global Flavors – Choose a Cuisine Category!");
        System.out.println("======================================================\n");

        System.out.println("1.  🍛 Indian          – Spicy, rich, and full of flavor!");
        System.out.println("2.  🍝 Italian         – Cheesy, creamy, and comforting!");
        System.out.println("3.  🌮 Mexican         – Zesty tacos, burritos, and salsa dance in every bite!");
        System.out.println("4.  🥡 Chinese         – Crispy, saucy, and umami-packed stir-fry heaven!");
        System.out.println("5.  🍣 Japanese        – Elegant sushi, comforting ramen, and delicate flavors!");
        System.out.println("6.  🍜 Thai            – A balance of sweet, spicy, and tangy goodness!");
        System.out.println("7.  🍔 American        – Burgers, fries, and classic comfort like no other!");
        System.out.println("8.  🫒 Mediterranean   – Olives, herbs, and fresh sunny delights from the coast!");
        System.out.println("9.  🥖 French          – Buttery croissants, rich sauces, and fine elegance!");
        System.out.println("10. 🍲 Korean          – Fiery kimchi, sizzling BBQ, and soul-warming stews!");
        System.out.println("11. 🥙 Middle Eastern  – Falafel, hummus, and a feast fit for royalty!");
        System.out.println("12. 🧀 Continental     – Sophisticated European dishes with cheesy indulgence!");
        System.out.println("13. 🍕 Fast Food       – Quick bites, big satisfaction!");
        System.out.println("14. 🍨 Dessert         – Sweet, creamy delights to melt your heart!");
        System.out.println("15. 🥤 Beverages       – Sip into refreshment with every drop!");
        System.out.println("16. ❌ Exit");

        System.out.println("✨ Enter your choice and dive into a world of taste!");
        System.out.println("======================================================\n");
    }
}
class Menu_Items{
    private int Item_id;
    private int R_id;
    private String Item_name;
    private String Item_description;
    private double Item_price;
    private String Item_category;
    private Time Preparation_time;

    public double getItem_price() {
        return Item_price;
    }

    public Menu_Items(int Item_id, int R_id, String Item_name, String Item_description, double Item_price, String Item_category, Time Preparation_time) {
        this.Item_id = Item_id;
        this.R_id = R_id;
        this.Item_name = Item_name;
        this.Item_description = Item_description;
        this.Item_price = Item_price;
        this.Item_category = Item_category;
        this.Preparation_time = Preparation_time;
    }

    Scanner sc = new Scanner(System.in);
    Menu_Items menu_items_;
    Orders orders_ = new Orders(0,0,0,null,0.0,null,null);
    private final ArrayList<Menu_Items> menu_items = new ArrayList<>();

    public void fetchMenu_ItemsData() throws Exception{
        menu_items.clear();

        Statement stmt = Customer.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Menu_items");
        while (rs.next()) {
            Item_id = rs.getInt("Item_id");
            R_id = rs.getInt("R_id");
            Item_name = rs.getString("Item_name");
            Item_description = rs.getString("Item_description");
            Item_price = rs.getDouble("Item_price");
            Item_category = rs.getString("Item_category");
            Preparation_time = rs.getTime("preparation_time");

            menu_items_ = new Menu_Items(Item_id,R_id,Item_name,Item_description,Item_price,Item_category,Preparation_time);
            menu_items.add(menu_items_);
        }
    }
    ArrayList<Menu_Items> selecteditems;
    public void Menu_ItemsChoice(ArrayList <Menu_Items> templist, int R_id, int userid) throws Exception {
        selecteditems = new ArrayList<>();
        String itemChoice;

        while (true) {
            System.out.print("🛒 Pick item (1-10) or Finish to done ordering : ");
            itemChoice = sc.nextLine().trim();

            if (itemChoice.equalsIgnoreCase("Exit")) {
                System.out.print("❌ Ordering cancelled. Returning");
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(1000);
                    System.out.print(".");
                }
                System.out.println();
                return;
            }
            else if (itemChoice.equalsIgnoreCase("Finish")) {
                orders_.insertOrderDetails(selecteditems, R_id, userid);
                break;
            }
            else if(!itemChoice.matches("^[0-9]+$") || Integer.parseInt(itemChoice) > 10){
                System.out.println("⚠️ Invalid choice! Try again or enter Exit.");
            }
            else if (Integer.parseInt(itemChoice) >= 1 && Integer.parseInt(itemChoice) <= 10) {
                selecteditems.add(templist.get(Integer.parseInt(itemChoice)-1));
                System.out.println("✅ You picked item " + itemChoice + ". You can continue ordering.");
            }
        }
    }

    public void Menu_Items_Details(int R_id, int userid) throws Exception {
        fetchMenu_ItemsData();

        boolean found = false;
        int number = 1;

        ArrayList<Menu_Items> templist = new ArrayList<>();
        templist.clear();

        System.out.println("\n╔════════╤══════════════════════╤════════════════════════════════════════════════════════════════════════════════╤══════════╤════════════════╤════════════════╗");
        System.out.printf("║ %-6s │ %-22s │ %-76s │ %-8s │ %-14s │ %-14s ║\n",
                "No.", "Item Name", "Description", "Price", "Category", "Prep Time");
        System.out.println("╠════════╪══════════════════════╪════════════════════════════════════════════════════════════════════════════════╪══════════╪════════════════╪════════════════╣");

        for (Menu_Items item : menu_items) {
            if (item.R_id == R_id) {
                String description = item.Item_description.length() > 76
                        ? item.Item_description.substring(0, 73) + "..."
                        : item.Item_description;

                String prepTime = (item.Preparation_time != null)
                        ? item.Preparation_time.toString()
                        : "00:00:00";

                System.out.printf("║ %-6d │ %-22s │ %-76s │ ₹%-7.2f │ %-14s │ %-14s ║\n",
                        number++, item.Item_name, description, item.Item_price, item.Item_category, prepTime);

                templist.add(item);

                found = true;
            }
        }

        System.out.println("╚════════╧══════════════════════╧════════════════════════════════════════════════════════════════════════════════╧══════════╧════════════════╧════════════════╝");

        if (!found) {
            System.out.println("\n⚠ No items found for this restaurant.");
        }

        Menu_ItemsChoice(templist, R_id, userid);

    }
}
class Orders{
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

    Payment payment = new Payment(0,0,null,null,0.0,null);

    public void insertOrderDetails(ArrayList <Menu_Items> selecteditems, int R_id, int userid) throws Exception {
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
        stmt.setTimestamp(3,Timestamp.valueOf(order_date));
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
        payment.payment(total_amount, order_id);
    }
}
class Payment{
    private int payment_id;
    private int order_id;
    private LocalDateTime payment_date;
    private String payment_method;
    private double payment_amount;
    private String payment_status;

    public Payment(int payment_id, int order_id, LocalDateTime payment_date, String payment_method, double payment_amount, String payment_status) {
        this.payment_id = payment_id;
        this.order_id = order_id;
        this.payment_date = payment_date;
        this.payment_method = payment_method;
        this.payment_amount = payment_amount;
        this.payment_status = payment_status;
    }
    Scanner sc = new Scanner(System.in);
    Delivery delivery = new Delivery(0,0,0,0,null);
    int counter;
    public boolean CardPayment(double totalAmount, int order_id) throws Exception {

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
            completePayment(order_id, totalAmount, "Credit/Debit Card");
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

    public boolean cash_on_delivery(double totalAmount, int order_id) throws Exception {
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

                        PreparedStatement stmt = Customer.getConnection().prepareStatement(
                                "INSERT INTO payment_details(order_id, payment_date, payment_method, payment_amount, payment_status) VALUES (?, ?, ?, ?, ?)");
                        stmt.setInt(1, order_id);
                        stmt.setTimestamp(2, Timestamp.valueOf(payment_date));
                        stmt.setString(3, payment_method);
                        stmt.setDouble(4, payment_amount);
                        stmt.setString(5, payment_status);
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
    public boolean UPIPayment(double totalAmount, int order_id) throws Exception {
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
                    return UPIPayment(totalAmount, order_id);
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

            completePayment(order_id, totalAmount, platform + " (UPI)");

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
    private void completePayment(int order_id, double amount, String method) throws Exception {
        payment_date = LocalDateTime.now();
        payment_method = method;
        payment_amount = amount * 1.05;
        payment_status = "Completed";
        this.order_id = order_id;

        PreparedStatement stmt = Customer.getConnection().prepareStatement("INSERT INTO payment_details(order_id, payment_date, payment_method, payment_amount, payment_status) VALUES (?, ?, ?, ?, ?)");

        stmt.setInt(1, this.order_id);
        stmt.setTimestamp(2, Timestamp.valueOf(payment_date));
        stmt.setString(3, payment_method);
        stmt.setDouble(4, payment_amount);
        stmt.setString(5, payment_status);
        stmt.executeUpdate();

        PreparedStatement orderStmt = Customer.getConnection().prepareStatement("UPDATE orders SET payment_status = 'Paid' , order_status = 'Delivered' WHERE order_id = ?");
        orderStmt.setInt(1, order_id);
        orderStmt.executeUpdate();
        delivery.insert_delivery(order_id);
    }

    public void payment(double totalAmount, int order_id) throws Exception {
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
                    payment_choice_ = CardPayment(totalAmount, order_id);
                    break;
                }
                case "2": {
                    payment_choice_ = UPIPayment(totalAmount,order_id);
                    break;
                }
                case "3": {
                    payment_choice_ = cash_on_delivery(totalAmount,order_id) ;
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
class Delivery {
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
class Run_Bitezy {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Customer signUp_logIn = new Customer(0,null,null,null,null,null);

        System.out.println("1. For SignUp\n2. For LogIn\n3. For Exit");
        boolean check = true;
        while (check) {
            System.out.print("Enter Choice : ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1": {
                    signUp_logIn.SignUp();
                    break;
                }
                case "2": {
                    signUp_logIn.LogIn();
                    break;
                }
                case "3": {
                    System.out.print("Exiting");
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(500);
                        System.out.print(".");
                    }
                    check = false;
                    break;
                }
                default: {
                    System.out.println("❌ Enter valid choice (1-3)");
                    break;
                }
            }
        }
    }
}