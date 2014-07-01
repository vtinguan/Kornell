var fs = require('fs');
var AWS = require('aws-sdk');

// Environment Variables
var warFile = process.env.KNL_API_WAR || "kornell-api/kornell-api.war";
var asgName = process.env.KNL_ASG_NAME || "kornell-api-test-asg";
var skipUpload = (process.env.KNL_SKIP_UP == "true");
var waitTimeout = parseInt(process.env.KNL_WAIT_TIMEOUT) || 300;
var waitStep = parseInt(process.env.KNL_WAIT_STEP) || 20;

AWS.config.update({
	region : 'sa-east-1',
	logger : process.stdout
});

var warBucket = "dist-sa-east-1.craftware.com";
var warKey = "Kornell/kornell-api.war"
var asgUpPolicy = "double"
var asgDownPolicy = "half"

var s3 = new AWS.S3();
var asg = new AWS.AutoScaling();
var elb = new AWS.ELB();

function knot() {
	console.log("Finishing...")
	setTimeout(function() {
		console.log('Bye!');
		process.exit();
	}, 1000);
}

function wait(msg, acc, callback) {
	console.log("Waiting [" + acc + "/" + waitTimeout + "s] for " + msg)
	if (acc >= waitTimeout)
		callback();
	else
		setTimeout(function() {
			wait(msg, acc + waitStep, callback);
		}, waitStep * 1000);
}

function scaleDown() {
	console.log("Scaling down ASG ["+asgDownPolicy+"@"+asgName+"]");
	asg.executePolicy({
		PolicyName : asgDownPolicy,
		AutoScalingGroupName : asgName,
	}, knot);
}

function waitScaleUp(err, data) {
	if (err) throw err;
	wait("scale up", 0, scaleDown);
}

function scaleUp(err,data) {
	if(err) throw err;
	console.log("Scaling up ASG ["+asgUpPolicy+"@"+asgName+"]");
	asg.executePolicy({
		PolicyName : asgUpPolicy,
		AutoScalingGroupName : asgName,
	}, waitScaleUp);
}

function withWarFile(err, data) {
	if (err)
		throw err;
	if (!skipUpload) {
		console.log('Uploading new version to [s3://' + warBucket + "/"
				+ warKey + ']');
		s3.putObject({
			Bucket : warBucket,
			Key : warKey,
			Body : data
		}, scaleUp);
	} else
		scaleUp();

}

function uploadNewVersion() {
	console.log('Reading local war file [' + warFile + ']');
	fs.readFile(warFile, withWarFile)
}

var main = function() {
	console.log('Deploying Kornell API to Wildfly');
	uploadNewVersion();
}

main();
