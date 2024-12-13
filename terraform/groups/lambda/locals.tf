locals {
  vpc_id                        = data.aws_vpc.vpc.id
  file_transfer_api_kms_key_id  = data.aws_kms_key.kms_key.id
  lambda_vpc_access_subnet_ids  = concat(
    split(",", data.terraform_remote_state.network.outputs.application_ids),
    split(",", data.terraform_remote_state.network.outputs.routing_ids)
  )
}
