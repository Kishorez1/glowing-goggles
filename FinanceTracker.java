import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FinanceTracker extends Frame {
    private TextField descField, amountField;
    private Choice typeChoice;
    private TextArea displayArea;
    private Connection conn;

    public FinanceTracker() {
        initializeDatabase();

        setTitle("Personal Finance Tracker");
        setSize(500, 450);
        setLayout(new FlowLayout());

        add(new Label("Description:"));
        descField = new TextField(20);
        add(descField);

        add(new Label("Amount:"));
        amountField = new TextField(10);
        add(amountField);

        add(new Label("Type:"));
        typeChoice = new Choice();
        typeChoice.add("Income");
        typeChoice.add("Expense");
        add(typeChoice);
        
add(new Label("Delete by Transaction ID:"));
TextField deleteField = new TextField(5);
add(deleteField);

        Button addButton = new Button("Add Transaction");
        add(addButton);

        Button viewButton = new Button("View Transactions");
        add(viewButton);

        Button balanceButton = new Button("Calculate Balance");
        add(balanceButton);
        Button deleteButton = new Button("Delete Transaction");
        add(deleteButton);

        displayArea = new TextArea(15, 50);
        displayArea.setEditable(false);
        add(displayArea);

        
        addButton.addActionListener(e -> addTransaction());
        viewButton.addActionListener(e -> viewTransactions());
        balanceButton.addActionListener(e -> calculateBalance());
        deleteButton.addActionListener(e -> deleteTransaction(deleteField.getText()));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeDatabase();
                dispose();
            }
        });

        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
            System.out.println("Database Connected Successfully!");

            String sql = "CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "description TEXT NOT NULL, "
                    + "amount REAL NOT NULL, "
                    + "type TEXT CHECK(type IN ('Income', 'Expense')) NOT NULL)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
            displayArea.setText("Database Connection Failed: " + e.getMessage());
        }
    }

    private void addTransaction() {
        if (conn == null) {
            displayArea.setText("Database connection is not established!");
            return;
        }

        String description = descField.getText().trim();
        String amountText = amountField.getText().trim();

        if (description.isEmpty() || amountText.isEmpty()) {
            displayArea.setText("Please enter both description and amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            String type = typeChoice.getSelectedItem();

            String sql = "INSERT INTO transactions (description, amount, type) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, description);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, type);
                pstmt.executeUpdate();
            }

            descField.setText("");
            amountField.setText("");
            displayArea.setText("Transaction Added Successfully!");
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid amount. Please enter a valid number.");
        } catch (Exception e) {
            e.printStackTrace();
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private void viewTransactions() {
        if (conn == null) {
            displayArea.setText("Database connection is not established!");
            return;
        }

        displayArea.setText("Transactions:\n\n");

        String sql = "SELECT * FROM transactions ORDER BY id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                displayArea.append("ID: " + rs.getInt("id") + " | " +
                        rs.getString("type") + ": " +
                        rs.getString("description") + " - $" +
                        rs.getDouble("amount") + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private void calculateBalance() {
        if (conn == null) {
            displayArea.setText("Database connection is not established!");
            return;
        }

        try {
            double income = getTotalAmount("Income");
            double expense = getTotalAmount("Expense");
            double balance = income - expense;

            displayArea.setText("Total Income: $" + income +
                    "\nTotal Expense: $" + expense +
                    "\nBalance: $" + balance);
        } catch (Exception e) {
            e.printStackTrace();
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private double getTotalAmount(String type) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM transactions WHERE type = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            double total = rs.getDouble("total");
            rs.close();
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void deleteTransaction(String idText) {
        if (conn == null) {
            displayArea.setText("Database connection is not established!");
            return;
        }
    
        if (idText.trim().isEmpty()) {
            displayArea.setText("Please enter a Transaction ID to delete.");
            return;
        }
    
        try {
            int id = Integer.parseInt(idText.trim());
            String sql = "DELETE FROM transactions WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    displayArea.setText("Transaction with ID " + id + " deleted successfully.");
                } else {
                    displayArea.setText("No transaction found with ID " + id + ".");
                }
            }
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid ID. Please enter a numeric value.");
        } catch (SQLException e) {
            e.printStackTrace();
            displayArea.setText("Error: " + e.getMessage());
        }
    }
    

    private void closeDatabase() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Database Connection Closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error closing database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new FinanceTracker();
    }
}