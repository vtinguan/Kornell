<!DOCTYPE html>
<html>
  <head>
  	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
  	<meta charset="utf-8">
  	<meta name="kornell.version" content="CD Check"> 

    <title>&nbsp;&nbsp;</title>
    <!-- before your module(*.nocache.js) loading  -->
	<!--[if lt IE 9]>
	<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<!--[if IE 7]>
 	<link rel="stylesheet" href="Kornell/css/font-awesome-ie7.css">
	<![endif]-->
	<!-- your module(*.nocache.js) loading -->  
	<script type="text/javascript">
		<%
		String apiEndpoint = "";
		if (System.getenv("PARAM1") != null)
			apiEndpoint = System.getenv("PARAM1");
		if (System.getProperty("PARAM1") != null)
			apiEndpoint = System.getProperty("PARAM1");  		
		%>
		var KornellConfig = {
			apiEndpoint:"<%= apiEndpoint %>"
		}; 
		
		function updateFavicon(url){
			var link = document.createElement('link'),
			oldLink = document.getElementById('icon');
			link.id = 'icon';
			link.rel = 'shortcut icon';
			link.type = 'image/x-icon';
			link.href = url;
			if (oldLink) {
			 	document.head.removeChild(oldLink);
			}
			document.getElementsByTagName('head')[0].appendChild(link);
		};
		
  	</script> 
    <script type="text/javascript" src="Kornell/Kornell.nocache.js"></script>
    <link id="Skin" type="text/css" rel="stylesheet" href="skins/first/css/skin.css"/>  
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
  </body>
</html>
