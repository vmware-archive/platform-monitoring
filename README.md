# platform-monitoring

KPI Validator Job: https://concourse.cf-denver.com/teams/healthwatch/pipelines/healthwatch-v1.6/jobs/validate-kpis
* Runs every morning at 6am Denver time (Mountain Time)
* Runs against latest cf-release. Keeping cf release the latest is automated (https://concourse.cf-denver.com/teams/envs/pipelines/pave-moonstorm)
* Checks all metrics in kpis.text
    * The number next to the metric corresponds to the number of seconds between emissions.
    * When the number next to the metric name is `-1` the metric is probably event-driven, so we are not checking a frequency of emission.
    * The KPI validator runs for 5 minutes, so if kpis.txt is expecting it to be emitted every 30 seconds, there will be a total of 10 emissions.
