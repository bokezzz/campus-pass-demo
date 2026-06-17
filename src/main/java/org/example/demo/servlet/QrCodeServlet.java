package org.example.demo.servlet;

import com.google.zxing.WriterException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.ReservationDao;
import org.example.demo.model.Reservation;
import org.example.demo.service.QrCodeService;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 二维码图片接口。
 *
 * <p>pass-code.jsp 中的 img 标签访问 /qr-code?id=预约ID。
 * 本 Servlet 查询预约记录后，调用 QrCodeService 使用 ZXing 生成 PNG 二维码。</p>
 */
@WebServlet("/qr-code")
public class QrCodeServlet extends HttpServlet {
    private final ReservationDao reservationDao = new ReservationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 二维码内容以数据库记录为准，避免只靠前端参数生成伪造通行码。
            Reservation reservation = reservationDao.findById(Long.parseLong(request.getParameter("id")));
            if (reservation == null) {
                response.sendError(404);
                return;
            }
            // 告诉浏览器这是 PNG 图片，而不是普通 HTML 文本。
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-store");
            ImageIO.write(QrCodeService.generate(reservation, 220), "PNG", response.getOutputStream());
        } catch (SQLException | WriterException e) {
            throw new ServletException(e);
        }
    }
}
