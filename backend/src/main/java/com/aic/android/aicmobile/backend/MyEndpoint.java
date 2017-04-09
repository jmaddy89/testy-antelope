/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.aic.android.aicmobile.backend;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;


import java.sql.Date;
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

    private static final String URL = "jdbc:google:mysql://i-melody-158021:us-east1:aic/aic";
    private static final String USER = "root";
    private static final String PASSWORD = "AIC7015203";

    @ApiMethod(name = "projectQuery")
    public List<Projects> projectQuery() {

        List<Projects> projects = new ArrayList<>();

        try {

            Class.forName("com.mysql.jdbc.GoogleDriver");

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

            Class.forName("com.mysql.jdbc.GoogleDriver");

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

            Class.forName("com.mysql.jdbc.GoogleDriver");

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

            Class.forName("com.mysql.jdbc.GoogleDriver");

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

            Class.forName("com.mysql.jdbc.GoogleDriver");

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

    @ApiMethod(name="createERPEntry")
    public MyBean createERPEntry(NewFirebaseLogin loginInfo) {
        String name = null;
        int erpUserId = 0;
        String email = loginInfo.getEmail();
        String uid = loginInfo.getUid();

        try {

            Class.forName("com.mysql.jdbc.GoogleDriver");

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
}
