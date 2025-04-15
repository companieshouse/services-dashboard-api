package uk.gov.companieshouse.servicesdashboardapi.lambda;

public class CronEvent {
    // AWS wraps event's data in a "detail" field
    // which contains the actual event data
    // Until we don't need a complex data structure
    // we can just use a simple class to represent the event
    // and avoid a custom deserializer
    private Detail detail;

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public static class Detail {
        private String action;
        private boolean deepscan = false; // Default value is false

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public boolean isDeepscan() {
            return deepscan;
        }

        public void setDeepscan(boolean deepscan) {
            this.deepscan = deepscan;
        }
    }
}