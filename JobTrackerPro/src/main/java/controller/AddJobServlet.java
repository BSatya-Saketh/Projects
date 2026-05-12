package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dao.JobDAO;
import model.Job;

@WebServlet("/addJob")
public class AddJobServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String company = request.getParameter("company");
        String role = request.getParameter("role");
        String status = request.getParameter("status");
        String notes = request.getParameter("notes");
        String date = request.getParameter("date");

        Job job = new Job();
        job.setCompany(company);
        job.setRole(role);
        job.setStatus(status);
        job.setNotes(notes);
        job.setAppliedDate(date);

        try {
            JobDAO dao = new JobDAO();
            dao.addJob(job);

            response.sendRedirect("viewJobs");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error adding job");
        }
    }
}