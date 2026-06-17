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

/**
 * 二维码生成服务。
 *
 * <p>任务书提示“生成二维码可以用开源的 zxing.jar 包”，
 * 因此这里使用 ZXing 的 QRCodeWriter 生成真实 PNG 二维码，
 * 而不是用静态图片或 CSS 占位图。</p>
 */
public final class QrCodeService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private QrCodeService() {
    }

    /**
     * 组装写入二维码中的文本内容。
     *
     * <p>二维码中包含脱敏后的姓名、身份证号、手机号、校区、进校时间、
     * 通行码编号、审核状态和生成时间，既满足任务书要求，也避免泄露完整敏感信息。</p>
     */
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

    /**
     * 生成二维码图片。
     *
     * @param reservation 预约记录
     * @param size 二维码图片宽高，单位像素
     * @return 可直接通过 ImageIO 输出为 PNG 的 BufferedImage
     */
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
