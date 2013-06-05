<!DOCTYPE html>
<html>
  <head>
  	<meta http-equiv="content-type" content="text/html; charset=UTF-8">

  	<!-- 
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="viewport" content="width=device-width, user-scalable=no">
     -->

    <title>Kornell</title>
    <!-- before your module(*.nocache.js) loading  -->
	<!--[if lt IE 9]>
	<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<!--[if IE 7]>
 	<link rel="stylesheet" href="Kornell/css/font-awesome-ie7.css">
	<![endif]-->
	<!-- your module(*.nocache.js) loading -->  
    <script type="text/javascript" src="Kornell/Kornell.nocache.js"></script>
    <script type="text/javascript">
		var KornellConfig = {
			apiEndpoint:"<%= System.getenv("PARAM1") != null ? System.getenv("PARAM1") : "" %>"
		};  				
  	</script>
	<link id="KornellStyle" type="text/css" rel="stylesheet" href="Kornell.css"/>
  </head>

  <body>
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>
    <link id="Skin" type="text/css" rel="stylesheet" href="skins/first/skin.css"/>  
  </body>
</html>