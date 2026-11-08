{{- define "food-delivery.labels" -}}
app.kubernetes.io/part-of: {{ .Chart.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end }}

{{- define "food-delivery.image" -}}
{{ .root.Values.image.registry }}/{{ .name }}:{{ .root.Values.image.tag }}
{{- end }}
