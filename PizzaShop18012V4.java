import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Menu {
    private String[] pizzaMenu = {"Cheese Burst Pizza", "Veggie Pizza", "Paneer Pizza", "Pepperoni Pizza"};
    private double[] pizzaPrices = {250, 150, 200, 400};
    private String[] toppingsMenu = {"Mushrooms", "Onions", "Bell Peppers", "Olives", "Bacon"};
    private double[] toppingPrices = {20, 30, 25, 10, 50};

    public String[] getPizzaMenu() {
        return pizzaMenu;
    }

    public double getPizzaPrice(int pizzaIndex) {
        return pizzaPrices[pizzaIndex - 1];
    }

    public String[] getToppingsMenu() {
        return toppingsMenu;
    }

    public double getToppingPrice(int toppingIndex) {
        return toppingPrices[toppingIndex - 1];
    }
}

class OrderManager {
    private List<String> orderedPizzas = new ArrayList<>();
    private List<String> orderedSizes = new ArrayList<>();
    private List<Integer> quantities = new ArrayList<>();
    private List<List<String>> orderedToppings = new ArrayList<>();
    private List<Double> orderPrices = new ArrayList<>();

    public double processOrder(int pizzaChoice, String size, int quantity, Menu menu, JCheckBox[] toppingCheckBoxes) {
        double pizzaPrice = menu.getPizzaPrice(pizzaChoice);
        double totalPrice = pizzaPrice * quantity;
        String pizzaName = menu.getPizzaMenu()[pizzaChoice - 1];

        List<String> toppings = new ArrayList<>();
        for (int i = 0; i < toppingCheckBoxes.length; i++) {
            if (toppingCheckBoxes[i].isSelected()) {
                toppings.add(menu.getToppingsMenu()[i]);
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

    public List<String> getOrderedPizzas() {
        return orderedPizzas;
    }

    public List<String> getOrderedSizes() {
        return orderedSizes;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }

    public List<List<String>> getOrderedToppings() {
        return orderedToppings;
    }

    public List<Double> getOrderPrices() {
        return orderPrices;
    }
}

public class PizzaShop18012V4 {
    private static final String[] PIZZA_OPTIONS = {"Cheese Burst Pizza", "Veggie Pizza", "Paneer Pizza", "Pepperoni Pizza"};
    private static final double[] PIZZA_PRICES = {250, 150, 200, 400};
    private static final String[] TOPPING_OPTIONS = {"Mushrooms", "Onions", "Bell Peppers", "Olives", "Bacon"};
    private static final double[] TOPPING_PRICES = {20, 30, 25, 10, 50};

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

    private Connection connection;
    private PreparedStatement insertOrderStmt;

    private Menu menu;
    private OrderManager orderManager;

    public PizzaShop18012V4() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pizza_shop", "root", "");
            insertOrderStmt = connection.prepareStatement("INSERT INTO orders (pizza, size, quantity, toppings, total_price, customer_name, customer_email, customer_phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        menu = new Menu();
        orderManager = new OrderManager();

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

        pizzaMenu = new JComboBox<>(PIZZA_OPTIONS);
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

        toppingCheckBoxes = new JCheckBox[TOPPING_OPTIONS.length];
        for (int i = 0; i < TOPPING_OPTIONS.length; i++) {
            toppingCheckBoxes[i] = new JCheckBox(TOPPING_OPTIONS[i]);
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
        gbc.gridy = 10 + TOPPING_OPTIONS.length;
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

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        customerDetailsPanel.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        customerDetailsPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        customerDetailsPanel.add(emailField, gbc);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 2;
        customerDetailsPanel.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField(20);
        gbc.gridx = 1;
        customerDetailsPanel.add(phoneField, gbc);

        JButton confirmButton = new JButton("Confirm Order");
        confirmButton.setFont(new Font("Arial", Font.PLAIN, 20));
        confirmButton.setBackground(new Color(255, 165, 0));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();

                try {
                    insertOrderStmt.setString(1, pizza);
                    insertOrderStmt.setString(2, size);
                    insertOrderStmt.setInt(3, quantity);
                    insertOrderStmt.setString(4, toppings);
                    insertOrderStmt.setDouble(5, orderTotal);
                    insertOrderStmt.setString(6, name);
                    insertOrderStmt.setString(7, email);
                    insertOrderStmt.setString(8, phone);
                    insertOrderStmt.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error placing order.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                showBillScreen();
            }
        });

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        customerDetailsPanel.add(confirmButton, gbc);

        frame.add(customerDetailsPanel, "CustomerDetails");
    }

    private void createBillScreen() {
        billPanel = new JPanel();
        billPanel.setBackground(new Color(255, 250, 240));
        billPanel.setLayout(new BoxLayout(billPanel, BoxLayout.Y_AXIS));

        orderSummary = new JTextArea(10, 40);
        orderSummary.setEditable(false);
        orderSummary.setFont(new Font("Arial", Font.PLAIN, 18));

        JScrollPane scrollPane = new JScrollPane(orderSummary);
        billPanel.add(scrollPane);

        JButton backToOrderButton = new JButton("Back to Order");
        backToOrderButton.setFont(new Font("Arial", Font.PLAIN, 20));
        backToOrderButton.setBackground(new Color(255, 165, 0));
        backToOrderButton.setForeground(Color.WHITE);
        backToOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showOrderScreen();
            }
        });

        billPanel.add(backToOrderButton);

        frame.add(billPanel, "Bill");
    }

    private String getSelectedToppings() {
        StringBuilder selectedToppings = new StringBuilder();
        for (int i = 0; i < toppingCheckBoxes.length; i++) {
            if (toppingCheckBoxes[i].isSelected()) {
                if (selectedToppings.length() > 0) {
                    selectedToppings.append(", ");
                }
                selectedToppings.append(TOPPING_OPTIONS[i]);
            }
        }
        return selectedToppings.toString();
    }

    private void showCustomerDetailsScreen() {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "CustomerDetails");
    }

    private void showOrderScreen() {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "Order");
    }

    private void showBillScreen() {
        orderSummary.setText("Order Summary:\n");
        orderSummary.append("Pizza: " + pizza + " (" + size + ")\n");
        orderSummary.append("Quantity: " + quantity + "\n");
        orderSummary.append("Toppings: " + toppings + "\n");
        orderSummary.append("Total Price: Rs." + orderTotal + "\n");

        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "Bill");
    }

    public static void main(String[] args) {
        new PizzaShop18012V4();
    }
}