package ch4;

import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/23
 */
public class Product {
    @NumberFormat(pattern = "$###,##0.00")
    BigDecimal price;

}
