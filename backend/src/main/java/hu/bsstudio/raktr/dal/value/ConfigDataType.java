package hu.bsstudio.raktr.dal.value;

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
                throw new IllegalArgumentException("Invalid BOOLEAN value: " + value);
            }
        }
    };

    public abstract void validate(String value);

}
