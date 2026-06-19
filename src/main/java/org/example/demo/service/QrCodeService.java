package org.example.demo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.example.demo.model.Reservation;
import org.example.demo.util.SecurityUtil;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

public final class QrCodeService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private QrCodeService() {
    }

    public static String buildPayload(Reservation reservation) {
        return String.join("\n",
                "校园通行码预约管理系统",
                "姓名:" + SecurityUtil.maskName(reservation.getVisitorName()),
                "身份证号:" + SecurityUtil.maskIdentityNo(reservation.getIdentityNo()),
                "手机号:" + SecurityUtil.maskPhone(reservation.getPhone()),
                "校区:" + reservation.getCampus(),
                "进校时间:" + reservation.getVisitTime().format(FORMATTER),
                "通行码:" + reservation.getPassCode(),
                "状态:" + reservation.getStatus().getLabel(),
                "生成时间:" + LocalDateTime.now().format(FORMATTER)
        );
    }

    public static BufferedImage generate(Reservation reservation, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        // UTF-8 保证二维码扫描后中文不乱码；MARGIN 控制二维码白边宽度。
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = new QRCodeWriter().encode(buildPayload(reservation), BarcodeFormat.QR_CODE, size, size, hints);
        MatrixToImageConfig config = new MatrixToImageConfig(0xFF111111, 0xFFFFFFFF);
        return MatrixToImageWriter.toBufferedImage(matrix, config);
    }
}
