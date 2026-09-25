package hu.bsstudio.raktr.dal.value;

import hu.bsstudio.raktr.exception.InvalidValueException;

public enum ConfigDataType {

    STRING {
        @Override
        public void validate(String value) {
            // any string is valid
        }
    },

    BOOLEAN {
        @Override
        public void validate(String value) {
            if (!"true".equals(value) && !"false".equals(value)) {
                throw new InvalidValueException("Invalid BOOLEAN value: " + value);
            }
        }
    };

    public abstract void validate(String value);

}
