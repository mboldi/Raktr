package hu.bsstudio.raktr.support;

import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class RowCountAssert {

    private final int actualCount;

    public void isEqualTo(int expectedCount) {
        assertThat(actualCount).isEqualTo(expectedCount);
    }

    public void isEmpty() {
        assertThat(actualCount).isZero();
    }

}
