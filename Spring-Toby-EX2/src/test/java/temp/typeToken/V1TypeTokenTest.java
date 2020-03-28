package temp.typeToken;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class V1TypeTokenTest {

    @Test
    void typeSafe() throws Exception{
        V1TypeToken.TypesafeMap typesafeMap = new V1TypeToken.TypesafeMap();
        typesafeMap.put(String.class, "First");
        assertThat(typesafeMap.get(String.class)).isEqualTo("First");

        typesafeMap.put(Integer.class, 1);
        assertThat(typesafeMap.get(Integer.class)).isEqualTo(1);


        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        typesafeMap.put(List.class, integers);

        List<String> strings = Arrays.asList("A", "B", "C", "D", "E");

    }

}