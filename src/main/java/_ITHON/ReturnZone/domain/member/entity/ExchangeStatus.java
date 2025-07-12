package _ITHON.ReturnZone.domain.member.entity;

public enum ExchangeStatus {
    PENDING, // 요청 완료 (관리자 대기)
    APPROVED, // 승인 완료 (송금됨)
    REJECTED // 반려됨
}
