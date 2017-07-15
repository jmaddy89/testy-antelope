/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.aic.android.aicmobile.backend;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "aicDataAPI",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.aicmobile.android.aic.com",
                ownerName = "backend.aicmobile.android.aic.com",
                packagePath = ""
        )
)
public class MyEndpoint {

    private static final String URL = "jdbc:google:mysql://aic-mobile-5fdf1:us-east1:aic/aic";
    private static final String USER = "root";
    private static final String PASSWORD = "AIC7015203";
    private static final String DRIVER = "com.mysql.jdbc.GoogleDriver";

    @ApiMethod(name = "projectQuery")
    public List<Projects> projectQuery() {

        List<Projects> projects = new ArrayList<>();

        try {

            Class.forName(DRIVER);

            String finalQuery = "SELECT proj_id, proj_number, customer, description, budget, burn, project_status, aic_contact, customer_contact FROM aic.google_project_list WHERE (project_status=1 OR project_status=2) AND proj_number>100";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);

                ResultSet rs = stmt.executeQuery();

                //Loop through result set and set project list
                while (rs.next()) {
                    Projects proj = new Projects();
                    proj.setProjectId(rs.getInt("proj_id"));
                    proj.setProjectNum(rs.getInt("proj_number"));
                    proj.setCustomer(rs.getString("customer"));
                    proj.setProjectDesc(rs.getString("description"));
                    proj.setBudget(rs.getFloat("budget"));
                    proj.setBurn(rs.getFloat("burn"));
                    proj.setProjectStatus(rs.getInt("project_status"));
                    proj.setAicContact("aic_contact");
                    proj.setCustomerContact("customer_contact");
                    projects.add(proj);
                }
            } catch(Exception e) {
                Projects fail = new Projects();
                fail.setCustomer(e.toString());
                projects.add(fail);
                return projects;
            }
        } catch(Exception e) {
            Projects fail = new Projects();
            fail.setCustomer(e.toString());
            projects.add(fail);
            return projects;
        }
        return projects;
    }

    @ApiMethod(name = "getMainPage")
    public MainPage getMainPage() {
        MainPage mainPage = new MainPage();
        try {

            Class.forName(DRIVER);

            String finalQuery = "SELECT * FROM aic.google_main_page";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    mainPage.setActiveProjects(rs.getInt("active_project_count"));
                    mainPage.setCompleteProjects(rs.getInt("complete_project_count"));
                    mainPage.setIncompleteRFQs(rs.getInt("incomplete_rfq_count"));
                    mainPage.setCompleteRFQs(rs.getInt("pending_project_count"));
                }

            } catch (Exception e) {

            }
        } catch (Exception e) {

        }

        return mainPage;
    }

    @ApiMethod(name = "getCustomerList")
    public List<Customers> getCustomerList() {

        List<Customers> customers = new ArrayList<>();
        try {

            Class.forName(DRIVER);

            String finalQuery = "SELECT ndx, customer FROM aic.customers ORDER BY customer ASC";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);

                ResultSet rs = stmt.executeQuery();

                //Loop through result set and set customer list
                while (rs.next()) {
                    Customers customer = new Customers();
                    customer.setId(rs.getInt("ndx"));
                    customer.setName(rs.getString("customer"));
                    customers.add(customer);
                }
            } catch(Exception e) {
                Customers fail = new Customers();
                fail.setName(e.toString());
                customers.add(fail);
                return customers;
            }
        } catch(Exception e) {
            Customers fail = new Customers();
            fail.setName(e.toString());
            customers.add(fail);
            return customers;
        }
        return customers;
    }

    @ApiMethod(name = "getContactList")
    public List<CustomerContacts> getContactList() {

        List<CustomerContacts> contacts = new ArrayList<>();
        try {

            Class.forName(DRIVER);

            String finalQuery = "SELECT DISTINCT customer_contact FROM aic.projects WHERE customer_contact IS NOT NULL AND customer_contact != '' ORDER BY customer_contact ASC";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);

                ResultSet rs = stmt.executeQuery();

                //Loop through result set and set customer contact list
                while (rs.next()) {
                    CustomerContacts contact = new CustomerContacts();
                    contact.setCustomerContact(rs.getString("customer_contact"));
                    contacts.add(contact);
                }
            } catch(Exception e) {
                CustomerContacts fail = new CustomerContacts();
                fail.setCustomerContact(e.toString());
                contacts.add(fail);
                return contacts;
            }
        } catch(Exception e) {
            CustomerContacts fail = new CustomerContacts();
            fail.setCustomerContact(e.toString());
            contacts.add(fail);
            return contacts;
        }
        return contacts;
    }

    @ApiMethod(name = "getDeliverables")
    public List<Deliverables> getDeliverables(@Named("projectNumber") int projectNumber) {

        List<Deliverables> deliverables = new ArrayList<>();
        try {

            Class.forName(DRIVER);

            String finalQuery = "SELECT * FROM labor_description WHERE proj_number=?";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);
                stmt.setInt(1, projectNumber);

                ResultSet rs = stmt.executeQuery();

                //Loop through result set and set customer contact list
                while (rs.next()) {
                    Deliverables deliverable = new Deliverables();
                    deliverable.setProjectNumber(rs.getInt("proj_number"));
                    deliverable.setCoNumber(rs.getInt("co_number"));
                    deliverable.setOptionNumber(rs.getInt("option_number"));
                    deliverable.setDeliverableId(rs.getInt("task_number"));
                    deliverable.setDeliverableDesc(rs.getString("task_description"));
                    deliverables.add(deliverable);
                }
            } catch(Exception e) {
                Deliverables fail = new Deliverables();
                fail.setDeliverableDesc(e.toString());
                deliverables.add(fail);
                return deliverables;
            }
        } catch(Exception e) {
            Deliverables fail = new Deliverables();
            fail.setDeliverableDesc(e.toString());
            deliverables.add(fail);
            return deliverables;
        }
        return deliverables;
    }

    @ApiMethod(name = "getRoles")
    public List<TimeEntryRoles> getRoles(@Named("userId") String userId) {

        List<TimeEntryRoles> roles = new ArrayList<>();
        try {

            Class.forName(DRIVER);

            String finalQuery = "SELECT * FROM google_time_entry_roles WHERE firebase_id=?";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);
                stmt.setString(1, userId);

                ResultSet rs = stmt.executeQuery();

                //Loop through result set and set customer contact list
                while (rs.next()) {
                    TimeEntryRoles role = new TimeEntryRoles();
                    role.setRoleId(rs.getInt("role_id"));
                    role.setUserId(rs.getInt("user_id"));
                    role.setRoleDesc(rs.getString("role_name"));
                    roles.add(role);
                }
            } catch(Exception e) {
                TimeEntryRoles fail = new TimeEntryRoles();
                fail.setRoleDesc(e.toString());
                roles.add(fail);
                return roles;
            }
        } catch(Exception e) {
            TimeEntryRoles fail = new TimeEntryRoles();
            fail.setRoleDesc(e.toString());
            roles.add(fail);
            return roles;
        }
        return roles;
    }

    @ApiMethod(name = "submitTime")
    public MyBean submitTime(NewTimeEntry timeInfo) {

        String s = null;
        try {

            Class.forName(DRIVER);

            String createTimeEntry = "INSERT INTO aic.time_entry(proj_id, project_number, user_id, role_id, deliverable_id, co_number, option_number, time, date, billable, notes) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

            try {
                // Rebuild connection and statement
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(createTimeEntry);

                // Add data to fill in values corresponding with above query
                stmt.setInt(1, timeInfo.getProjectId());
                stmt.setInt(2, timeInfo.getProjectNumber());
                stmt.setInt(3, timeInfo.getUserId());
                stmt.setInt(4, timeInfo.getRoleId());
                stmt.setInt(5, timeInfo.getDeliverableId());
                stmt.setInt(6, timeInfo.getCoNumber());
                stmt.setInt(7, timeInfo.getOptionNumber());
                stmt.setFloat(8, timeInfo.getTime());
                stmt.setString(9, timeInfo.getDate());
                stmt.setBoolean(10, timeInfo.isBillable());
                stmt.setString(11, timeInfo.getNote());

                // Execute query
                stmt.executeUpdate();

                // Return response to indicate success!
                s = "Time entry added successfully!";
            } catch (SQLException e) {
                MyBean sqlFail = new MyBean();
                sqlFail.setData("Failed trying to add time: " + e.toString());
                return sqlFail;
            }
        } catch(Exception e) {
            MyBean totalFail = new MyBean();
            totalFail.setData("Failed loading driver: " + e.toString());
        }
        MyBean result = new MyBean();
        result.setData(s);
        return result;
    }

    @ApiMethod(name = "submitRFQ")
    public MyBean submitRFQ(NewProject projectInfo) {

        String s = null;
        int projNumber = 0;

        try {

            Class.forName(DRIVER);

            String projNumberQuery = "SELECT MAX(proj_number) + 1 FROM aic.projects";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(projNumberQuery);

                ResultSet rs = stmt.executeQuery();

                // Check for result, then set project number
                if (rs.next()) {
                    projNumber = rs.getInt(1);
                }

            } catch(Exception e) {
                MyBean projNumberFail = new MyBean();
                projNumberFail.setData("Failed getting project number: " + e.toString());
                return projNumberFail;
            }

            String createProjectQuery = "INSERT INTO aic.projects(proj_number, customer, description, aic_contact, customer_contact, aic_contact_info, folder_path) VALUES(?,?,?,?,?,?,?)";
            String folderPath = "\\\\aicstorage\\AIC\\Jobs\\Prj" + projNumber + "-" + projectInfo.getCustomer() + "-" + projectInfo.getDescription();
            try {
                // Rebuild connection and statement
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(createProjectQuery);

                // Add data to fill in values corresponding with above query
                stmt.setInt(1, projNumber);
                stmt.setString(2, projectInfo.getCustomer());
                stmt.setString(3, projectInfo.getDescription());
                stmt.setString(4, projectInfo.getAicContact());
                stmt.setString(5, projectInfo.getCustomerContact());
                stmt.setString(6, projectInfo.getAicContactInfo());
                stmt.setString(7, folderPath);

                // Execute query
                stmt.executeUpdate();

                // Return response to indicate success!
                s = projectInfo.getCustomer() + " - " + projectInfo.getDescription() + " added successfully!";
            } catch (SQLException e) {
                MyBean sqlFail = new MyBean();
                sqlFail.setData("Failed trying to insert: " + e.toString());
                return sqlFail;
            }
        } catch(Exception e) {
            MyBean totalFail = new MyBean();
            totalFail.setData("Failed loading driver: " + e.toString());
        }
        MyBean result = new MyBean();
        result.setData(s);
        return result;
    }

    @ApiMethod(name = "createERPEntry")
    public MyBean createERPEntry(NewFirebaseLogin loginInfo) {
        String name = null;
        int erpUserId = 0;
        String email = loginInfo.getEmail();
        String uid = loginInfo.getUid();

        try {

            Class.forName(DRIVER);

            String userIdQuery = "SELECT user_id FROM aic.aic_emp_user_ci WHERE contact_type='email' AND contact_value=?";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(userIdQuery);
                stmt.setString(1, email);

                ResultSet rs = stmt.executeQuery();

                // Check for result, then set project number
                if (rs.next()) {
                    erpUserId = rs.getInt(1);
                }

            } catch(Exception e) {
                MyBean userIdFail = new MyBean();
                userIdFail.setData("Failed getting project number: " + e.toString());
                return userIdFail;
            }

            // Update the users table with the firebase id based on the user id of the ERP
            String createUidQuery = "UPDATE aic.aic_emp_users SET firebase_id=? WHERE id=?";
            try {
                //Rebuild connection and statement
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(createUidQuery);

                //Add data to fill in values corresponding with above query
                stmt.setString(1, uid);
                stmt.setInt(2, erpUserId);

                //Execute query
                stmt.executeUpdate();

            } catch (SQLException e) {
                MyBean sqlFail = new MyBean();
                sqlFail.setData("Failed trying to insert: " + e.toString());
                return sqlFail;
            }

            String userNameQuery = "SELECT CONCAT(fname, ' ', lname) FROM aic.aic_emp_users WHERE id=?";
            try {
                // Rebuild connection and statement
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(userNameQuery);

                // Add user id to where clause
                stmt.setInt(1, erpUserId);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    name = rs.getString(1);
                }
            } catch (SQLException e) {
                MyBean sqlFail = new MyBean();
                sqlFail.setData("Failed trying to get display name: " + e.toString());
                return sqlFail;
            }
        } catch(Exception e) {
            MyBean totalFail = new MyBean();
            totalFail.setData("Failed loading driver: " + e.toString());
        }
        MyBean result = new MyBean();
        result.setData(name);
        return result;
    }

    @ApiMethod(name = "downloadTime")
    public List<TimeEntryDay> downloadTime(TimeEntryRequestDayInfo request) {
        List<TimeEntryDay> time = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, request.getYear());
        cal.set(Calendar.WEEK_OF_YEAR, request.getWeekNumber());

        // Set day of week to sunday
        cal.set(Calendar.DAY_OF_WEEK, 1);

        // Get start date
        String startDate = sdf.format(cal.getTime());

        // Add days to get to saturday as end date
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = sdf.format(cal.getTime());

        try {

            Class.forName(DRIVER);

            String timeQuery = "SELECT * FROM aic.google_time_entry WHERE firebase_id=? and date BETWEEN ? AND ? ORDER BY proj_number";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(timeQuery);
                stmt.setString(1, request.getUserId());
                stmt.setString(2, startDate);
                stmt.setString(3, endDate);

                ResultSet rs = stmt.executeQuery();

                // Loop through results and build list
                while (rs.next()) {
                    TimeEntryDay timeData = new TimeEntryDay();

                    timeData.setProjectNumber(rs.getInt("proj_number"));
                    timeData.setCustomer(rs.getString("customer"));
                    timeData.setDescription(rs.getString("description"));
                    timeData.setBillable(rs.getBoolean("billable"));
                    timeData.setSubmitted(rs.getBoolean("submitted"));
                    timeData.setDate(rs.getDate("date"));
                    timeData.setTime(rs.getFloat("time"));
                    timeData.setNote(rs.getString("notes"));

                    time.add(timeData);
                }

            } catch(Exception e) {
                TimeEntryDay queryFail = new TimeEntryDay();
                queryFail.setCustomer("Failed getting project number: " + e.toString());
                time.add(queryFail);
                return time;
            }

        } catch(Exception e) {
            TimeEntryDay totalFail = new TimeEntryDay();
            totalFail.setCustomer("Failed loading driver: " + e.toString());
            time.add(totalFail);
        }

        return time;
    }

    @ApiMethod(name = "submitWeekTime")
    public MyBean submitWeekTime(TimeEntryRequestDayInfo request) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, request.getYear());
        cal.set(Calendar.WEEK_OF_YEAR, request.getWeekNumber());

        // Set day of week to sunday
        cal.set(Calendar.DAY_OF_WEEK, 1);

        // Get start date
        String startDate = sdf.format(cal.getTime());

        // Add days to get to saturday as end date
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = sdf.format(cal.getTime());

        int userId = 0;


        try {

            Class.forName(DRIVER);
            String userIdQuery = "SELECT id FROM aic.aic_emp_users WHERE firebase_id=?";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(userIdQuery);
                stmt.setString(1, request.getUserId());

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            } catch (Exception e) {
                MyBean queryFail = new MyBean();
                queryFail.setData("Failed to find user id");
                return queryFail;
            }

            String timeQuery = "UPDATE aic.time_entry SET submitted=1 WHERE user_id=? and date BETWEEN ? AND ?";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(timeQuery);
                stmt.setInt(1, userId);
                stmt.setString(2, startDate);
                stmt.setString(3, endDate);

                stmt.executeUpdate();

            } catch(Exception e) {
                MyBean queryFail = new MyBean();
                queryFail.setData("Failed to submit time, try again");
                return queryFail;
            }

        } catch(Exception e) {
            MyBean queryFail = new MyBean();
            queryFail.setData("Failed to load driver");
            return queryFail;
        }

        MyBean time = new MyBean();
        time.setData("Time submitted successfully!");
        return time;
    }

    @ApiMethod(name = "getProjectBreakdown")
    public ProjectBreakdown getProjectBreakdown(@Named("projectNumber") int projectNumber) {
        ProjectBreakdown breakdown = new ProjectBreakdown();
        try {

            Class.forName(DRIVER);

            String finalQuery = "SELECT * FROM aic.google_project_detail_breakdown WHERE proj_number=?";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);
                stmt.setInt(1, projectNumber);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    breakdown.setProjNumber(rs.getInt("proj_number"));
                    breakdown.setLaborBudget(rs.getFloat("labor"));
                    breakdown.setMaterialBudget(rs.getFloat("material"));
                    breakdown.setSubcontractorBudget(rs.getFloat("subcontractor"));
                    breakdown.setContingencyBudget(rs.getFloat("contingency"));
                }

            } catch (Exception e) {

            }
        } catch (Exception e) {

        }

        return breakdown;
    }

    @ApiMethod(name = "getExpenses")
    public List<Expenses> getExpenses(@Named("projectNumber") int projectNumber) {
        List<Expenses> expenses = new ArrayList<>();

        try {

            Class.forName(DRIVER);

            String timeQuery = "SELECT * FROM aic.google_expenses WHERE proj_number=?";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(timeQuery);
                stmt.setInt(1, projectNumber);

                ResultSet rs = stmt.executeQuery();

                // Loop through results and build list
                while (rs.next()) {
                    Expenses expense = new Expenses();

                    expense.setPoNumber(rs.getInt("po_number"));
                    expense.setAmount(rs.getFloat("amount"));
                    expense.setCategory(rs.getString("category"));
                    expense.setDate(rs.getString("date"));
                    expense.setNotes(rs.getString("notes"));
                    expense.setVendor(rs.getString("vendor"));

                    expenses.add(expense);
                }

            } catch(Exception e) {
                Expenses queryFail = new Expenses();
                queryFail.setCategory("Failed getting project number: " + e.toString());
                expenses.add(queryFail);
                return expenses;
            }

        } catch(Exception e) {
            Expenses totalFail = new Expenses();
            totalFail.setCategory("Failed loading driver: " + e.toString());
            expenses.add(totalFail);
        }

        return expenses;
    }

}
