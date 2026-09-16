package uk.gov.companieshouse.servicesdashboardapi.model.deptrack;

import uk.gov.companieshouse.servicesdashboardapi.model.common.BaseMetricsInfo;

public class DepTrackMetricsInfo extends BaseMetricsInfo {

    @Override
    public String toString() {
        return String.format("{C:%s/H:%s/M:%s/L:%s/v:%s/c:%s/pT:%s/pW:%s/pF:%s}", critical, high, medium, low, vulnerabilities, components, policyViolationsTotal, policyViolationsWarn, policyViolationsFail);
    }
}


