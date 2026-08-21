package Main;

import Customer.Customer;

import java.util.Scanner;

class Run_Bitezy {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Customer signUp_logIn = new Customer(0,null,null,null,null,null);

        boolean check = true;
        while (check) {
            System.out.println("1. For SignUp\n2. For LogIn\n3. For Exit");
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