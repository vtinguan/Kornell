DB_ENDPOINT=$(grep RDS_HOSTNAME /opt/elasticbeanstalk/deploy/configuration/containerconfiguration | cut -d\" -f4)
DB_IP=$(dig +short $DB_ENDPOINT | tail -1)

echo '#Generated on ' `date`
echo 127.0.0.1   localhost localhost.localdomain
echo $DB_IP db.kornell
