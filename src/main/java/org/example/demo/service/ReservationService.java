package org.example.demo.service;

import org.example.demo.model.Reservation;
import org.example.demo.model.ReservationStatus;
import org.example.demo.model.ReservationType;
import org.example.demo.util.SecurityUtil;

import java.time.LocalDateTime;

/**
 * 预约业务规则服务。
 *
 * <p>Servlet 负责接收请求，DAO 负责数据库读写，而“社会公众自动通过、
 * 公务预约待审核、通行码是否有效”等业务判断集中放在这里，避免控制层变复杂。</p>
 */
public final class ReservationService {
    private ReservationService() {
    }

    /**
     * 初始化一条新预约的系统字段。
     *
     * <p>社会公众预约提交后自动审核通过；公务预约提交后为待审核，
     * 需要后台学校管理员或部门管理员审核后才可有效。</p>
     */
    public static void prepareNewReservation(Reservation reservation) {
        reservation.setApplyTime(LocalDateTime.now());
        reservation.setPassCode(SecurityUtil.generatePassCode());
        if (reservation.getType() == ReservationType.PUBLIC) {
            reservation.setStatus(ReservationStatus.APPROVED);
        } else {
            reservation.setStatus(ReservationStatus.PENDING);
        }
    }

    /**
     * 判断通行码是否有效。
     *
     * <p>根据任务书，通行码要在“有效预约时间内”显示有效效果。
     * 本项目将有效预约时间解释为预约进校日期当天：
     * 只有预约状态为已通过，并且当前日期等于预约进校日期，才显示紫色有效通行码。</p>
     */
    public static boolean isPassCodeValid(Reservation reservation, LocalDateTime now) {
        if (reservation == null || reservation.getStatus() != ReservationStatus.APPROVED || reservation.getVisitTime() == null) {
            return false;
        }
        return reservation.getVisitTime().toLocalDate().equals(now.toLocalDate());
    }

    /**
     * 统计预约人次。
     *
     * <p>预约本人算 1 人；随行人员按文本行数累加，用于后台统计“次数”和“人次”。</p>
     */
    public static int peopleCount(Reservation reservation) {
        if (reservation == null || reservation.getCompanions() == null || reservation.getCompanions().isBlank()) {
            return 1;
        }
        String normalized = reservation.getCompanions().replace("\r\n", "\n").trim();
        return 1 + normalized.split("\\n+").length;
    }
}
