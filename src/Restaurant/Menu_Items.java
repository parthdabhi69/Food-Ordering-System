package Restaurant;

import Customer.*;
import Orders.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Menu_Items{
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

    public String getItem_name() {
        return Item_name;
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

    private void writeOrderToFile(ArrayList<Menu_Items> orderedItems, int R_id, int userid) {
        String fileName = "order_summary.txt";
        File file = new File(fileName);
        boolean fileExists = file.exists();

        try (FileWriter writer = new FileWriter(file, true)) {
            if (!fileExists) {
                writer.write("========================================\n");
                writer.write("           RESTAURANT ORDER SUMMARY     \n");
                writer.write("========================================\n\n");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());

            writer.write("Order Date: " + currentDate + "\n");
            writer.write("User ID: " + userid + "\n");
            writer.write("Restaurant ID: " + R_id + "\n");
            writer.write("----------------------------------------\n");
            writer.write("Ordered Items:\n");

            double total = 0.0;
            for (Menu_Items item : orderedItems) {
                writer.write(String.format("- %-25s ₹%.2f\n", item.getItem_name(), item.getItem_price()));
                total += item.getItem_price();
            }

            writer.write("----------------------------------------\n");
            writer.write(String.format("TOTAL: %26s ₹%.2f\n", "", total));
            writer.write("========================================\n\n");

            System.out.println("✅ Order summary saved to: " + fileName);
        } catch (IOException e) {
            System.out.println("❌ Error writing order to file: " + e.getMessage());
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
                System.out.println();
                return;
            }
            else if (itemChoice.equalsIgnoreCase("Finish")) {
                if (!selecteditems.isEmpty()) {
                    orders_.insertOrderDetails(selecteditems, R_id, userid);
                    writeOrderToFile(selecteditems, R_id, userid);
                } else {
                    System.out.println("⚠️ No items selected. Order cancelled.");
                }
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