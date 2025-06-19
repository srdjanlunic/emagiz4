package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NvdCve {
    private Cve cve;

    public Cve getCve() {
        return cve;
    }

    public void setCve(Cve cve) {
        this.cve = cve;
    }

    public static class Cve {
        private String id;
        private String published;
        private String lastModified;
        private List<Description> descriptions;
        private Metrics metrics;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPublished() {
            return published;
        }

        public void setPublished(String published) {
            this.published = published;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }

        public List<Description> getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(List<Description> descriptions) {
            this.descriptions = descriptions;
        }

        public Metrics getMetrics() {
            return metrics;
        }

        public void setMetrics(Metrics metrics) {
            this.metrics = metrics;
        }
    }

    public static class Description {
        private String lang;
        private String value;

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Metrics {
        @SerializedName("cvssMetricV2")
        private List<CvssMetricV2> cvssMetricV2;

        public List<CvssMetricV2> getCvssMetricV2() {
            return cvssMetricV2;
        }

        public void setCvssMetricV2(List<CvssMetricV2> cvssMetricV2) {
            this.cvssMetricV2 = cvssMetricV2;
        }
    }

    public static class CvssMetricV2 {
        private String baseSeverity;

        public String getBaseSeverity() {
            return baseSeverity;
        }

        public void setBaseSeverity(String baseSeverity) {
            this.baseSeverity = baseSeverity;
        }
    }
} 