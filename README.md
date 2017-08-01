# platform-monitoring

KPI Validator Job: https://pcf-metrics.ci.cf-app.com/teams/main/pipelines/kpi-validator/jobs/validate-kpis
* Runs every morning at 6am Denver time (Mountain Time)
* Runs against latest cf-release. Keeping cf release the latest is automated (https://pcf-metrics.ci.cf-app.com/teams/main/pipelines/kpi-validator/jobs/cf-deployment)

Scalable Syslog Deployment
https://github.com/cloudfoundry/scalable-syslog
bosh -d scalablesyslog deploy manifests/scalable-syslog.yml -o manifests/cf-deployment-ops.yml --vars-file=/Users/pivotal/workspace/deployments-metrics/bosh-deployments/gcp/healthwatch/cf-deployment-vars.yml --vars-store=scalable-syslog-vars.yml