package dao;

import java.sql.*;
import java.util.*;
import model.Job;
import util.DBConnection;

public class JobDAO {

    // ADD JOB
    public void addJob(Job job) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO jobs(company, role, status, notes, applied_date) VALUES (?, ?, ?, ?, ?)"
        );

        ps.setString(1, job.getCompany());
        ps.setString(2, job.getRole());
        ps.setString(3, job.getStatus());
        ps.setString(4, job.getNotes());
        ps.setDate(5, java.sql.Date.valueOf(job.getAppliedDate()));

        ps.executeUpdate();
    }

    // GET ALL JOBS
    public List<Job> getAllJobs() throws Exception {

        List<Job> list = new ArrayList<>();

        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM jobs");

        while (rs.next()) {
            Job j = new Job();

            j.setId(rs.getInt("id"));
            j.setCompany(rs.getString("company"));
            j.setRole(rs.getString("role"));
            j.setStatus(rs.getString("status"));
            j.setNotes(rs.getString("notes"));
            j.setAppliedDate(rs.getString("applied_date"));

            list.add(j);
        }

        return list;
    }

    // DELETE
    public void deleteJob(int id) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "DELETE FROM jobs WHERE id=?"
        );

        ps.setInt(1, id);
        ps.executeUpdate();
    }
    
    
    public Job getJobById(int id) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement("SELECT * FROM jobs WHERE id=?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        Job j = new Job();

        if (rs.next()) {
            j.setId(rs.getInt("id"));
            j.setCompany(rs.getString("company"));
            j.setRole(rs.getString("role"));
            j.setStatus(rs.getString("status"));
            j.setNotes(rs.getString("notes"));
            j.setAppliedDate(rs.getString("applied_date"));
        }

        return j;
    }
    
    public void updateJob(Job job) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "UPDATE jobs SET company=?, role=?, status=?, notes=?, applied_date=? WHERE id=?"
        );

        ps.setString(1, job.getCompany());
        ps.setString(2, job.getRole());
        ps.setString(3, job.getStatus());
        ps.setString(4, job.getNotes());
        ps.setDate(5, java.sql.Date.valueOf(job.getAppliedDate()));
        ps.setInt(6, job.getId());

        ps.executeUpdate();
    }
    
    public int countByStatus(String status) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "SELECT COUNT(*) FROM jobs WHERE status=?"
        );

        ps.setString(1, status);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

        return 0;
    }
}