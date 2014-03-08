NEWRELIC=$(cat <<EOF
\ncommon: &default_settings
\n\040  license_key: $NEWRELIC_KEY
\n\040  # agent_enabled: true
\n\040  enable_auto_app_naming: false
\n\040  enable_auto_transaction_naming: false
\n\040  app_name: Eduvem
\n\040  log_level: info
\n\040  #audit_mode: true
\n\040  #log_file_count: 1
\n\040  #log_limit_in_kbytes: 0
\n\040  #log_daily: false
\n\040  #log_file_path:
\n\040  ssl: true
\n\040  # proxy_host: hostname
\n\040  # proxy_port: 8080
\n\040  # proxy_user: username
\n\040  # proxy_password: password
\n\040  capture_params: false
\n\040  # ignored_params: credit_card, ssn, password
\n\040
\n\040  transaction_tracer:
\n\040    enabled: true
\n\040    transaction_threshold: apdex_f
\n\040    record_sql: obfuscated
\n\040    #obfuscated_sql_fields: credit_card, ssn, password
\n\040    log_sql: false
\n\040
\n\040    stack_trace_threshold: 0.5
\n\040    explain_enabled: true
\n\040    explain_threshold: 0.5
\n\040    top_n: 20
\n\040
\n\040  error_collector:
\n\040    enabled: true
\n\040    # ignore_errors:
\n\040    # ignore_status_codes: 404
\n\040
\n\040  cross_application_tracer:
\n\040    enabled: true
\n\040
\n\040  thread_profiler:
\n\040    enabled: true
\n\040
\n\040  #============================== Browser Monitoring ===============================
\n\040  browser_monitoring:
\n\040    auto_instrument: true
\n\040    enabled: false
\n\040
\n# Application Environments
\n# ------------------------------------------
\ndevelopment:
\n  <<: *default_settings
\n  app_name: Eduvem (Development)
\n
\ntest:
\n  <<: *default_settings
\n  app_name: Eduvem (Test)
\n
\nproduction:
\n  <<: *default_settings
\n
\nstaging:
\n  <<: *default_settings
\n  app_name: Eduvem (Staging)
EOF
)
echo -e $NEWRELIC