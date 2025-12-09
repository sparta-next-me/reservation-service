package org.nextme.reservation_service.reservation.domain;

public enum ReservationStatus {

    /** 결제 대기: 예약 요청은 접수되었으나 결제가 시작되지 않은 초기 상태. */
    PENDING_PAYMENT("결제 대기"),

    /** 결제 실패: 결제 시도가 실패한 상태. */
    PAYMENT_FAILED("결제 실패"),

    /** 확정 완료: 결제가 성공적으로 승인되어 예약이 최종 확정된 상태. */
    CONFIRMED("확정 완료"),

    /** 취소됨: 고객 또는 시스템에 의해 예약이 취소된 상태. */
    CANCELLED("취소됨"),

    /** 이용 완료: 예약된 서비스의 이용이 성공적으로 끝난 상태. */
    COMPLETED("이용 완료"),

    /** 노쇼/불참: 예약이 확정되었으나 고객이 나타나지 않은 상태. */
    NO_SHOW("노쇼"),

    /** 만료됨: 결제 유효 시간이 초과되어 자동 무효화된 상태. */
    EXPIRED("만료됨");

    private final String description;

    ReservationStatus(String description) {this.description = description;}

    public String getDescription() {
        return description;
    }
}
