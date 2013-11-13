/*
============================================================================
Licensed Materials - Property of IBM
 
Copyright IBM Corp. 2013 All Rights Reserved.
 
US Government Users Restricted Rights - Use, duplication or
disclosure restricted by GSA ADP Schedule Contract with
IBM Corp.
============================================================================
*/


{
		function changeTut(linkId) {
			var pane = document.getElementById(linkId);
			var tutorials = {helloworldLink : "WebSocket/Tutorial31_Start.html",
					         compwebLink : "WebSocket/Tutorial31_CompleteWebPage.html",
					         connectLink : "WebSocket/Tutorial31_Connecting.html",
			                 msgLink : "WebSocket/Tutorial31_Messages.html",
			                 subscribeLink :"WebSocket/Tutorial31_Subscribe.html",
			                 diagnosticsLink :"WebSocket/Tutorial31_Diagnostics.html",
			                 highAvailabilityLink :"WebSocket/Tutorial31_HighAvailability.html"
					       };

			for (key in tutorials)
				document.getElementById(key).style.fontWeight='normal';
				
			pane.style.fontWeight='bold';
			changeTutPane(tutorials[linkId]);
		}
		
		function changeTutPane(filename){
			document.getElementById("tutPane").innerHTML = "";
			var html = "<iframe src='" + filename + "' frameborder=0 scrolling=no height=900px width=95%></iframe>";
			document.getElementById("tutPane").innerHTML = html;
		}
		
		function changeTab(id){
			var liArray = new Array();
			liArray[0] = "int";
			liArray[1] = "uti";
			liArray[2] = "ref";
			liArray[3] = "tut";
			liArray[4] = "use";
			
			for(var i = 0; i < liArray.length; i++){
				document.getElementById(liArray[i]).setAttribute("class", "");
			}
			document.getElementById(id).setAttribute("class", "selected");
			changeContent(id);
		}
		
		function changeContent(id){
			var content = document.getElementById("content");
			content.innerHTML = "";
			
			var newContent = "";
			document.getElementById("tutNavBar").style.visibility='hidden';
			document.getElementById("tutPane").innerHTML="";
			switch(id){
				case 'int':	newContent = grabIntContent();
					break;
				case 'uti': newContent = grabUtiContent();
				   break;
				case 'ref': newContent = grabRefContent();
					break;
				case 'tut':	document.getElementById("tutNavBar").style.visibility='visible'; 
						changeTut('helloworldLink');
					break;
				case 'use': newContent = grabUseContent();
					break;
				default:
					break;
			}
			content.innerHTML = newContent;
		}
		
		function grabIntContent() {
			return 'Welcome to the IBM messaging utility sample. This sample is provided to help you explore, understand and utilize the JavaScript  libraries for  Mobile and M2M Messaging with WebSphere MQ.'
            +' Here we provide the information you need to get started creating your own mobile messaging applications. This utility includes links to the IBM messaging community, which contains a series of articles to help you create your own apps as well as a web client tutorial so you can get hands on quickly and see messaging code in action.'
			+' Then once you have explored the tutorial, have a look at the IBM WebSphere web client utility itself, which will help you test your own messaging applications.'
			+' This sample is provided in good faith and AS-IS.' 
						
			      +'<br>'	
			      +'<div id="utilities" class="container">'
			      +'<div>'
			      +'<img class="imgContainer" src="PlayButton.gif" height="70px" width="70px" alt="Getting started image" />'
			      +'</div>'
			      +'<div class="textContainer" style="float:left;">'
			      +'<p>Utilities<br/>'
			      +'Tools to try out Web Messaging</p>'
			      +'</div>'
			      +'<div class="linkContainer">'
			      +'<span class="link" onClick="changeTab(\'uti\');">Web Messaging Utility</span>'
			      +'</div>'
			      +'</div>'
	
			      +'<div id="apiDoc" class="container">'
			      +'<div>'
			      +'<img class="imgContainer" src="EnterperiseMessagingButton.gif" height="70px" width="70px" alt="API Image" />'
			      +'</div>'
			      +'<div class="textContainer" style="float:left;">'
			      +'<p>Application Programming Interface documentation</p>'
			      +'</div>'
			      +'<div class="linkContainer">'
			      +'<a href="WebSocket/index.html">JavaScript</a><br />'
			      +'<a href="clients/java/doc/javadoc/index.html">Java</a><br />'
			      +'<a href="clients/c/doc/html/index.html">C</a><br />'
			      +'<a href="MQServer/javadoc/index.html">MQServer API Documentation</a>'
			      +'</div>'
			      +'</div>'
	
			      +'<div id="gettingstarted" class="container">'
			      +'<div>'
			      +'<img class="imgContainer" src="PlayButton.gif" height="70px" width="70px" alt="Getting started image" />'
			      +'</div>'
			      +'<div class="textContainer" style="float:left;">'
			      +'<p>Getting started<br />'
			      +'To get started, try our Web Messaging tutorial</p>'
			      +'</div>'
			      +'<div class="linkContainer">'
			      +'<span class="link" onClick="changeTab(\'tut\');">Web Messaging Tutorial</span>'
			      +'</div>'
			      +'</div>'
	
			      +'<div id="useful" class="container">'
			      +'<div>'
			      +'<img class="imgContainer" src="CommunityButton.gif" height="70px" width="70px" alt="useful links Image" />'
			      +'</div>'
			      +'<div class="textContainer" style="float:left;">'
			      +'<p>Useful links</p>'
			      +'</div>'
			      +'<div class="linkContainer">'
			      +'<a href="https://www.ibm.com/developerworks/mydeveloperworks/blogs/c565c720-fe84-4f63-873f-607d87787327/entry/test1?lang=en">IBM Messaging Community</a><br />'
			      +'<a href="http://www-01.ibm.com/software/integration/wmq/library/index.html">WebSphere MQ InfoCenter</a><br />'
			      +'</div>'
			      +'</div>';
		}
		function grabUtiContent() {
			return '<iframe src="WebSocket/WebMessagingUtility.html" frameborder=0 scrolling=no height=900px width=100%></iframe>';
		}
		function grabRefContent() {
		    return '<iframe src="References.html" frameborder=0 scrolling=no height=900px width=100%></iframe>';
		}
		function grabUseContent() { 
			return '<div style="margin-left:1.5em;">'
			      +'<p>Other resources on the Web.</p>'
			      +'</div>'
			  
			      +'<div id="msgCommunity" class="container">' 
	              +'<div> <img class="imgContainer" src="CommunityButton.gif" height="70px" width="70px" alt="IBM Messaging Community icon image" /> </div>'
			      +'<div class="textContainer"> <p><a href="https://www.ibm.com/developerworks/mydeveloperworks/blogs/c565c720-fe84-4f63-873f-607d87787327/entry/test1?lang=en"> IBM Messaging Community</a></p> </div>' 
			      +'<div class="textContainer"> <p><a href="https://www.ibm.com/developerworks/mydeveloperworks/blogs/c565c720-fe84-4f63-873f-607d87787327/entry/mobile_messaging?lang=en"> Mobile Messaging</a></p> </div>'
			      +'<div class="textContainer"> <p><a href="https://www.ibm.com/developerworks/mydeveloperworks/blogs/c565c720-fe84-4f63-873f-607d87787327/entry/participate19?lang=en"> Join in on the conversation</a></p> </div>'
			      +'</div>' 
			 
			      +'<div id="infocenterLibrary" class="container">' 
			      +'<div> <img class="imgContainer" src="websphere-brandmark.gif" alt="WebSphere icon image" /> </div>' 
			      +'<div class="textContainer"> <p><a href="http://www-01.ibm.com/software/integration/wmq/library/index.html"> WebSphere MQ infocenter library</a></p></div>' 
			      +'</div>' 
			  
			      +'<div id="mqttSepecification" class="container">'
			      +'<div> <img class="imgContainer" src="mqttLogo.png" height="70px" width="70px" alt="MQTT icon image" /> </div>' 
			      +'<div class="textContainer"> <p><a href="http://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html"> MQTT V3.1 Specification</a></p></div>' 
			      +'</div>';
		}
	}
