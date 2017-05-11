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

            String finalQuery = "SELECT proj_number, customer, description, budget, burn, project_status, aic_contact, customer_contact FROM aic.google_project_list WHERE (project_status=1 OR project_status=2) AND proj_number>100";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);

                ResultSet rs = stmt.executeQuery();

                //Loop through result set and set project list
                while (rs.next()) {
                    Projects proj = new Projects();
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

    @ApiMethod(name = "activeProjectCount")
    public MyBean activeProjectCount() {
        String s = null;

        try {

            Class.forName(DRIVER);

            String finalQuery = "SELECT COUNT(*) FROM aic.projects WHERE (project_status=1 OR project_status=2) AND co_number=0 AND option_number=0 AND proj_number>100";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(finalQuery);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    s = rs.getString(1);
                } else {
                    s = "0";
                }

            } catch (Exception e) {

            }
        } catch (Exception e) {

        }

        MyBean response = new MyBean();
        response.setData(s);
        return response;
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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

        // Convert string dates to sql dates
//        java.sql.Date sDate = java.sql.Date.valueOf(startDate);
//        java.sql.Date eDate = java.sql.Date.valueOf(endDate);

        try {

            Class.forName(DRIVER);

            String timeQuery = "SELECT * FROM aic.google_time_entry WHERE user_id=? and date BETWEEN ? AND ?";

            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(timeQuery);
                stmt.setInt(1, request.getUserId());
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
}
