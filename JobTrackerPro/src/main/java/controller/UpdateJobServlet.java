package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dao.JobDAO;
import model.Job;

@WebServlet("/updateJob")
public class UpdateJobServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        String company = request.getParameter("company");
        String role = request.getParameter("role");
        String status = request.getParameter("status");
        String notes = request.getParameter("notes");
        String date = request.getParameter("date");

        Job job = new Job();
        job.setId(id);
        job.setCompany(company);
        job.setRole(role);
        job.setStatus(status);
        job.setNotes(notes);
        job.setAppliedDate(date);

        try {
            JobDAO dao = new JobDAO();
            dao.updateJob(job);

            response.sendRedirect(request.getContextPath() + "/viewJobs");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error updating job");
        }
    }
}