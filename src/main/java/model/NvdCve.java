package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents a CVE record from the National Vulnerability Database.
 */
public class NvdCve {
    
    /** Main CVE object */
    private Cve cve;
    
    /**
     * Gets the CVE data.
     * @return the CVE object
     */
    public Cve getCve() {
        return cve;
    }
    
    /**
     * Sets the CVE data.
     * @param cve the CVE object to set
     */
    public void setCve(Cve cve) {
        this.cve = cve;
    }
    
    /**
     * Contains detailed CVE information.
     */
    public static class Cve {
        private String id;
        private String published;
        private String lastModified;
        private List<Description> descriptions;
        private Metrics metrics;
        
        /**
         * @return CVE ID (e.g. CVE-2025-12345)
         */
        public String getId() {
            return id;
        }
        
        /**
         * @param id CVE ID to set
         */
        public void setId(String id) {
            this.id = id;
        }
        
        /**
         * @return Published date
         */
        public String getPublished() {
            return published;
        }
        
        /**
         * @param published the published date to set
         */
        public void setPublished(String published) {
            this.published = published;
        }
        
        /**
         * @return Last modified date
         */
        public String getLastModified() {
            return lastModified;
        }
        
        /**
         * @param lastModified last modified date to set
         */
        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }
        
        /**
         * @return List of descriptions
         */
        public List<Description> getDescriptions() {
            return descriptions;
        }
        
        /**
         * @param descriptions list of descriptions to set
         */
        public void setDescriptions(List<Description> descriptions) {
            this.descriptions = descriptions;
        }
        
        /**
         * @return Metrics object containing CVSS data
         */
        public Metrics getMetrics() {
            return metrics;
        }
        
        /**
         * @param metrics metrics object to set
         */
        public void setMetrics(Metrics metrics) {
            this.metrics = metrics;
        }
    }
    
    /**
     * Represents a description of the CVE.
     */
    public static class Description {
        private String lang;
        private String value;
        
        /**
         * @return Language code (e.g. "en")
         */
        public String getLang() {
            return lang;
        }
        
        /**
         * @param lang language code to set
         */
        public void setLang(String lang) {
            this.lang = lang;
        }
        
        /**
         * @return Description text
         */
        public String getValue() {
            return value;
        }
        
        /**
         * @param value description text to set
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
    
    /**
     * Holds CVSS metrics.
     */
    public static class Metrics {
        @SerializedName("cvssMetricV2")
        private List<CvssMetricV2> cvssMetricV2;
        
        /**
         * @return List of CVSSv2 metrics
         */
        public List<CvssMetricV2> getCvssMetricV2() {
            return cvssMetricV2;
        }
        
        /**
         * @param cvssMetricV2 CVSSv2 metrics to set
         */
        public void setCvssMetricV2(List<CvssMetricV2> cvssMetricV2) {
            this.cvssMetricV2 = cvssMetricV2;
        }
    }
    
    /**
     * Represents CVSS v2 severity data.
     */
    public static class CvssMetricV2 {
        private String baseSeverity;
        
        /**
         * @return Severity level (e.g. "LOW", "MEDIUM", "HIGH")
         */
        public String getBaseSeverity() {
            return baseSeverity;
        }
        
        /**
         * @param baseSeverity severity level to set
         */
        public void setBaseSeverity(String baseSeverity) {
            this.baseSeverity = baseSeverity;
        }
    }
}
