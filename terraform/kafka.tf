resource "helm_release" "kafka" {
    repository = "oci://registry-1.docker.io/bitnamicharts"
    name = "kafka"
    chart = "kafka"
    version = "30.0.0"
    namespace  = "kafka"
    create_namespace = true
}