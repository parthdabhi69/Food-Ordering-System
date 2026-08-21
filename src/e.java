class DistanceCalculator {

    private static final String API_KEY = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6ImIxZGM2MzZlZGNjOTRhOWQ4MjkzMzlmMjU2ZDU3Mzk2IiwiaCI6Im11cm11cjY0In0="; // Replace with your actual API key

    public static void main(String[] args) {
        double totalAmount = 8423;
        int order_id = 134;
        String bank = "sbi567";
        String platform = "ipa222y";
        System.out.println("\n  ╔════════════════════════════════════════════╗");
        System.out.println("  ║           *==CASH ON DELIVERY==*           ║");
        System.out.println("  ╠════════════════════════════════════════════╣");
        System.out.printf("  ║  Order Total: %-29s║\n", "₹" + totalAmount * 1.05);
        System.out.println("  ╟────────────────────────────────────────────╢");
        System.out.println("  ║  ► Pay cash when order arrives             ║");
        System.out.println("  ║  ► Payment Status: Pending                 ║");
        System.out.println("  ╚════════════════════════════════════════════╝");
    }
}
