NEWRELIC=$(cat <<EOF
\ncommon: &default_settings
\n  license_key: $NEWRELIC_KEY
\n  # agent_enabled: true
\n  enable_auto_app_naming: false
\n  enable_auto_transaction_naming: false
\n  app_name: Eduvem
\n  log_level: info
\n  #audit_mode: true
\n  #log_file_count: 1
\n  #log_limit_in_kbytes: 0
\n  #log_daily: false
\n  #log_file_path:
\n  ssl: true
\n  # proxy_host: hostname
\n  # proxy_port: 8080
\n  # proxy_user: username
\n  # proxy_password: password
\n  capture_params: false
\n  # ignored_params: credit_card, ssn, password
\n
\n  transaction_tracer:
\n    enabled: true
\n    transaction_threshold: apdex_f
\n    record_sql: obfuscated
\n    #obfuscated_sql_fields: credit_card, ssn, password
\n    log_sql: false
\n
\n    stack_trace_threshold: 0.5
\n    explain_enabled: true
\n    explain_threshold: 0.5
\n    top_n: 20
\n
\n  error_collector:
\n    enabled: true
\n    # ignore_errors:
\n    # ignore_status_codes: 404
\n
\n  cross_application_tracer:
\n    enabled: true
\n
\n  thread_profiler:
\n    enabled: true
\n
\n  #============================== Browser Monitoring ===============================
\n  browser_monitoring:
\n    auto_instrument: true
\n    enabled: false
\n
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
EOF)
echo -e $NEWRELIC