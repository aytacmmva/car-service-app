
import repository.OrderRepository;
import repository.RepairerRepository;
import service.OrderService;

import java.util.Scanner;

    public class Main {
        public static void main(String[] args) {
            // Manual Dependency Injection
            OrderRepository orderRepository = new OrderRepository();
            RepairerRepository repairerRepository = new RepairerRepository();
            OrderService orderService = new OrderService(orderRepository, repairerRepository);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Car Service Application Started.");
            System.out.println("Available commands:");
            System.out.println("- order open <price>");
            System.out.println("- order assign <orderId> <repairerId>");
            System.out.println("- order complete <orderId>");
            System.out.println("- order cancel <orderId>");
            System.out.println("- order list <page> <size> <sortBy(id|price|opening_timestamp|completion_timestamp|status)>");
            System.out.println("- exit");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) break;

                String[] parts = input.split("\\s+");
                if (parts.length < 2) continue;

                try {
                    if (parts[0].equalsIgnoreCase("order")) {
                        handleOrderCommands(parts, orderService);
                    } else {
                        System.out.println("Unknown command.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        private static void handleOrderCommands(String[] parts, OrderService service) {
            String action = parts[1].toLowerCase();
            switch (action) {
                case "open":
                    service.openOrder(Double.parseDouble(parts[2]));
                    break;
                case "assign":
                    service.assignRepairer(Long.parseLong(parts[2]), Long.parseLong(parts[3]));
                    break;
                case "complete":
                    service.completeOrder(Long.parseLong(parts[2]));
                    break;
                case "cancel":
                    service.cancelOrder(Long.parseLong(parts[2]));
                    break;
                case "list":
                    int page = Integer.parseInt(parts[2]);
                    int size = Integer.parseInt(parts[3]);
                    String sortBy = parts.length > 4 ? parts[4] : "id";
                    service.listOrders(page, size, sortBy);
                    break;
                default:
                    System.out.println("Unknown order action.");
            }
        }
    }

