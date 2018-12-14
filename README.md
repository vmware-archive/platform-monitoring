# platform-monitoring

KPI Validator Job: https://pcf-metrics.ci.cf-app.com/teams/main/pipelines/kpi-validator/jobs/validate-kpis
* Runs every morning at 6am Denver time (Mountain Time)
* Runs against latest cf-release. Keeping cf release the latest is automated (https://pcf-metrics.ci.cf-app.com/teams/main/pipelines/kpi-validator/jobs/cf-deployment)
* Checks all metrics in kpis.text
    * The number next to the metric corresponds to the number of seconds between emissions.
    * When the number next to the metric name is `-1` the metric is probably event-driven, so we are not checking a frequency of emission.
    * The KPI validator runs for 5 minutes, so if kpis.txt is expecting it to be emitted every 30 seconds, there will be a total of 10 emissions.

Scalable Syslog Deployment
https://github.com/cloudfoundry/scalable-syslog
bosh -d scalablesyslog deploy manifests/scalable-syslog.yml -o manifests/cf-deployment-ops.yml --vars-file=/Users/pivotal/workspace/deployments-metrics/bosh-deployments/gcp/healthwatch/cf-deployment-vars.yml --vars-store=scalable-syslog-vars.yml