package temp.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempService2 {
    private final TempProtoModel tempProtoModel;

    public void print() {
        System.out.println(tempProtoModel);
    }
}
