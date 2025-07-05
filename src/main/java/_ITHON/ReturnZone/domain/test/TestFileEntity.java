package _ITHON.ReturnZone.domain.test; // 패키지 변경

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TestFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgText;

    private String imgUrl;

    public TestFileEntity(String imgText, String imgUrl) {
        this.imgText = imgText;
        this.imgUrl = imgUrl;
    }
}