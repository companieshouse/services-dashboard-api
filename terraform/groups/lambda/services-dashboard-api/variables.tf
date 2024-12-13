variable "aws_account" {
  type        = string
  description = "The AWS account name"
}

variable "aws_account_id" {
  type        = string
  description = "The AWS account identifier"
}

variable "aws_profile" {
  type        = string
  description = "The AWS profile name; used as a prefix for Vault secrets"
}

variable "region" {
  type        = string
  description = "The AWS region in which resources will be created"
}

variable "environment" {
  type        = string
  description = "The environment name to be used when creating AWS resources"
}

variable "service" {
  type        = string
  description = "The service name to be used when creating AWS resources"
}

variable "file_transfer_api_kms_key_id" {
  type        = string
  description = "The ID of the customer-managed key in KMS used by the file transfer API service for S3 bucket encryption"
}

variable "image_bucket_name" {
  type        = string
  description = "The name of the S3 bucket that will store processed images"
}

variable "image_bucket_prefix" {
  type        = string
  description = "The prefix for objects stored in the S3 image bucket; must begin with a leading forward slash and end without a trailing forward slash"
}

variable "lambda_event_source_batch_size" {
  type        = number
  description = "The largest number of records that Lambda will retrieve from the SQS event source at the time of invocation"
}

variable "lambda_handler_name" {
  type        = string
  description = "The lambda function entrypoint"
}

variable "lambda_logs_retention_days" {
  type        = number
  description = "The number of days to retain Lambda logs in CloudWatch"
}

variable "lambda_memory_size" {
  type        = string
  description = "The amount of memory made available to the Lambda function at runtime in megabytes"
}

variable "lambda_timeout_seconds" {
  type        = string
  description = "The amount of time the lambda function is allowed to run before being stopped"
}

variable "lambda_runtime" {
  type        = string
  description = "The lambda runtime to use for the function"
}

variable "lambda_vpc_access_subnet_ids" {
  type        = list(string)
  description = "A list of subnet identifiers the Lambda function will be able to access resources in"
}

variable "release_bucket_name" {
  type        = string
  description = "The name of the S3 bucket containing the release artefact for the Lambda function"
}

variable "release_artifact_key" {
  type        = string
  description = "The release artifact key for the Lambda function"
}

variable "data_queue_max_receive_count" {
  type        = number
  description = "The maximum number of times a message may be received before being moved to the dead-letter queue"
}

variable "data_queue_visibility_timeout_seconds" {
  type        = number
  description = "The visibility timeout for the queue in seconds"
}

variable "vpc_id" {
  type        = string
  description = "The VPC in which to create resources"
}

variable "message_retention_seconds" {
  type        = number
  description = "The number of seconds Amazon SQS retains a message"
}
