package Restaurant;

import Customer.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Restaurant {
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

    public Restaurant getRestaurantChoice(ArrayList<Restaurant> tempList, int userid) {
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

                if (isRestaurantOpen(selected)) {
                    break;
                } else {
                    System.out.println("\n❌ This restaurant is currently closed. Please choose another restaurant.");
                    System.out.println("🕒 Operating Hours: " + selected.R_opening_time + " - " + selected.R_closing_time+"\n");
                    selected = null;
                }
            }
            else {
                System.out.println("⚠️ Invalid choice! Please enter a number between 1 and 5 or enter Exit.");
            }
        }
        return selected;
    }

    private boolean isRestaurantOpen(Restaurant restaurant) {
        LocalTime currentTime = LocalTime.now();
        LocalTime openingTime = restaurant.R_opening_time.toLocalTime();
        LocalTime closingTime = restaurant.R_closing_time.toLocalTime();

        if (closingTime.isBefore(openingTime)) {
            return !currentTime.isBefore(openingTime) || !currentTime.isAfter(closingTime);
        } else {
            return !currentTime.isBefore(openingTime) && !currentTime.isAfter(closingTime);
        }
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

        Restaurant selectedRestaurant = getRestaurantChoice(tempList, userid);

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