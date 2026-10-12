package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import uk.gov.companieshouse.servicesdashboardapi.model.common.BaseMetricsInfo;

public class MongoMetricsInfo extends BaseMetricsInfo {

    @Override
    public String toString() {
        return String.format("critical:%s high:%s medium:%s low:%s vulnerabilities:%s policyViolationsTotal:%s policyViolationsWarn:%s policyViolationsFail:%s",
                critical, high, medium, low, vulnerabilities, policyViolationsTotal, policyViolationsWarn, policyViolationsFail);
    }
}


