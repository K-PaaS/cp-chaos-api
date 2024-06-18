apiVersion: chaos-mesh.org/v1alpha1
kind: ${kind}
metadata:
  name: ${name}
  namespace: ${namespace}
spec:
  mode: all
  selector:
    namespaces:
    - ${namespaces}
    labelSelectors:
      ${labelSelectors}
    pods:
      ${namespaces}:
      - ${pods[i]}