package ch5.step2;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */
public enum Level {
    BASIC(1), SILVER(2), GOLD(3);

    private final int value;

    Level(int value){
        this.value = value;
    }

    public int intValue(){
        return this.value;
    }

    // 값으로 부터 레벨 타입 오브젝트를 가져온다.
    public static Level valueOf(int value){
        switch (value) {
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default: throw new AssertionError("Unknown value: " + value);
        }
    }
}
