package EventPackage;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventOperations {
          private static final String URL = "jdbc:mysql://localhost:3306/event_reminder";
          private static final String USER = "root";
          private static final String PASSWORD = "root";

          public static class Event {
                    private int id;
                    private String title;
                    private String description;
                    private LocalDateTime dateTime;

                    public Event(int id, String title, String description, LocalDateTime dateTime) {
                              this.id = id;
                              this.title = title;
                              this.description = description;
                              this.dateTime = dateTime;
                    }

                    public int getId() {
                              return id;
                    }

                    public String getTitle() {
                              return title;
                    }

                    public String getDescription() {
                              return description;
                    }

                    public LocalDateTime getDateTime() {
                              return dateTime;
                    }

                    public String toString() {
                              return "[ID: " + id + "] " + title + " (" + dateTime.toString() + ")";
                    }
          }

          //? 1. CREATE Operation
          public boolean createEvent(String title, String description, LocalDateTime dateTime) {
                    String query = "INSERT INTO events (title, description, event_date_time) VALUES (?, ?, ?)";
                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                                        PreparedStatement pstmt = conn.prepareStatement(query)) {

                              pstmt.setString(1, title);
                              pstmt.setString(2, description);
                              pstmt.setTimestamp(3, Timestamp.valueOf(dateTime));

                              pstmt.executeUpdate();
                              return true;
                    } catch (SQLException e) {
                              e.printStackTrace();
                              return false;
                    }
          }

          //? 2. READ Operation
          public List<Event> readEvents() {
                    List<Event> events = new ArrayList<>();
                    String query = "SELECT id, title, description, event_date_time FROM events ORDER BY event_date_time ASC";

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                                        Statement stmt = conn.createStatement();
                                        ResultSet rs = stmt.executeQuery(query)) {

                              while (rs.next()) {
                                        int id = rs.getInt("id");
                                        String title = rs.getString("title");
                                        String description = rs.getString("description");
                                        LocalDateTime dateTime = rs.getTimestamp("event_date_time").toLocalDateTime();

                                        events.add(new Event(id, title, description, dateTime));
                              }
                    } catch (SQLException e) {
                              e.printStackTrace();
                    }
                    return events;
          }

          //? 3. UPDATE Operation
          public boolean updateEvent(int id, String title, String description, LocalDateTime dateTime) {
                    String query = "UPDATE events SET title = ?, description = ?, event_date_time = ? WHERE id = ?";

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                                        PreparedStatement pstmt = conn.prepareStatement(query)) {

                              pstmt.setString(1, title);
                              pstmt.setString(2, description);
                              pstmt.setTimestamp(3, Timestamp.valueOf(dateTime));
                              pstmt.setInt(4, id);

                              int rowsAffected = pstmt.executeUpdate();
                              return rowsAffected > 0;
                    } catch (SQLException e) {
                              e.printStackTrace();
                              return false;
                    }
          }

          //? 4. DELETE Operation
          public boolean deleteEvent(int id) {
                    String query = "DELETE FROM events WHERE id = ?";

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                                        PreparedStatement pstmt = conn.prepareStatement(query)) {

                              pstmt.setInt(1, id);

                              int rowsAffected = pstmt.executeUpdate();
                              return rowsAffected > 0;
                    } catch (SQLException e) {
                              e.printStackTrace();
                              return false;
                    }
          }
}
