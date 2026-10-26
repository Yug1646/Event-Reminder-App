import EventPackage.EventOperations;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main extends JFrame {
          private EventOperations eventOps;
          private DefaultListModel<EventOperations.Event> listModel;
          private JList<EventOperations.Event> eventList;
          private JTextField titleField, dateTimeField;
          private JTextArea descArea;

          private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

          public Main() {
                    eventOps = new EventOperations();

                    setTitle("Event Reminder App");
                    setSize(650, 500);
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    setLocationRelativeTo(null);

                    setLayout(new BorderLayout(10, 10));

                    // 1. Form Panel (Inputs for Title, Description, Date/Time)
                    JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    formPanel.add(new JLabel("Event Title:"));
                    titleField = new JTextField();
                    formPanel.add(titleField);

                    formPanel.add(new JLabel("Description:"));
                    descArea = new JTextArea(2, 20);
                    formPanel.add(new JScrollPane(descArea));

                    formPanel.add(new JLabel("Date & Time (yyyy-MM-dd HH:mm):"));
                    dateTimeField = new JTextField(LocalDateTime.now().format(FORMATTER));
                    formPanel.add(dateTimeField);

                    add(formPanel, BorderLayout.NORTH);

                    listModel = new DefaultListModel<>();
                    eventList = new JList<>(listModel);
                    eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    add(new JScrollPane(eventList), BorderLayout.CENTER);

                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

                    JButton addButton = new JButton("Add Event");
                    JButton showButton = new JButton("Show Events");
                    JButton updateButton = new JButton("Update Event");
                    JButton deleteButton = new JButton("Delete Event");

                    buttonPanel.add(addButton);
                    buttonPanel.add(showButton);
                    buttonPanel.add(updateButton);
                    buttonPanel.add(deleteButton);

                    add(buttonPanel, BorderLayout.SOUTH);

                    showButton.addActionListener(e -> loadEvents());

                    addButton.addActionListener(e -> {
                              String title = titleField.getText().trim();
                              String desc = descArea.getText().trim();
                              String dtStr = dateTimeField.getText().trim();

                              if (title.isEmpty() || dtStr.isEmpty()) {
                                        JOptionPane.showMessageDialog(this, "Title and Date/Time cannot be empty!",
                                                            "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                              }

                              try {
                                        LocalDateTime dt = LocalDateTime.parse(dtStr, FORMATTER);
                                        boolean success = eventOps.createEvent(title, desc, dt);
                                        if (success) {
                                                  JOptionPane.showMessageDialog(this, "Event added successfully!");
                                                  clearFields();
                                                  loadEvents();
                                        } else {
                                                  JOptionPane.showMessageDialog(this,
                                                                      "Failed to add event to database.", "Error",
                                                                      JOptionPane.ERROR_MESSAGE);
                                        }
                              } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(this,
                                                            "Invalid date format! Please use yyyy-MM-dd HH:mm",
                                                            "Format Error", JOptionPane.ERROR_MESSAGE);
                              }
                    });

                    updateButton.addActionListener(e -> {
                              EventOperations.Event selected = eventList.getSelectedValue();
                              if (selected == null) {
                                        JOptionPane.showMessageDialog(this,
                                                            "Please select an event from the list to update.",
                                                            "Warning", JOptionPane.WARNING_MESSAGE);
                                        return;
                              }

                              String title = titleField.getText().trim();
                              String desc = descArea.getText().trim();
                              String dtStr = dateTimeField.getText().trim();

                              if (title.isEmpty() || dtStr.isEmpty()) {
                                        JOptionPane.showMessageDialog(this, "Title and Date/Time cannot be empty!",
                                                            "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                              }

                              try {
                                        LocalDateTime dt = LocalDateTime.parse(dtStr, FORMATTER);
                                        boolean success = eventOps.updateEvent(selected.getId(), title, desc, dt);
                                        if (success) {
                                                  JOptionPane.showMessageDialog(this, "Event updated successfully!");
                                                  clearFields();
                                                  loadEvents();
                                        } else {
                                                  JOptionPane.showMessageDialog(this, "Failed to update event.",
                                                                      "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                              } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(this,
                                                            "Invalid date format! Please use yyyy-MM-dd HH:mm",
                                                            "Format Error", JOptionPane.ERROR_MESSAGE);
                              }
                    });
                    deleteButton.addActionListener(e -> {
                              EventOperations.Event selected = eventList.getSelectedValue();
                              if (selected == null) {
                                        JOptionPane.showMessageDialog(this,
                                                            "Please select an event from the list to delete.",
                                                            "Warning", JOptionPane.WARNING_MESSAGE);
                                        return;
                              }

                              int confirm = JOptionPane.showConfirmDialog(this,
                                                  "Are you sure you want to delete this event?", "Confirm Deletion",
                                                  JOptionPane.YES_NO_OPTION);
                              if (confirm == JOptionPane.YES_OPTION) {
                                        boolean success = eventOps.deleteEvent(selected.getId());
                                        if (success) {
                                                  JOptionPane.showMessageDialog(this, "Event deleted successfully!");
                                                  clearFields();
                                                  loadEvents();
                                        } else {
                                                  JOptionPane.showMessageDialog(this, "Failed to delete event.",
                                                                      "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                              }
                    });

                    eventList.addListSelectionListener(e -> {
                              EventOperations.Event selected = eventList.getSelectedValue();
                              if (selected != null) {
                                        titleField.setText(selected.getTitle());
                                        descArea.setText(selected.getDescription());
                                        dateTimeField.setText(selected.getDateTime().format(FORMATTER));
                              }
                    });
                    loadEvents();
          }

          private void loadEvents() {
                    listModel.clear();
                    List<EventOperations.Event> events = eventOps.readEvents();
                    for (EventOperations.Event ev : events) {
                              listModel.addElement(ev);
                    }
          }

          private void clearFields() {
                    titleField.setText("");
                    descArea.setText("");
                    dateTimeField.setText(LocalDateTime.now().format(FORMATTER));
                    eventList.clearSelection();
          }

          public static void main(String[] args) {
                    SwingUtilities.invokeLater(() -> {
                              new Main().setVisible(true);
                    });
          }
}