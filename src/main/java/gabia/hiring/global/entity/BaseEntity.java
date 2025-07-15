//package gabia.hiring.global.entity;
//
//
//import jakarta.persistence.Column;
//import jakarta.persistence.EntityListeners;
//import jakarta.persistence.MappedSuperclass;
//import lombok.Getter;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import java.time.LocalDateTime;
//
///**
// * @MappedSuperclass 사용:
// * @Entity가 아닌 @MappedSuperclass 사용
// * 테이블이 생성되지 않고 상속받는 엔티티에만 컬럼 추가
// */
//@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
//@Getter
//public abstract class BaseEntity {
//
//    @CreatedDate
//    @Column(updatable = false)
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    private LocalDateTime updatedAt;
//}