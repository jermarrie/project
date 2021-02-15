package by.jwd.task6.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import by.jwd.task6.entity.Hotel;
import by.jwd.task6.service.HotelService;
import by.jwd.task6.service.ServiceException;
import by.jwd.task6.service.impl.HotelServiceImpl;

import java.io.File;
import java.io.IOException;

@WebServlet(urlPatterns = { "/upload/*" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "uploads";
    private HotelService hotelService = new HotelServiceImpl();

    private static final String HOTEL_ATTRIBUTE = "hotel";
    private static final String MAIN_PAGE = "Controller?command=to_main_page";
    private static final String ERROR_PAGE = "Controller?command=to_error_page";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String applicationDir = request.getServletContext().getRealPath("/redwood"); // TODO временная дир-я
        String uploadFileDir = applicationDir + File.separator + UPLOAD_DIR + File.separator;

        File fileSaveDir = new File(uploadFileDir);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }

        int hotelId = Integer.valueOf(((Hotel) request.getSession().getAttribute(HOTEL_ATTRIBUTE)).getHotelId());
        request.getParts().stream().forEach(part -> {
            try {
                String path = uploadFileDir + part.getSubmittedFileName();
                part.write(path);
                hotelService.insertHotelPhoto(path, hotelId);
            } catch (IOException e) {
               // TODO заменить на ajax сообщение
                e.printStackTrace();
            } catch (ServiceException e) {
                //TODO
                e.printStackTrace();
            }
        });
        request.getRequestDispatcher(MAIN_PAGE).forward(request, response); // TODO заменить на ajax сообщение
    }
}
