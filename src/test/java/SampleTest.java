import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {
    @Test
    void sampleTest() {
        assertTrue(true);
    }
    @Test
    void testEquals() {
        String str1 = "JUnit";
        String str2 = "JUnit";
        assertEquals(str1, str2, "Strings should be equal");
    }

    @Test
    void testNotNull() {
        String str = "JUnit Test";
        assertNotNull(str, "String should not be null");
    }

    @Test
    void testNull() {
        String str = null;
        assertNull(str, "String should be null");
    }

    @Test
    void testArrayEquals() {
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};
        assertArrayEquals(expected, actual, "Arrays should be equal");
    }

    @Test
    void testThrowsException() {
        assertThrows(ArithmeticException.class, () -> {
            int result = 1 / 0; // This will throw ArithmeticException
        }, "Division by zero should throw ArithmeticException");
    }

    @Test
    void testNotEqual() {
        int x = 5;
        int y = 10;
        assertNotEquals(x, y, "Values should not be equal");
    }

    @Test
    void testTrueCondition() {
        boolean condition = (2 + 2 == 4);
        assertTrue(condition, "Condition should be true");
    }

    @Test
    void testFalseCondition() {
        boolean condition = (2 + 2 == 5);
        assertFalse(condition, "Condition should be false");
    }

    @Test
    void testGreaterThan() {
        int value = 5;
        assertTrue(value > 0, "Value should be greater than 0");
    }

    @Test
    void testLessThan() {
        int value = -1;
        assertTrue(value < 0, "Value should be less than 0");
    }
}
