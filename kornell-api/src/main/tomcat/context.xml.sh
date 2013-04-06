CONTEXT=$( cat <<EOF
<Context>
	<Resource name="jdbc/KornellDS" 
		auth="Container" 
	    type="javax.sql.DataSource"
		maxActive="100"  
		maxIdle="10" 
		maxWait="10000" 
		username="$USERNAME"
		password="kornell" 
		driverClassName="com.mysql.jdbc.Driver"
		url="jdbc:mysql://db.kornell:3306/kornell" 
		
		validationQuery="select 42"
		validationQueryTimeout="2"
		testOnBorrow="true"
		testOnReturn="false"
		/>
	
			
	
	<Realm className="org.apache.catalina.realm.DataSourceRealm"
		dataSourceName="jdbc/KornellDS"
		localDataSource="true"
   		userTable="Password" 
   		userNameCol="username"
   		userCredCol="password"
   		userRoleTable="Role" 
   		roleNameCol="role"/>
	
</Context>
EOF
)
echo $CONTEXT
