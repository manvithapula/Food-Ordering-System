import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

// Database Connection Utility
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/food_order";
    private static final String USER = "root";
    private static final String PASSWORD = "manvitha@2004";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }
}

// Colors Class for consistent styling
class Colors {
    static final Color PRIMARY = new Color(255, 90, 95);
    static final Color SECONDARY = new Color(51, 51, 51);
    static final Color BACKGROUND = new Color(248, 249, 250);
    static final Color ACCENT = new Color(255, 193, 7);
    static final Color TEXT = new Color(33, 37, 41);
    static final Color CARD_BACKGROUND = Color.WHITE;
    static final Color HOVER = new Color(234, 236, 238);
}

// Enhanced StyledButton
class StyledButton extends JButton {
    public StyledButton(String text) {
        super(text);
        setBackground(Colors.PRIMARY);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(150, 40));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setBackground(Colors.PRIMARY.darker());
            }
            public void mouseExited(MouseEvent e) {
                setBackground(Colors.PRIMARY);
            }
        });
    }
}

// Login Page Class
class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    public LoginPage() {
        setTitle("Foodie's Paradise - Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        final ImageIcon backgroundIcon = loadBackgroundImage(400, 500);

        // Create main panel with background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundIcon != null) {
                    g.drawImage(backgroundIcon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
                }
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Create login panel
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(loginPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);

    }
    private ImageIcon loadBackgroundImage(int width, int height) {
        try {
            String defaultPath = "C:\\Users\\manvi\\IdeaProjects\\FoodOrder\\src\\main\\resources\\images\\default.jpeg";
            ImageIcon originalIcon = new ImageIcon(defaultPath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
            return null;
        }
    }


    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 200));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Foodie's Paradise");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Colors.PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(userLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        StyledButton loginButton = new StyledButton("Login");
        StyledButton registerButton = new StyledButton("Register");

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterPage());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);

        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return loginPanel;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                HomePage homePage = new HomePage(userId);
                homePage.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password",
                        "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void openRegisterPage() {
        RegisterPage registerPage = new RegisterPage(this);
        registerPage.setVisible(true);
        this.setVisible(false);
    }
}

// Register Page Class
class RegisterPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private LoginPage loginPage;

    public RegisterPage(LoginPage loginPage) {
        this.loginPage = loginPage;
        setTitle("Foodie's Paradise - Register");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        final ImageIcon backgroundIcon = loadBackgroundImage(400, 600);

        // Main panel with background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundIcon != null) {
                    g.drawImage(backgroundIcon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
                }
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Registration panel
        JPanel registerPanel = createRegisterPanel();
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(registerPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    private ImageIcon loadBackgroundImage(int width, int height) {
        try {
            String defaultPath = "C:\\Users\\manvi\\IdeaProjects\\FoodOrder\\src\\main\\resources\\images\\default.jpeg";
            ImageIcon originalIcon = new ImageIcon(defaultPath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
            return null;
        }
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 200));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        registerPanel.setLayout(new GridBagLayout());
        registerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Colors.PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        registerPanel.add(titleLabel, gbc);

        // Form fields
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        emailField = new JTextField(15);
        phoneField = new JTextField(15);
        addressArea = new JTextArea(3, 15);

        addFormField(registerPanel, "Username:", usernameField, 1);
        addFormField(registerPanel, "Password:", passwordField, 2);
        addFormField(registerPanel, "Email:", emailField, 3);
        addFormField(registerPanel, "Phone:", phoneField, 4);

        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        registerPanel.add(addressLabel, gbc);

        addressArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(addressArea);
        gbc.gridx = 1;
        gbc.gridy = 5;
        registerPanel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        StyledButton registerButton = new StyledButton("Register");
        StyledButton backButton = new StyledButton("Back to Login");

        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            loginPage.setVisible(true);
            this.dispose();
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        registerPanel.add(buttonPanel, gbc);

        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return registerPanel;
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = gridy;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = gridy;
        panel.add(field, gbc);
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressArea.getText();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, email, phone, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
            loginPage.setVisible(true);
            this.dispose();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            } else {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }
}

// MenuItem Class
class MenuItem {
    int id;
    String name;
    double price;
    String description;
    ImageIcon image;

    public MenuItem(int id, String name, double price, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        loadImage(imagePath);
    }

    private void loadImage(String imagePath) {
        try {
            String absolutePath = "C:\\Users\\manvi\\IdeaProjects\\FoodOrder\\src\\main\\resources\\images\\" + imagePath;
            File imageFile = new File(absolutePath);

            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(absolutePath);
                Image scaledImage = originalIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                this.image = new ImageIcon(scaledImage);
            } else {
                String defaultPath = "C:\\Users\\manvi\\IdeaProjects\\FoodOrder\\src\\main\\resources\\images\\default.jpeg";
                ImageIcon defaultIcon = new ImageIcon(defaultPath);
                Image scaledImage = defaultIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                this.image = new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            // Create a colored placeholder if image loading fails
            BufferedImage placeholder = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = placeholder.createGraphics();
            g2d.setColor(Colors.ACCENT);
            g2d.fillRect(0, 0, 200, 150);
            g2d.dispose();
            this.image = new ImageIcon(placeholder);
        }
    }
}

// HomePage Class
class HomePage extends JFrame {
    private final int userId;
    private List<MenuItem> menuItems;
    private Map<MenuItem, Integer> cart;
    private JPanel menuPanel;
    private JLabel totalLabel;
    private double totalAmount = 0.0;
    private JPanel cartPanel;

    public HomePage(int userId) {
        this.userId = userId;
        this.menuItems = new ArrayList<>();
        this.cart = new HashMap<>();

        setTitle("Foodie's Paradise");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Colors.BACKGROUND);

        loadMenuItems();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Colors.PRIMARY);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Foodie's Paradise");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // Add logout button
        StyledButton logoutButton = new StyledButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.addActionListener(e -> handleLogout());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setBackground(Colors.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Menu Panel
        menuPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        menuPanel.setBackground(Colors.BACKGROUND);

        for (MenuItem item : menuItems) {
            JPanel card = createMenuItemCard(item);
            menuPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Cart Panel
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(Colors.CARD_BACKGROUND);
        cartPanel.setBorder(BorderFactory.createLineBorder(Colors.SECONDARY, 1));
        cartPanel.setPreferredSize(new Dimension(300, getHeight()));

        JLabel cartTitle = new JLabel("Your Cart");
        cartTitle.setFont(new Font("Arial", Font.BOLD, 20));
        cartTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartPanel.add(cartTitle);

        totalLabel = new JLabel("Total: Rs0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartPanel.add(totalLabel);

        StyledButton checkoutButton = new StyledButton("Checkout");
        checkoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkoutButton.addActionListener(e -> handleCheckout());
        cartPanel.add(checkoutButton);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(cartPanel, BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
            this.dispose();
        }
    }

    private void loadMenuItems() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items");
            while (rs.next()) {
                menuItems.add(new MenuItem(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("image_path")));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + ex.getMessage());
        }
    }

    private JPanel createMenuItemCard(MenuItem item) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Colors.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.SECONDARY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Image
        JLabel imageLabel = new JLabel(item.image);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name
        JLabel nameLabel = new JLabel(item.name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JTextArea descArea = new JTextArea(item.description);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel(String.format("Rs%.2f", item.price));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(Colors.PRIMARY);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to Cart Button
        StyledButton addButton = new StyledButton("Add to Cart");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> addToCart(item));

        card.add(imageLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(descArea);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(priceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(addButton);

        return card;
    }

    private void updateCart() {
        // Remove all components except title, total and checkout button
        Component[] components = cartPanel.getComponents();
        JLabel cartTitle = (JLabel) components[0];
        JLabel total = totalLabel;
        StyledButton checkout = (StyledButton) components[components.length - 1];

        cartPanel.removeAll();

        cartPanel.add(cartTitle);
        cartPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add cart items
        for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
            MenuItem item = entry.getKey();
            int quantity = entry.getValue();

            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
            itemPanel.setBackground(Colors.CARD_BACKGROUND);

            JLabel itemName = new JLabel(item.name + " x" + quantity);
            JLabel itemPrice = new JLabel(String.format("Rs%.2f", item.price * quantity));

            // Add remove button
            StyledButton removeButton = new StyledButton("Remove");
            removeButton.setPreferredSize(new Dimension(80, 30));
            removeButton.addActionListener(e -> removeFromCart(item));

            itemPanel.add(itemName);
            itemPanel.add(itemPrice);
            itemPanel.add(removeButton);

            cartPanel.add(itemPanel);
            cartPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        cartPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cartPanel.add(total);
        cartPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cartPanel.add(checkout);

        cartPanel.revalidate();
        cartPanel.repaint();
    }

    private void removeFromCart(MenuItem item) {
        cart.remove(item);
        updateTotal();
        updateCart();
    }

    private void addToCart(MenuItem item) {
        cart.merge(item, 1, Integer::sum);
        updateTotal();
        updateCart();
    }

    private void updateTotal() {
        totalAmount = cart.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().price * entry.getValue())
                .sum();
        totalLabel.setText(String.format("Total: Rs%.2f", totalAmount));
    }

    private void handleCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Total amount: Rs" + String.format("%.2f", totalAmount) + "\nProceed to checkout?",
                "Confirm Order",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            placeOrder();
        }
    }

    private void placeOrder() {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert order
            String orderQuery = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, totalAmount);
            orderStmt.setString(3, "PLACED");
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);

                // Insert order items
                String itemQuery = "INSERT INTO order_items (order_id, item_id, quantity, price_at_time) VALUES (?, ?, ?, ?)";
                PreparedStatement itemStmt = conn.prepareStatement(itemQuery);

                for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
                    MenuItem item = entry.getKey();
                    int quantity = entry.getValue();

                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.id);
                    itemStmt.setInt(3, quantity);
                    itemStmt.setDouble(4, item.price);
                    itemStmt.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                cart.clear();
                updateTotal();
                updateCart();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error placing order: " + ex.getMessage());
        }
    }
}

public class FoodOrderingSystem {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (initializeDatabase()) {
            SwingUtilities.invokeLater(() -> {
                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true);
            });
        }
    }

    private static boolean initializeDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();

            // Create Users Table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(50) NOT NULL," +
                    "email VARCHAR(100)," +
                    "phone VARCHAR(20)," +
                    "address TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Create Menu Items Table
            stmt.execute("CREATE TABLE IF NOT EXISTS menu_items (" +
                    "item_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "price DOUBLE NOT NULL," +
                    "description TEXT," +
                    "image_path VARCHAR(255)," +
                    "category VARCHAR(50)," +
                    "is_vegetarian BOOLEAN DEFAULT FALSE," +
                    "is_available BOOLEAN DEFAULT TRUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Create Orders Table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "order_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT," +
                    "total_amount DOUBLE NOT NULL," +
                    "status VARCHAR(20) NOT NULL," +
                    "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "delivery_address TEXT," +
                    "special_instructions TEXT," +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id))");

            // Create Order Items Table
            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "order_item_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "order_id INT," +
                    "item_id INT," +
                    "quantity INT NOT NULL," +
                    "price_at_time DOUBLE NOT NULL," +
                    "special_requests TEXT," +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id)," +
                    "FOREIGN KEY (item_id) REFERENCES menu_items(item_id))");

            // Insert sample user if none exist
            ResultSet userRs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            userRs.next();
            if (userRs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (username, password, email) VALUES " +
                        "('demo_user', 'password123', 'demo@example.com')");
            }

            // Insert sample menu items if none exist
            ResultSet menuRs = stmt.executeQuery("SELECT COUNT(*) FROM menu_items");
            menuRs.next();
            if (menuRs.getInt(1) == 0) {
                stmt.execute("INSERT INTO menu_items (name, price, description, image_path, category, is_vegetarian) VALUES " +
                        "('Classic Burger', 299.99, 'Juicy beef patty with fresh lettuce, tomatoes, and our special sauce', '1.jpeg', 'Burgers', FALSE)," +
                        "('Margherita Pizza', 399.99, 'Traditional Italian pizza with fresh basil, mozzarella, and tomato sauce', '2.jpeg', 'Pizza', TRUE)," +
                        "('Caesar Salad', 249.99, 'Crisp romaine lettuce with Caesar dressing, croutons, and parmesan shavings', '3.jpeg', 'Salads', TRUE)," +
                        "('Pasta Alfredo', 349.99, 'Creamy fettuccine pasta with rich parmesan cheese sauce and herbs', '4.jpeg', 'Pasta', TRUE)," +
                        "('Chicken Wings', 329.99, 'Spicy buffalo wings served with blue cheese dip and celery sticks', '5.jpeg', 'Appetizers', FALSE)");
            }

            System.out.println("Database initialized successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage = "Database Error: " + e.getMessage() + "\n" +
                    "Please check if:\n" +
                    "1. MySQL server is running\n" +
                    "2. Database 'food_order' exists\n" +
                    "3. User credentials are correct";
            JOptionPane.showMessageDialog(null, errorMessage, "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}