import java.util.*;
import java.util.stream.Collectors;

// Main class for the Smart Grocery Budget Planner backend
public class BSCS2_RODRIGO_knapsack_binary {

    // Product class to store item details
    static class Product {
        String name, category;
        int price;

        public Product(String name, String category, int price) {
            this.name = name;
            this.category = category;
            this.price = price;
        }

        @Override
        public String toString() {
            return "Name: " + name + " | Category: " + category + " | Price: ₱" + price;
        }
    }

    // Node class for binary decision tree
    static class TreeNode {
        String text; // Display text for the node
        int price; // Price of the current item
        int totalCost; // Accumulated cost up to this node
        int remainingBudget; // Budget left at this node
        List<Product> items; // Items included up to this node
        List<TreeNode> children; // Child nodes
        boolean isLeaf; // Indicates if node is a leaf

        public TreeNode(String text, int price, int totalCost, int remainingBudget, List<Product> items, boolean isLeaf) {
            this.text = text;
            this.price = price;
            this.totalCost = totalCost;
            this.remainingBudget = remainingBudget;
            this.items = new ArrayList<>(items);
            this.children = new ArrayList<>();
            this.isLeaf = isLeaf;
        }
    }

    static List<Product> products = new ArrayList<>();
    static Integer lastBudget = null;
    static List<Product> lastKnapsackResult = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Initialize default products to match frontend
        initializeDefaultProducts();

        // Main menu loop
        boolean running = true;
        while (running) {
            System.out.println("\n==== Smart Grocery Budget Planner ====");
            System.out.println("1. Add Product");
            System.out.println("2. Show All Products");
            System.out.println("3. Filter Products by Category");
            System.out.println("4. Plan with Budget (Knapsack)");
            System.out.println("5. Show Binary Decision Tree");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine(); // Clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    addProductMenu(sc);
                    break;
                case 2:
                    showProducts(products);
                    break;
                case 3:
                    filterProductsMenu(sc);
                    break;
                case 4:
                    knapsackMenu(sc);
                    break;
                case 5:
                    if (lastBudget == null) {
                        System.out.println("Enter a budget first (option 4)");
                        break;
                    }
                    System.out.println("\n--- Binary Decision Tree ---");
                    TreeNode root = buildBinaryTree(products, lastBudget, lastKnapsackResult);
                    printBinaryTree(root, 0);
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
        sc.close();
    }

    // Initialize default products to match frontend data
    static void initializeDefaultProducts() {
        products.add(new Product("Noodles", "Dry Goods", 50));
        products.add(new Product("Chicken", "Meat", 150));
        products.add(new Product("Bacon", "Meat", 118));
        products.add(new Product("Rice", "Grains", 50));
        products.add(new Product("Milk", "Dairy", 60));
        products.add(new Product("Eggs", "Protein", 40));
        products.add(new Product("Bread", "Bakery", 30));
        products.add(new Product("Apples", "Fruits", 45));
        products.add(new Product("Bananas", "Fruits", 35));
        products.add(new Product("Fish", "Meat", 80));
        products.add(new Product("Toothpaste", "Toiletries", 25));
        products.add(new Product("Shampoo", "Toiletries", 70));
        products.add(new Product("Canned Tuna", "Canned Goods", 55));
        products.add(new Product("Coffee", "Beverages", 90));
        products.add(new Product("Soap", "Toiletries", 20));
        products.add(new Product("Cheese", "Dairy", 85));
        products.add(new Product("Tomatoes", "Vegetables", 25));
        products.add(new Product("Onions", "Vegetables", 20));
        products.add(new Product("Lettuce", "Vegetables", 30));
        products.add(new Product("Orange Juice", "Beverages", 75));
        products.add(new Product("Peanut Butter", "Spreads", 65));
        products.add(new Product("Jam", "Spreads", 60));
        products.add(new Product("Yogurt", "Dairy", 50));
        products.add(new Product("Beef", "Meat", 120));
        products.add(new Product("Instant Noodles", "Dry Goods", 15));
        products.add(new Product("Detergent", "Cleaning Supplies", 40));
        products.add(new Product("Dishwashing Liquid", "Cleaning Supplies", 35));
        products.add(new Product("Carrots", "Vegetables", 28));
        products.add(new Product("Cabbage", "Vegetables", 22));
        products.add(new Product("Sardines", "Canned Goods", 38));
    }

    // Menu to add a new product
    static void addProductMenu(Scanner sc) {
        System.out.print("Enter product name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter category: ");
        String category = sc.nextLine().trim();
        System.out.print("Enter price: ");
        int price;
        try {
            price = sc.nextInt();
            sc.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid price! Please enter a number.");
            sc.nextLine(); // Clear invalid input
            return;
        }

        // Validate input
        if (name.isEmpty() || category.isEmpty() || price <= 0) {
            System.out.println("Invalid input! Name and category cannot be empty, and price must be positive.");
            return;
        }

        products.add(new Product(name, category, price));
        System.out.println("Product added successfully.");
    }

    // Display a list of products
    static void showProducts(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("No products available.");
        } else {
            System.out.println("\n--- Product List ---");
            for (Product p : list) {
                System.out.println(p);
            }
        }
    }

    // Menu to filter products by category
    static void filterProductsMenu(Scanner sc) {
        System.out.print("Enter category to filter: ");
        String category = sc.nextLine().trim();
        if (category.isEmpty()) {
            System.out.println("Category cannot be empty.");
            return;
        }
        List<Product> filtered = filterByCategory(products, category);
        showProducts(filtered);
    }

    // Menu to run knapsack algorithm with budget
    static void knapsackMenu(Scanner sc) {
        System.out.print("Enter your budget: ");
        int budget;
        try {
            budget = sc.nextInt();
            sc.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid budget! Please enter a number.");
            sc.nextLine(); // Clear invalid input
            return;
        }

        if (budget <= 0) {
            System.out.println("Budget must be positive.");
            return;
        }

        lastBudget = budget;
        lastKnapsackResult = knapsack(products, budget);

        // Display results
        System.out.println("\n--- Best Items You Can Afford ---");
        showProducts(lastKnapsackResult);
        int totalCost = lastKnapsackResult.stream().mapToInt(p -> p.price).sum();
        System.out.println("Total Cost: ₱" + totalCost + " | Remaining Budget: ₱" + (budget - totalCost));
    }

    // Knapsack algorithm to select optimal items within budget
    static List<Product> knapsack(List<Product> products, int budget) {
        int n = products.size();
        int[][] dp = new int[n + 1][budget + 1];
        boolean[][] selected = new boolean[n + 1][budget + 1];

        // Fill DP table
        for (int i = 1; i <= n; i++) {
            Product p = products.get(i - 1);
            for (int w = 0; w <= budget; w++) {
                if (p.price <= w && p.price + dp[i - 1][w - p.price] >= dp[i - 1][w]) {
                    dp[i][w] = p.price + dp[i - 1][w - p.price];
                    selected[i][w] = true;
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Backtrack to find selected products
        List<Product> result = new ArrayList<>();
        int w = budget;
        for (int i = n; i > 0 && w >= 0; i--) {
            if (selected[i][w]) {
                Product p = products.get(i - 1);
                result.add(p);
                w -= p.price;
            }
        }

        return result;
    }

    // Filter products by category
    static List<Product> filterByCategory(List<Product> products, String category) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.category.equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    // Build binary tree matching frontend's median-based approach
    static TreeNode buildBinaryTree(List<Product> products, int budget, List<Product> selectedItems) {
        // Handle empty or invalid input
        if (selectedItems == null || selectedItems.isEmpty()) {
            String text = String.format("Budget: ₱%d, No Items, Total: ₱0, Remaining: ₱%d", budget, budget);
            return new TreeNode(text, 0, 0, budget, new ArrayList<>(), true);
        }

        // Sort items by price to ensure consistent insertion order
        List<Product> sortedItems = new ArrayList<>(selectedItems);
        sortedItems.sort((a, b) -> a.price - b.price);

        // Start building tree with initial conditions
        return buildBinaryTreeRecursive(products, budget, sortedItems, 0, new ArrayList<>());
    }

    // Recursive helper to build binary tree
    static TreeNode buildBinaryTreeRecursive(List<Product> products, int budget, List<Product> selectedItems,
                                             int totalCost, List<Product> itemsSoFar) {
        if (selectedItems.isEmpty()) {
            String text = String.format("Budget: ₱%d, No Items, Total: ₱%d, Remaining: ₱%d",
                    budget, totalCost, budget - totalCost);
            return new TreeNode(text, 0, totalCost, budget - totalCost, itemsSoFar, true);
        }

        // Choose median item as root to balance tree
        int medianIndex = selectedItems.size() / 2;
        Product rootItem = selectedItems.get(medianIndex);

        // Calculate node properties
        int newTotalCost = totalCost + rootItem.price;
        int remainingBudget = budget - newTotalCost;
        List<Product> newItemsSoFar = new ArrayList<>(itemsSoFar);
        newItemsSoFar.add(rootItem);

        String text = String.format("%s (₱%d), Total: ₱%d, Remaining: ₱%d",
                rootItem.name, rootItem.price, newTotalCost, remainingBudget);
        TreeNode root = new TreeNode(text, rootItem.price, newTotalCost, remainingBudget, newItemsSoFar, false);

        // Split items into left (cheaper) and right (more expensive) subtrees
        List<Product> leftItems = selectedItems.stream()
                .filter(item -> item.price < rootItem.price)
                .collect(Collectors.toList());
        List<Product> rightItems = selectedItems.stream()
                .filter(item -> item.price > rootItem.price ||
                        (item.price == rootItem.price && item != rootItem))
                .collect(Collectors.toList());

        // Recursively build subtrees
        if (!leftItems.isEmpty()) {
            TreeNode leftChild = buildBinaryTreeRecursive(products, budget, leftItems, newTotalCost, newItemsSoFar);
            root.children.add(leftChild);
        }
        if (!rightItems.isEmpty()) {
            TreeNode rightChild = buildBinaryTreeRecursive(products, budget, rightItems, newTotalCost, newItemsSoFar);
            root.children.add(rightChild);
        }

        // Mark as leaf if no children
        if (root.children.isEmpty()) {
            root.isLeaf = true;
        }

        return root;
    }

    // Print binary tree with indentation
    static void printBinaryTree(TreeNode node, int depth) {
        if (node == null) return;

        // Print current node
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.println(node.text);

        // Recursively print children
        for (TreeNode child : node.children) {
            printBinaryTree(child, depth + 1);
        }
    }
}