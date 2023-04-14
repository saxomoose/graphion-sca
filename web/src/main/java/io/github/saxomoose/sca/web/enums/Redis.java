package io.github.saxomoose.sca.web.enums;

public class Redis {
    public enum Queue {
        KEY("graphionscaweb:jobs");

        private final String key;

        Queue(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }
}
