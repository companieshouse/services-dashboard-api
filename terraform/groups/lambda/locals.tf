locals {

  docker_repo          = "services-dashboard-api"
  service_name         = local.docker_repo
  lambda_function_name = "${local.service_name}-${var.environment}"
  ssm_prefix           = "/${lambda_function_name}"

  # Secrets
  stack_secrets   = jsondecode(data.vault_generic_secret.stack_secrets.data_json)
  service_secrets = jsondecode(data.vault_generic_secret.service_secrets.data_json)

  # Create a single map of secrets merging "stack" & "service" secrets.
  # Note: in case a secret with the same name is present in "stack" and "service"
  # the "service"'s value will overwrite the "stack"'s value.
  # This allows the normal practice of having (global) secrets as defaults
  # which can be overwritten by service specific needs
  vault_secrets = merge(local.stack_secrets, local.service_secrets)

  # Generate SSM secret names from vault names & appending ".secret" suffix
  ssm_secrets = {
    for k, v in local.vault_secrets :
    k + ".secret" => v
  }

  # MONGO SETTINGS
  mongo_protocol = "mongodb+srv"
  mongo_dbname   = "services_dashboard"

  # DEPENDENCY-TRACK SETTINGS
  dt_server_baseurl = "https://dependency-track.companieshouse.gov.uk"

}
