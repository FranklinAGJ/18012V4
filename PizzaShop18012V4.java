import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

class Menu {
    private Vector<String> pizzaMenu = new Vector<>();
    private Vector<Double> pizzaPrices = new Vector<>();
    private Vector<String> toppingsMenu = new Vector<>();
    private Vector<Double> toppingPrices = new Vector<>();

    public Menu() {
        pizzaMenu.add("Cheese Burst Pizza");
        pizzaMenu.add("Veggie Pizza");
        pizzaMenu.add("Paneer Pizza");
        pizzaMenu.add("Pepperoni Pizza");

        pizzaPrices.add(250.0);
        pizzaPrices.add(150.0);
        pizzaPrices.add(200.0);
        pizzaPrices.add(400.0);

        toppingsMenu.add("Mushrooms");
        toppingsMenu.add("Onions");
        toppingsMenu.add("Bell Peppers");
        toppingsMenu.add("Olives");
        toppingsMenu.add("Bacon");

        toppingPrices.add(20.0);
        toppingPrices.add(30.0);
        toppingPrices.add(25.0);
        toppingPrices.add(10.0);
        toppingPrices.add(50.0);
    }

    public Vector<String> getPizzaMenu() {
        return pizzaMenu;
    }

    public double getPizzaPrice(int pizzaIndex) {
        return pizzaPrices.get(pizzaIndex - 1);
    }

    public Vector<String> getToppingsMenu() {
        return toppingsMenu;
    }

    public double getToppingPrice(int toppingIndex) {
        return toppingPrices.get(toppingIndex - 1);
    }
}

class OrderManager {
    private Vector<String> orderedPizzas = new Vector<>();
    private Vector<String> orderedSizes = new Vector<>();
    private Vector<Integer> quantities = new Vector<>();
    private Vector<Vector<String>> orderedToppings = new Vector<>();
    private Vector<Double> orderPrices = new Vector<>();

    public double processOrder(int pizzaChoice, String size, int quantity, Menu menu, JCheckBox[] toppingCheckBoxes) {
        double pizzaPrice = menu.getPizzaPrice(pizzaChoice);
        double totalPrice = pizzaPrice * quantity;
        String pizzaName = menu.getPizzaMenu().get(pizzaChoice - 1);

        Vector<String> toppings = new Vector<>();
        for (int i = 0; i < toppingCheckBoxes.length; i++) {
            if (toppingCheckBoxes[i].isSelected()) {
                toppings.add(menu.getToppingsMenu().get(i));
                totalPrice += menu.getToppingPrice(i + 1) * quantity;
            }
        }

        orderedPizzas.add(pizzaName);
        orderedSizes.add(size);
        quantities.add(quantity);
        orderedToppings.add(toppings);
        orderPrices.add(totalPrice);

        return totalPrice;
    }

    public double calculateTotalBill() {
        double totalBill = 0;
        for (double price : orderPrices) {
            totalBill += price;
        }
        return totalBill;
    }

    public Vector<String> getOrderedPizzas() {
        return orderedPizzas;
    }

    public Vector<String> getOrderedSizes() {
        return orderedSizes;
    }

    public Vector<Integer> getQuantities() {
        return quantities;
    }

    public Vector<Vector<String>> getOrderedToppings() {
        return orderedToppings;
    }

    public Vector<Double> getOrderPrices() {
        return orderPrices;
    }
}

class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/pizza_shop";
    private static final String USER = "root";
    private static final String PASSWORD = "";  

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveOrder(String pizzaName, String size, int quantity, String toppings, double totalPrice, String customerName, String customerEmail, String customerPhone) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO orders (pizza_name, size, quantity, toppings, total_price, customer_name, customer_email, customer_phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, pizzaName);
                statement.setString(2, size);
                statement.setInt(3, quantity);
                statement.setString(4, toppings);
                statement.setDouble(5, totalPrice);
                statement.setString(6, customerName);
                statement.setString(7, customerEmail);
                statement.setString(8, customerPhone);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class PizzaShop18028V4 {

    private JFrame frame;
    private JComboBox<String> pizzaMenu;
    private JComboBox<String> sizeMenu;
    private JSpinner quantitySpinner;
    private JCheckBox[] toppingCheckBoxes;
    private JTextArea orderSummary;

    private String pizza;
    private String size;
    private int quantity;
    private String toppings;
    private double orderTotal;

    private JPanel orderPanel;
    private JPanel customerDetailsPanel;
    private JPanel billPanel;

    private Menu menu;
    private OrderManager orderManager;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    public PizzaShop18028V4() {
        menu = new Menu();
        orderManager = new OrderManager();
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("Pizza Palace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new CardLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        createOrderScreen();
        createCustomerDetailsScreen();
        createBillScreen();

        frame.setVisible(true);
    }

    private void createOrderScreen() {
        orderPanel = new JPanel(new GridBagLayout());
        orderPanel.setBackground(new Color(255, 250, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Welcome to Pizza Palace", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        gbc.gridwidth = 2;
        gbc.gridy = 0;
        orderPanel.add(titleLabel, gbc);

        JLabel pizzaLabel = new JLabel("Choose Pizza:");
        pizzaLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        orderPanel.add(pizzaLabel, gbc);

        pizzaMenu = new JComboBox<>(menu.getPizzaMenu());
        gbc.gridx = 1;
        orderPanel.add(pizzaMenu, gbc);

        JLabel sizeLabel = new JLabel("Choose Size:");
        sizeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 2;
        orderPanel.add(sizeLabel, gbc);

        sizeMenu = new JComboBox<>(new String[]{"Small", "Medium", "Large"});
        gbc.gridx = 1;
        orderPanel.add(sizeMenu, gbc);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 3;
        orderPanel.add(quantityLabel, gbc);

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1;
        orderPanel.add(quantitySpinner, gbc);

        JLabel toppingsLabel = new JLabel("Choose Toppings:");
        toppingsLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 4;
        orderPanel.add(toppingsLabel, gbc);

        toppingCheckBoxes = new JCheckBox[menu.getToppingsMenu().size()];
        for (int i = 0; i < menu.getToppingsMenu().size(); i++) {
            toppingCheckBoxes[i] = new JCheckBox(menu.getToppingsMenu().get(i));
            gbc.gridx = 1;
            gbc.gridy = 5 + i;
            orderPanel.add(toppingCheckBoxes[i], gbc);
        }

        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 20));
        nextButton.setBackground(new Color(255, 165, 0));
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pizza = (String) pizzaMenu.getSelectedItem();
                size = (String) sizeMenu.getSelectedItem();
                quantity = (int) quantitySpinner.getValue();
                toppings = getSelectedToppings();
                orderTotal = orderManager.processOrder(pizzaMenu.getSelectedIndex() + 1, size, quantity, menu, toppingCheckBoxes);
                showCustomerDetailsScreen();
            }
        });

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 10 + menu.getToppingsMenu().size();
        orderPanel.add(nextButton, gbc);

        frame.add(orderPanel, "Order");
    }

    private void createCustomerDetailsScreen() {
        customerDetailsPanel = new JPanel(new GridBagLayout());
        customerDetailsPanel.setBackground(new Color(255, 250, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        customerDetailsPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        customerDetailsPanel.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        customerDetailsPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        customerDetailsPanel.add(emailField, gbc);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 2;
        customerDetailsPanel.add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        gbc.gridx = 1;
        customerDetailsPanel.add(phoneField, gbc);

        JButton submitButton = new JButton("Submit Order");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 20));
        submitButton.setBackground(new Color(255, 165, 0));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateCustomerDetails()) {
                    
                    DatabaseManager.saveOrder(pizza, size, quantity, toppings, orderTotal, nameField.getText(), emailField.getText(), phoneField.getText());
                    showBillScreen();
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill out all customer details.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 4;
        customerDetailsPanel.add(submitButton, gbc);

        frame.add(customerDetailsPanel, "CustomerDetails");
    }

    private void createBillScreen() {
        billPanel = new JPanel(new BorderLayout());
        billPanel.setBackground(new Color(255, 250, 240));

        orderSummary = new JTextArea();
        orderSummary.setFont(new Font("Arial", Font.PLAIN, 20));
        orderSummary.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderSummary);
        billPanel.add(scrollPane, BorderLayout.CENTER);

        JButton doneButton = new JButton("Done");
        doneButton.setFont(new Font("Arial", Font.PLAIN, 20));
        doneButton.setBackground(new Color(255, 165, 0));
        doneButton.setForeground(Color.WHITE);
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(doneButton);
        billPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(billPanel, "Bill");
    }

    private void showCustomerDetailsScreen() {
        CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();
        cardLayout.show(frame.getContentPane(), "CustomerDetails");
    }

    private void showBillScreen() {
        StringBuilder bill = new StringBuilder();
        bill.append("Pizza Order Summary:\n\n");
        bill.append("Pizza: ").append(pizza).append("\n");
        bill.append("Size: ").append(size).append("\n");
        bill.append("Quantity: ").append(quantity).append("\n");
        bill.append("Toppings: ").append(toppings).append("\n");
        bill.append("Total: ₹").append(orderTotal).append("\n\n");
        bill.append("Thank you for ordering from Pizza Palace!");
        orderSummary.setText(bill.toString());

        CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();
        cardLayout.show(frame.getContentPane(), "Bill");
    }

    private boolean validateCustomerDetails() {
        return !nameField.getText().isEmpty() && !emailField.getText().isEmpty() && !phoneField.getText().isEmpty();
    }

    private String getSelectedToppings() {
        StringBuilder selectedToppings = new StringBuilder();
        for (int i = 0; i < toppingCheckBoxes.length; i++) {
            if (toppingCheckBoxes[i].isSelected()) {
                if (selectedToppings.length() > 0) {
                    selectedToppings.append(", ");
                }
                selectedToppings.append(menu.getToppingsMenu().get(i));
            }
        }
        return selectedToppings.toString();
    }

    public static void main(String[] args) {
        new PizzaShop18028V4();
    }
}
