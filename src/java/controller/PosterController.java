package controller;

import dao.PosterDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import model.Poster;

@WebServlet("/PosterController")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)  // 50MB
public class PosterController extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Determine the upload path
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir(); // Create the upload directory if it doesn't exist
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String filePath = "";

        // Save the uploaded file
        for (Part part : request.getParts()) {
            String fileName = extractFileName(part);
            if (fileName != null && !fileName.isEmpty()) {
                String fullFilePath = uploadPath + File.separator + fileName;
                part.write(fullFilePath); // Save the file to the server
                filePath = UPLOAD_DIR + "/" + fileName; // Save the relative path
            }
        }

        // Save poster details in the database
        Poster poster = new Poster(title, description, filePath);
        PosterDAO posterDAO = new PosterDAO();
        posterDAO.savePoster(poster);

        // Redirect to the dashboard to display updated list of posters
        response.sendRedirect("PosterController");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve all posters from the database
        PosterDAO posterDAO = new PosterDAO();
        request.setAttribute("posters", posterDAO.getAllPosters());

        // Forward the request to the dashboard JSP
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String content : contentDisp.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return null;
    }
}
