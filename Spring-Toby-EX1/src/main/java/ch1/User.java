package ch1;

import lombok.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/27
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private String id;
    private String name;
    private String password;

    /** 자바빈
     *  - 원래는 비줄얼 툴에서 조작가능한 컴포넌트를 말한다.
     *  - 웹 기반으로 자바의 주력이 변경되면서 디폴트 생성자와, 프로퍼티의 getter, setter를 노출하는 의미로 변경되었다.
     */
}
