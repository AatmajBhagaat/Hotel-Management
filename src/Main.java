import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;

public class Main{
    private static final String url = "jdbc:mysql://localhost:3306/hoteldb";
    private static final String username ="root";
    private final static String password = "Aatmaj@3580";
    public static void main(String[] args) throws SQLException, ClassNotFoundException
    {
        try
        {
            Class.forName("com.msql.cj.jdbc.Driver");
            System.out.println("Driver Loaded Successfully");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        try
        {
            Connection connection = DriverManager.getConnection(url,username,password);
            Statement statement = connection.createStatement();
            while(true) {
                System.out.println();
                System.out.println("============== HOTEL MANAGEMENT SYSTEM ================");
                System.out.println();
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View reservation");
                System.out.println("3. Get room number");
                System.out.println("4. Update reservation");
                System.out.println("5. Delete reservation");
                System.out.println("0. Exit");
                System.out.println();
                System.out.print("Choose an option: ");
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        reserveRoom(scanner, statement);
                        break;
                    case 2:
                        viewReservation(statement);
                        break;
                    case 3:
                        getRoomNumber(scanner, statement);
                        break;
                    case 4:
                        updateReservation(scanner, statement);
                        break;
                    case 5:
                        deleteReservation(scanner, statement);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid Choice. try again.");
                }
            }

        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }

    }
    private static void reserveRoom( Scanner scanner, Statement statement) throws SQLException
    {
        System.out.println();
        System.out.println("Enter Name of Customer: ");
        String Name = scanner.next();
        scanner.nextLine();
        System.out.println("Enter Room Number: ");
        int roomNumber = scanner.nextInt();
        System.out.println("Enter Contact Number: ");
        String contactNumber = scanner.next();

        String query ="insert into reservations(guest_Name,room_Number,contact_number)values('"+ Name +"','"+ roomNumber+"','"+ contactNumber+"')";
        try
        {
            int affectedRows = statement.executeUpdate(query);
            if(affectedRows>0)
            {
                System.out.println("Reservation Successfully!!");
            }
            else
            {
                System.out.println("Reservation Failed!!");
            }
        }
        catch(SQLException e)
        {
           e.printStackTrace();
        }
    }

    private static void viewReservation( Statement statement) throws SQLException
    {
            String query = "Select * from reservations; ";
            try( ResultSet resultSet = statement.executeQuery(query);)
            {


                System.out.println("========================== Current Reservation ================================");
                System.out.println();
                System.out.println("---------------------+--------------------+----------------------+------------------+----------------------+");
                System.out.println("|  Reservation ID    |    Guest Name      |    Room Number       |  Contact Number  |    Reservation Date  |");
                System.out.println("---------------------+--------------------+----------------------+---------------+-------------------------+");

                while(resultSet.next())
                {
                    int guestId = resultSet.getInt("reservations_Id");
                    String guestName = resultSet.getString("guest_Name");
                    int roomNumber = resultSet.getInt("room_Number");
                    String contactNumber = resultSet.getString("contact_Number");
                    String dateOfReservation = resultSet.getTimestamp("reservation_Date").toString();

                    System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s\n",
                            guestId,  guestName, roomNumber, contactNumber, dateOfReservation);
                }
                System.out.println("---------------------+--------------------+----------------------+---------------+-------------------------+");

            }
    }

    private static void getRoomNumber(Scanner scanner, Statement statement)
    {
        try
        {
            System.out.print("Enter Guest ID: ");
            int guestId = scanner.nextInt();
            System.out.print("Enter Guest Name: ");
            String guestName = scanner.next();

            String query = "SELECT room_Number FROM reservations"+
                    "WHERE reservations_ID= "+guestId+
                    " AND guest_Name= '"+guestName+"'";
            try(ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_Number");
                    System.out.printf("Rooms Number of reservation Id is " + guestId + "\nName of Guest: " + guestName + "\nRoom Number: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for given id and guest name.");
                }
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void updateReservation( Scanner scanner, Statement statement) {

       try
       {
           System.out.println("Enter Reservation Id: ");
           int reservationId = scanner.nextInt();
           scanner.nextLine();
           if (!resevationExists(reservationId, statement))
           {
               System.out.println("Reservation not found for given Id.");
                return;
           }
           System.out.println("Enter new guest Name: ");
           String newGuestName = scanner.nextLine();
           System.out.println("Enter room Number: ");
           int newRoomNumber = scanner.nextInt();
           System.out.println("Enter Contact Number: ");
           String newContactNumber =scanner.next();

           String query = "update reservations "+
                   "SET guest_Name='"+newGuestName+"',"+
                   "room_Number ="+newRoomNumber+","+
                   "contact_Number ='"+newContactNumber+"',"+
                   "reservation_Id ="+reservationId;

           int affectedRows = statement.executeUpdate(query);
           if(affectedRows>0)
           {
               System.out.println("Reservation updated successfully.");
           }
           else
           {
               System.out.println("Reservation update failed.");
           }

       }
       catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Scanner scanner, Statement statement)
    {
        try
        {
            System.out.println("Enter Reservation Id: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();
            if (!resevationExists( reservationId, statement))
            {
                System.out.println("Reservation not found for given Id.");
                return;
            }
            String query = "Delete from reservation where reservation_Id ="+reservationId;

            int affectedRows = statement.executeUpdate(query);
            if (affectedRows>0)
            {
                System.out.println("Reservation deleted successfully.");
            }
            else
            {
                System.out.println("Deletion failed.");
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    private static boolean resevationExists(int reservationId,Statement statement)
    {
        try
        {
            String query ="Select reservations_ID from reservations where reservation_Id ="+reservationId;

            try(ResultSet resultSet = statement.executeQuery(query)) {

                return resultSet.next();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public static void exit() throws InterruptedException
    {
        System.out.print("Exiting System");
        int count =5;
        while(count!=0)
        {
            System.out.print(".");
            Thread.sleep(500);
            count--;
        }
        System.out.println("\nThank you for using Hotel Reservation System");
    }

}