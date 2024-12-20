locals {

  service_name         = "services-dashboard-api"
  lambda_function_name = "${local.service_name}-${var.environment}"
  ssm_prefix           = "/${local.lambda_function_name}"

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
    "${k}.secret" => v
    # "${k}.secret" => sensitive(v)
  }

  # Generate a map of secrets that are 'not sensitive' (as otherwise I cannot use this map
  # because Terraform does not allow sensitive values to be used in a "for_each" expression)
  ssm_secrets_nonsensitive = {
    for k, v in local.ssm_secrets :
    k => (can(nonsensitive(v)) ? nonsensitive(v) : v)
  }
  ssm_secrets_keys = {
    for k in keys(local.ssm_secrets_nonsensitive) :
    k => (can(nonsensitive(k)) ? nonsensitive(k) : k)
  }
  # MONGO SETTINGS
  mongo_protocol = "mongodb+srv"
  mongo_dbname   = "services_dashboard"

  # DEPENDENCY-TRACK SETTINGS
  dt_server_baseurl = "https://dependency-track.companieshouse.gov.uk"

}
