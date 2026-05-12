package controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dao.JobDAO;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            JobDAO dao = new JobDAO();

            int applied = dao.countByStatus("Applied");
            int interview = dao.countByStatus("Interview");
            int offer = dao.countByStatus("Offer");
            int rejected = dao.countByStatus("Rejected");

            int total = applied + interview + offer + rejected;

            out.println("<html><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<link rel='stylesheet' href='css/app.css'>");
            out.println("</head><body>");
            
            out.println("<div class='sidebar'>");
            out.println("<h2><a href='" + request.getContextPath() + "/index.html' style='text-decoration:none; color:white; font-size:20px; font-weight:600;'>🚀 JobTrackerPro</a></h2>");
            out.println("<a href='dashboard'>Dashboard</a>");
            out.println("<a href='viewJobs'>Jobs</a>");
            out.println("<a href='add.html'>Add Job</a>");
            out.println("</div>");

            out.println("<div class='main'>");
            out.println("<h2>Dashboard</h2>");

            out.println("<div style='display:flex; gap:20px;'>");

            out.println("<div style='display:flex; gap:20px; margin-top:20px;'>");

            /* TOTAL */
            out.println("<div style='background:#e0f2fe; padding:20px; border-radius:14px; width:160px;'>");
            out.println("<p style='color:#0369a1;'>Total Jobs</p>");
            out.println("<h2>"+total+"</h2>");
            out.println("</div>");

            /* APPLIED */
            out.println("<div style='background:#dbeafe; padding:20px; border-radius:14px; width:160px;'>");
            out.println("<p style='color:#1d4ed8;'>Applied</p>");
            out.println("<h2>"+applied+"</h2>");
            out.println("</div>");

            /* INTERVIEW */
            out.println("<div style='background:#fef3c7; padding:20px; border-radius:14px; width:160px;'>");
            out.println("<p style='color:#b45309;'>Interview</p>");
            out.println("<h2>"+interview+"</h2>");
            out.println("</div>");

            /* OFFER */
            out.println("<div style='background:#dcfce7; padding:20px; border-radius:14px; width:160px;'>");
            out.println("<p style='color:#15803d;'>Offers</p>");
            out.println("<h2>"+offer+"</h2>");
            out.println("</div>");

            out.println("</div>");

            out.println("</div>");

            out.println("</div>");
            out.println("</div>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error loading dashboard");
        }
    }
}