var fs = require('fs'); 
var AWS = require('aws-sdk');

// Environment Variables
var warFile = process.env.KNL_API_WAR || "kornell-api/kornell-api.war";
var stackId = process.env.KNL_STACK_ID || (throw new "Stack ID is required");
var appId = process.env.KNL_APP_ID || (throw new "Application ID is required");

AWS.config.update({
	region : 'sa-east-1',
	logger : process.stdout
});

var warBucket = "dist-sa-east-1.craftware.com";
var warKey = "Kornell/kornell-api.war"
var skipUpload = ("true" == process.env.KNL_SKIP_UPLOAD)
var s3 = new AWS.S3();
var opsworks = new AWS.OpsWorks({region: 'us-east-1'});

function done(){
	console.info("done!");
	process.exit(0);
}

function fireDeploy(data){
	console.log('Deploying new version');
	var params = {
		Command: {
		  Name: 'deploy'		  
		},
		StackId: stackId,
		AppId: appId
	};
	opsworks.createDeployment(params,cb(done));
}

function withWarFile(data) {
	if (!skipUpload) {
		console.log('Uploading new version to [s3://' + warBucket + "/"
				+ warKey + ']');
		s3.putObject({
			Bucket : warBucket,
			Key : warKey,
			Body : data
		}, fireDeploy);
	} else
		fireDeploy();

}

function uploadNewVersion() {
	console.log('Reading local war file [' + warFile + ']');
	fs.readFile(warFile, cb(withWarFile))
}

var main = function() {
	console.log('Deploying Kornell API to Wildfly');
	uploadNewVersion();
}

function cb(fn) {
    return function() {
    	var args = Array.prototype.slice.call(arguments);    	
    	if (args.length < 1) suicide("Callback must have at least one param")
    	var err = args.slice(0,1)[0]
    	var data = args.slice(1)
        if (err) suicide(err.message)
        return fn.apply(this, data);
    }
}

function suicide(msg){
	console.error("ERROR: "+msg)
	process.exit(1); 
}


main();
