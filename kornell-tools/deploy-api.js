console.log('Deploying Kornell API');

var version = 'kornell-api-'
		+ new Date().toISOString().replace(/T/, '_').replace(/\:/g, '_')
				.replace(/\..+/, '')
var bucket = "kornell-artifacts";
var key = 'kornell-api/' + version + '.war';
var fileName = 'kornell-api/kornell-api.war';
var application = 'eduvem-api'
var environment = 'eduvem-api-test-web';

var versionsKept = 10;
console.log('version=' + version);

var fs = require('fs');
var AWS = require('aws-sdk');
AWS.config.update({
	region : 'sa-east-1'
});

var EB = new AWS.ElasticBeanstalk({
	apiVersion : '2010-12-01'
});
var S3 = new AWS.S3({
	apiVersion : '2006-03-01'
});

function knot(err, data) {
	if (err)
		throw err
	console.log("! Happy Ending !");
}

function removeOldVersions(err, data) {
	console.log("* Removing up old versions")
	if (err)
		throw err
	var versions = data.ApplicationVersions
	for (var i = 0; i < versions.length; i++) {
		var verLabel = versions[i].VersionLabel;
		if (i <= versionsKept)
			console.log("Version #" + i + " " + verLabel + " kept.");
		else
			EB.deleteApplicationVersion({
				ApplicationName : application,
				VersionLabel : verLabel
			}, function(err, data) {
				console.log("Version #" + i + " " + verLabel + " removed.");
			});

	}
	setTimeout(function(){whenReady(err, data, knot)},10000);
}

function lookupOldVersions(err, data) {
	console.log("* Looking up old versions")
	if (err)
		throw err
	EB.describeApplicationVersions({
		ApplicationName : application
	}, removeOldVersions)
}

function gcVersions(err, data) {
	whenReady(err, data, lookupOldVersions)
}

function updateEnvironment(err, data) {
	console.log("* Updating Environment")
	if (err)
		throw err
	EB.updateEnvironment({
		EnvironmentName : environment,
		VersionLabel : version
	}, gcVersions);
}

function whenReady(err, data, fun) {
	console.log("* Waiting for [" + environment + "] to be Ready ")
	if (err)
		throw err
	EB.describeEnvironments({
		EnvironmentNames : [ environment ]
	}, function(err, data) {
		var envs = data.Environments
		if (envs.length > 0) {
			var status = envs[0].Status
			console.log("* Environment status is " + status);
			if (status == 'Ready')
				fun(err, data)
			else
				setTimeout(function() {
					whenReady(err, data, fun);
				}, 10000);
		}
	})
}

function createVersion(err, data) {
	console.log("* Creating Version")
	if (err)
		throw err
	EB.createApplicationVersion({
		ApplicationName : application,
		VersionLabel : version,
		SourceBundle : {
			S3Bucket : bucket,
			S3Key : key
		}
	}, function(err, data) {
		console.log("* Version created")
		whenReady(err, data, updateEnvironment);
	})
}

function uploadBundle(err, data) {
	console.log("* Uploading Bundle")
	if (err)
		throw err
	S3.putObject({
		Key : key,
		Bucket : bucket,
		Body : data
	}, createVersion);
}

function readLocalFile() {
	console.log("* Reading Local File")
	fs.readFile(fileName, uploadBundle);
}

var main = readLocalFile;

main();
