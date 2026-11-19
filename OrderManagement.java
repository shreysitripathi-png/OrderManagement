import java.sql.*;
import java.util.Scanner;

class Orders {
    private int order_id;
    private String customer_name;
    private String product_name;
    private int quantity;
    private Double unit_price;
    private String status;

    public Orders() {
    }

    public Orders(int order_id, String customer_name, String product_name, int quantity, Double unit_price, String status) {
        this.order_id = order_id;
        this.customer_name = customer_name;
        this.product_name = product_name;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.status = status;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Double unit_price) {
        this.unit_price = unit_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "order_id=" + order_id +
                ", customer_name='" + customer_name + '\'' +
                ", product_name='" + product_name + '\'' +
                ", quantity=" + quantity +
                ", unit_price=" + unit_price +
                ", status='" + status + '\'' +
                '}';
    }
}

class OrdersAPI {
    String url = "jdbc:mysql://localhost:3306/ecommerce_db";
    String username = "root";
    String password = "Sam/yak@05";

    public void insertOrders(){
        try{
            Connection connection = DriverManager.getConnection(url, username, password);

            String sql = "INSERT INTO Orders(order_id, customer_name, product_name, quantity, unit_price, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = connection.prepareStatement(sql);

            Orders[] data = {
                    new Orders(101, "Sam", "Laptop", 10, 99.99, "Delivered"),
                    new Orders(102, "Samyak", "Phone", 5, 80.12, "Shipped"),
                    new Orders(103, "Samyak Chaturvedi", "CHarger", 20, 3.5, "Pending")
            };

            for(Orders p : data){
                ps.setInt(1, p.getOrder_id());
                ps.setString(2, p.getCustomer_name());
                ps.setString(3, p.getProduct_name());
                ps.setInt(4, p.getQuantity());
                ps.setDouble(5, p.getUnit_price());
                ps.setString(6, p.getStatus());

                ps.executeUpdate();
            }

            System.out.println("Three Orders Created Successfully...");
            connection.close();
        }
        catch (SQLException e) {

            if (e.getErrorCode() == 1062){
                System.out.println("Duplicate Exists");
            }
            else {
                System.out.println("Connection Failed....");
                e.printStackTrace();
            }
        }
    }

    public void displayByStatus(){
        try{
            Connection connection = DriverManager.getConnection(url, username, password);

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter status : ");
            String st = sc.nextLine();

            String sql = "select * from Orders where status = ? AND quantity > 2 ORDER BY unit_price DESC";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, st);

            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                Orders order = mapRow(resultSet);
                System.out.println(order);
            }
            sc.close();
            connection.close();
            }

        catch (SQLException e) {
            if (e.getErrorCode() == 1062){
                System.out.println("Duplicate Exists");
            }
            else {
                System.out.println("Connection Failed....");
                e.printStackTrace();
            }
        }
    }
    private Orders mapRow(ResultSet resultSet) throws SQLException{
        Orders product = new Orders();

        product.setOrder_id(resultSet.getInt(1));
        product.setCustomer_name(resultSet.getString(2));
        product.setProduct_name(resultSet.getString(3));
        product.setQuantity(resultSet.getInt(4));
        product.setUnit_price(resultSet.getDouble(5));
        product.setStatus(resultSet.getString(6));

        return product;
    }

    public void addQuantity(){
        try{
            Connection connection = DriverManager.getConnection(url, username, password);

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter order ID : ");
            int id = sc.nextInt();

            System.out.println("Enter new quantity : ");
            int quant = sc.nextInt();

            String sql = "UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, quant);
            ps.setInt(2, id);

            int result = ps.executeUpdate();

            if (result > 0){
                System.out.println("Updation Successful");
            }
            else{
                System.out.println("Order Id doesn't exist.");
            }

            sc.close();
            connection.close();
        }
        catch(SQLException e){
            System.out.println("Connection failed");
            e.printStackTrace();
        }
    }

    public void updateStatus(){
        try{
            Connection connection = DriverManager.getConnection(url, username, password);

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter order ID : ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter new status : ");
            String status = sc.nextLine();

            String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(2, id);
            ps.setString(1, status);

            int result = ps.executeUpdate();

            if (result > 0){
                System.out.println("Status Updated");
            }
            else{
                System.out.println("Invalid ID");
            }

            String sql2 = "select * from Orders where order_id = ?";

            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setInt(1, id);

            ResultSet rs = ps2.executeQuery();
            Orders ord;

            rs.next();
            if (rs.next()){
                ord = mapRow(rs);
            }

            

            sc.close();
            connection.close();
        }
        catch (SQLException e){
            System.out.println("Connection Failed");
            e.printStackTrace();
        }
    }

    public void displayTotal(){
        try{
            Connection connection = DriverManager.getConnection(url, username, password);

            String sql = "select SUM(quantity * unit_price) AS total from Orders";

            PreparedStatement ps = connection.prepareStatement(sql);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()){
                System.out.println("Total : " + resultSet.getDouble("total"));
            }

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter order ID to delete: ");
            int id = sc.nextInt();

            String sql1 = "DELETE from Orders where order_id = ? AND status = 'Cancelled'";
            PreparedStatement ps1 = connection.prepareStatement(sql1);

            ps1.setInt(1, id);

            int result = ps1.executeUpdate();

            if (result > 0){
                System.out.println("Deleted successfully...");
            }
            else{
                System.out.println("Order Id not found");
            }
        }
        catch(SQLException e){
            System.out.println("Connection Failed...");
            e.printStackTrace();
        }
    }
}


public class OrderManagement {
    public static void main(String args[]) {
        OrdersAPI orders = new OrdersAPI();
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insert Three Orders in DB.");
        System.out.println("2. Display by order status and quantity greater than two.");
        System.out.println("3. Increase Quantity using Id.");
        System.out.println("4. Update Status Using Id.");
        System.out.println("5. Display total value and Delete an order using Id");

        System.out.println("Enter an option : ");
        int op = sc.nextInt();

        if (op == 1){
            orders.insertOrders();
        }
        else if (op == 2){
            orders.displayByStatus();
        }
        else if (op == 3){
            orders.addQuantity();
        }
        else if (op == 4){
            orders.updateStatus();
        }
        else if (op == 5){
            orders.displayTotal();
        }
    }
}