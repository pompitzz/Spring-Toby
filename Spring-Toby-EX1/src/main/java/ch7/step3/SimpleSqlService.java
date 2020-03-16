package ch7.step3;

import lombok.Setter;

import java.util.Map;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/16
 */
@Setter
public class SimpleSqlService implements SqlService {
    private Map<String, String> sqlMap;

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if (sql == null){
            throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
        }
        return sql;
    }
}
