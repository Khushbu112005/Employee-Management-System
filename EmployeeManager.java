import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.nio.file.*;

public class EmployeeManager extends JFrame {
    private JTextField idField, nameField, departmentField, dateField;
    private JComboBox<String> attendanceStatus;
    private JTextArea outputArea;
    private final String DB_URL = "jdbc:mysql://localhost:3306/employees";
    private final String DB_USER = "root";
    private final String DB_PASS = "Kc@#1234";

    public EmployeeManager() {
        setTitle("Employee Management System");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        add(new JLabel("ID:"));
        idField = new JTextField(10);
        add(idField);

        add(new JLabel("Name:"));
        nameField = new JTextField(10);
        add(nameField);

        add(new JLabel("Department:"));
        departmentField = new JTextField(10);
        add(departmentField);

        add(new JLabel("Attendance:"));
        attendanceStatus = new JComboBox<>(new String[]{"Present", "Absent"});
        add(attendanceStatus);

        add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(10);
        add(dateField);

        JButton addEmployeeBtn = new JButton("Add Employee");
        JButton markAttendanceBtn = new JButton("Mark Attendance");
        JButton viewEmployeesBtn = new JButton("View Employees");
        JButton viewAttendanceBtn = new JButton("View Attendance");
        JButton deleteEmployeeBtn = new JButton("Delete by ID");
        JButton updateEmployeeBtn = new JButton("Update Employee");
        JButton searchBtn = new JButton("Search Employee");
        JButton dateAttendanceBtn = new JButton("View Attendance by Date");
        JButton exportBtn = new JButton("Export Employees");
        JButton summaryBtn = new JButton("Attendance Summary");

        add(addEmployeeBtn);
        add(markAttendanceBtn);
        add(viewEmployeesBtn);
        add(viewAttendanceBtn);
        add(deleteEmployeeBtn);
        add(updateEmployeeBtn);
        add(searchBtn);
        add(dateAttendanceBtn);
        add(exportBtn);
        add(summaryBtn);

        outputArea = new JTextArea(25, 70);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea));

        addEmployeeBtn.addActionListener(e -> addEmployee());
        markAttendanceBtn.addActionListener(e -> markAttendance());
        viewEmployeesBtn.addActionListener(e -> showEmployees());
        viewAttendanceBtn.addActionListener(e -> showAttendance());
        deleteEmployeeBtn.addActionListener(e -> deleteEmployee());
        updateEmployeeBtn.addActionListener(e -> updateEmployee());
        searchBtn.addActionListener(e -> searchEmployee());
        dateAttendanceBtn.addActionListener(e -> viewAttendanceByDate());
        exportBtn.addActionListener(e -> exportEmployees());
        summaryBtn.addActionListener(e -> attendanceSummary());

        setVisible(true);
    }

    private void addEmployee() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String dept = departmentField.getText().trim();
            if (name.isEmpty() || dept.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Department are required!");
                return;
            }
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "INSERT INTO employee (id, name, department) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, dept);
            stmt.executeUpdate();
            outputArea.setText("Employee added.");
            conn.close();
        } catch (SQLIntegrityConstraintViolationException ex) {
            outputArea.setText("Employee ID already exists.");
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void markAttendance() {
        try {
            int empId = Integer.parseInt(idField.getText().trim());
            String status = (String) attendanceStatus.getSelectedItem();
            LocalDate today = LocalDate.now();
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "INSERT INTO attendance (emp_id, date, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, empId);
            stmt.setDate(2, Date.valueOf(today));
            stmt.setString(3, status);
            stmt.executeUpdate();
            outputArea.setText("Attendance marked for today.");
            conn.close();
        } catch (SQLIntegrityConstraintViolationException ex) {
            outputArea.setText("Attendance already marked for today.");
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void showEmployees() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM employee");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Department: ").append(rs.getString("department"))
                        .append("\n");
            }
            outputArea.setText(sb.toString());
            conn.close();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void showAttendance() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT a.emp_id, e.name, a.date, a.status FROM attendance a JOIN employee e ON a.emp_id = e.id ORDER BY a.date DESC");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("emp_id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Date: ").append(rs.getDate("date"))
                        .append(", Status: ").append(rs.getString("status"))
                        .append("\n");
            }
            outputArea.setText(sb.toString());
            conn.close();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void deleteEmployee() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM attendance WHERE emp_id = ?");
            stmt1.setInt(1, id);
            stmt1.executeUpdate();
            PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM employee WHERE id = ?");
            stmt2.setInt(1, id);
            int rows = stmt2.executeUpdate();
            outputArea.setText(rows > 0 ? "Employee deleted." : "Employee not found.");
            conn.close();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void updateEmployee() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String dept = departmentField.getText().trim();
            if (name.isEmpty() || dept.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Department required!");
                return;
            }
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "UPDATE employee SET name = ?, department = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, dept);
            stmt.setInt(3, id);
            int rows = stmt.executeUpdate();
            outputArea.setText(rows > 0 ? "Employee updated." : "Employee not found.");
            conn.close();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void searchEmployee() {
        try {
            String query = nameField.getText().trim();
            if (query.isEmpty()) {
                outputArea.setText("Enter Name to search.");
                return;
            }
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "SELECT * FROM employee WHERE name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Department: ").append(rs.getString("department"))
                        .append("\n");
            }
            outputArea.setText(sb.length() > 0 ? sb.toString() : "No employee found.");
            conn.close();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void viewAttendanceByDate() {
        try {
            String dateInput = dateField.getText().trim();
            if (dateInput.isEmpty()) {
                outputArea.setText("Enter a date.");
                return;
            }
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "SELECT a.emp_id, e.name, a.status FROM attendance a JOIN employee e ON a.emp_id = e.id WHERE a.date = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(dateInput));
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder("Attendance on " + dateInput + ":\n");
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("emp_id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Status: ").append(rs.getString("status"))
                        .append("\n");
            }
            outputArea.setText(sb.toString());
            conn.close();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void exportEmployees() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM employee");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Department: ").append(rs.getString("department"))
                        .append("\n");
            }
            Files.write(Paths.get("employees.txt"), sb.toString().getBytes());
            outputArea.setText("Data exported to employees.txt");
            conn.close();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void attendanceSummary() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            String sql = "SELECT e.id, e.name, COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS present_days FROM employee e LEFT JOIN attendance a ON e.id = a.emp_id GROUP BY e.id, e.name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            StringBuilder sb = new StringBuilder("Attendance Summary:\n");
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Present Days: ").append(rs.getInt("present_days"))
                        .append("\n");
            }
            outputArea.setText(sb.toString());
            conn.close();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeManager::new);
    }
}
