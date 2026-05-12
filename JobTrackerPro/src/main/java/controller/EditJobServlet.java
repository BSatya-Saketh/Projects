package controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dao.JobDAO;
import model.Job;

@WebServlet("/editJob")
public class EditJobServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            JobDAO dao = new JobDAO();
            Job j = dao.getJobById(id);

            out.println("<html><head>");

            out.println("<meta charset='UTF-8'>");
            out.println("<link rel='stylesheet' href='css/app.css'>");

            out.println("</head><body>");

            /* SIDEBAR */
            out.println("<div class='sidebar'>");
            out.println("<h2><a href='" + request.getContextPath() + "/index.html' style='text-decoration:none; color:white; font-size:20px; font-weight:600;'>🚀 JobTrackerPro</a></h2>");
            out.println("<a href='" + request.getContextPath() + "/dashboard'>Dashboard</a>");
            out.println("<a href='" + request.getContextPath() + "/viewJobs'>Jobs</a>");
            out.println("<a href='" + request.getContextPath() + "/add.html'>Add Job</a>");
            out.println("</div>");

            /* MAIN */
            out.println("<div class='main'>");

            out.println("<h2>Edit Job</h2>");

            out.println("<div class='card' style='max-width:500px;'>");

            /* FORM */
            out.println("<form action='" + request.getContextPath() + "/updateJob' method='post'>");

            /* HIDDEN ID */
            out.println("<input type='hidden' name='id' value='" + j.getId() + "'>");

            /* COMPANY */
            out.println("<input name='company' value='" + j.getCompany() + "' required>");

            /* ROLE */
            out.println("<input name='role' value='" + j.getRole() + "' required>");

            /* STATUS */
            out.println("<select name='status'>");
            out.println("<option>" + j.getStatus() + "</option>");
            out.println("<option>Applied</option>");
            out.println("<option>Interview</option>");
            out.println("<option>Offer</option>");
            out.println("<option>Rejected</option>");
            out.println("</select>");

            /* NOTES */
            out.println("<input name='notes' value='" + j.getNotes() + "'>");

            /* DATE */
            out.println("<input type='date' name='date' value='" + j.getAppliedDate() + "'>");

            /* BUTTON */
            out.println("<button type='submit'>Update Job</button>");

            out.println("</form>");

            out.println("</div>"); // card
            out.println("</div>"); // main

            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error loading job");
        }
    }
}