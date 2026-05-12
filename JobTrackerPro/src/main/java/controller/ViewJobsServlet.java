package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dao.JobDAO;
import model.Job;

@WebServlet("/viewJobs")
public class ViewJobsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            JobDAO dao = new JobDAO();
            List<Job> list = dao.getAllJobs();

            out.println("<html><head>");

            out.println("<meta charset='UTF-8'>");
            out.println("<link rel='stylesheet' href='css/app.css'>");

            out.println("</head><body>");

            /* SIDEBAR */
            out.println("<div class='sidebar'>");
            out.println("<h2><a href='" + request.getContextPath() + "/index.html' style='text-decoration:none; color:white; font-size:20px; font-weight:600;'>🚀 JobTrackerPro</a></h2>");
            out.println("<a href='" + request.getContextPath() + "/dashboard'>Dashboard</a>");
            out.println("<a class='active' href='" + request.getContextPath() + "/viewJobs'>Jobs</a>");
            out.println("<a href='" + request.getContextPath() + "/add.html'>Add Job</a>");
            out.println("</div>");

            /* MAIN */
            out.println("<div class='main'>");

            out.println("<h2>Job Applications</h2>");
            out.println("<div style='margin-top:20px;'></div>");
            /* CARD */
            out.println("<div style='background:white; padding:30px; border-radius:16px; box-shadow:0 10px 25px rgba(0,0,0,0.05);'>");

            /* TABLE */
            out.println("<table style='width:100%; border-collapse:collapse;'>");

            out.println("<tr style='color:#94a3b8; font-size:13px; text-transform:uppercase;'>");
            out.println("<th>Role</th>");
            out.println("<th>Company</th>");
            out.println("<th>Status</th>");
            out.println("<th>Actions</th>");
            out.println("</tr>");
            
            for (Job j : list) {

            	out.println("<tr style='border-bottom:1px solid #e2e8f0;'>");

            	out.println("<td style='padding:18px 10px;'>"+j.getRole()+"</td>");
            	out.println("<td style='padding:18px 10px;'>"+j.getCompany()+"</td>");

                out.println("<td>");
                String color = "#60a5fa"; // applied

                if(j.getStatus().equals("Interview")) color = "#fbbf24";
                if(j.getStatus().equals("Offer")) color = "#34d399";
                if(j.getStatus().equals("Rejected")) color = "#f87171";

                out.println("<span style='padding:6px 12px; border-radius:20px; font-size:12px; color:white; background:"+color+";'>"+j.getStatus()+"</span>");
                out.println("</td>");

                out.println("<td>");
                out.println("<a href='" + request.getContextPath() + "/editJob?id=" + j.getId() + "' style='margin-right:10px; padding:6px 12px; background:#fcd34d; border-radius:8px; text-decoration:none; color:black;'>Edit</a>");

                out.println("<a href='" + request.getContextPath() + "/deleteJob?id=" + j.getId() + "' style='padding:6px 12px; background:#f87171; color:white; border-radius:8px; text-decoration:none;'>Delete</a>");
                out.println("</td>");

                out.println("</tr>");
            }
            
            out.println("</table>");
            out.println("</div>"); // card
            out.println("</div>"); // main

            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error loading jobs");
        }
    }
}