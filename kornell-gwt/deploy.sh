#!/bin/bash
echo Installing s3cmd
wget -O /etc/yum.repos.d/s3tools.repo http://s3tools.org/repo/RHEL_6/s3tools.repo
yum -y install s3cmd

echo Generating s3cmd config file
echo "
access_key = $(AWS_ACCESS_KEY_ID)
access_token = 
add_encoding_exts = 
add_headers = 
bucket_location = US
cache_file = 
cloudfront_host = cloudfront.amazonaws.com
default_mime_type = binary/octet-stream
delay_updates = False
delete_after = False
delete_after_fetch = False
delete_removed = False
dry_run = False
enable_multipart = True
encoding = UTF-8
encrypt = False
follow_symlinks = False
force = False
get_continue = False
gpg_command = None
gpg_decrypt = %(gpg_command)s -d --verbose --no-use-agent --batch --yes --passphrase-fd %(passphrase_fd)s -o %(output_file)s %(input_file)s
gpg_encrypt = %(gpg_command)s -c --verbose --no-use-agent --batch --yes --passphrase-fd %(passphrase_fd)s -o %(output_file)s %(input_file)s
gpg_passphrase = 
guess_mime_type = True
host_base = s3.amazonaws.com
host_bucket = %(bucket)s.s3.amazonaws.com
human_readable_sizes = False
invalidate_default_index_on_cf = False
invalidate_default_index_root_on_cf = True
invalidate_on_cf = False
list_md5 = False
log_target_prefix = 
mime_type = 
multipart_chunk_size_mb = 15
preserve_attrs = True
progress_meter = True
proxy_host = 
proxy_port = 0
recursive = False
recv_chunk = 4096
reduced_redundancy = False
secret_key = $(AWS_SECRET_KEY)
send_chunk = 4096
simpledb_host = sdb.amazonaws.com
skip_existing = False
socket_timeout = 300
urlencoding_mode = normal
use_https = True
verbosity = WARNING
website_endpoint = http://%(bucket)s.s3-website-%(location)s.amazonaws.com/
website_error = 
website_index = index.html
" > s3cfg

echo Syncing
s3cmd -c ./s3cfg  --delete-removed --exclude="WEB-INF/*" --recursive sync target/kornell-gwt-0.0.1-SNAPSHOT/  s3://eduvem.com.br 
