package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dao.JobDAO;

@WebServlet("/deleteJob")
public class DeleteJobServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        try {
            JobDAO dao = new JobDAO();
            dao.deleteJob(id);

            response.sendRedirect("viewJobs");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error deleting job");
        }
    }
}