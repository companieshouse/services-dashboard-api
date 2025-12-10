locals {

  service_name         = "services-dashboard-api"
  stack_name           = "rand-pocs-stack"
  lambda_function_name = "${local.service_name}"
  kms_alias            = "alias/aws/ssm"
  json_folder          = "input_json/${var.aws_profile}"

  vpc_name                     = local.stack_secrets["vpc_name"]
  lambda_vpc_access_subnet_ids = data.aws_subnets.application.ids
  application_subnet_ids       = data.aws_subnets.application.ids
  application_subnet_pattern   = local.stack_secrets["application_subnet_pattern"]

  additional_iam_policies_json = [data.aws_iam_policy_document.ssm_access_policy.json]

  lambda_cloudwatch_event_rules = [
    {
      name                = "${local.lambda_function_name}-lightscan"
      description         = "Trigger Lambda often for light scans"
      schedule_expression = "cron(45 6-17 ? * MON-FRI *)"
      target_input        = data.local_file.lightscan.content
    },
    {
      name                = "${local.lambda_function_name}-deepscan"
      description         = "Trigger Lambda seldom for deep scans"
      schedule_expression = "cron(15 7 ? 1/1 TUE#1 *)"
      target_input        = data.local_file.deepscan.content
    },
    {
      name                = "${local.lambda_function_name}-load-endol"
      description         = "Trigger Lambda daily for End of Life scans"
      schedule_expression = "cron(0 7 ? * MON-FRI *)"
      target_input        = data.local_file.endolscan.content
    }
  ]

  # Secrets
  stack_secrets        = data.vault_generic_secret.stack_secrets.data
  stack_secrets_path   = "applications/${var.aws_profile}/${var.environment}/${local.stack_name}"
  service_secrets      = data.vault_generic_secret.service_secrets.data
  service_secrets_path = "${local.stack_secrets_path}/services-dashboard"

  # DEPENDENCY-TRACK SETTINGS
  dt_server_baseurl = "https://dependency-track.companieshouse.gov.uk"
}
