/** ************************ connector ************************* */
var SAAgent = null; /* 서비스를 생성 */
var SASocket = null; /* 안드로이드와 통신을 위해 생성 */
var CHANNELID = 104; /* ServiceProfile.xml에 적혀있음. */
var ProviderAppName = "WellDoneNoteGear2"; /* 안드로이드에서 이 이름을 써야함 */

function createHTML(log_string) {
	var str = log_string.split("//");
	var word = new String(str[0]);
	var mean = new String(str[1]);

	var wordDiv = document.getElementById('resultBoardWord');
	wordDiv.innerHTML = word;

	var meanDiv = document.getElementById('resultBoardMean');
	meanDiv.innerHTML = mean;

	word.fontcolor("red");
	mean.fontcolor("blue");

	alert(word + " : " + mean);
}

function onReceive(channelId, data) {
	wContents = data;
	openFileStreamForWrite();
	createHTML(data);
}

var agentCallBack = {
	onconnect : function(socket) {
		SASocket = socket;
		SASocket.setSocketStatusListener(function(reason) {
			console.log("socket status changed : " + reason);
		});

		SASocket.setDataReceiveListener(onReceive);
	}
};

var peerAgentFindCallback = {
	onpeeragentfound : function(peerAgent) {
		try {
			if (peerAgent.appName == ProviderAppName) {
				SAAgent.setServiceConnectionListener(agentCallBack);
				SAAgent.requestServiceConnection(peerAgent);
			}
		} catch (err) {
			alert("exception : " + err.name + ", msg : " + err.message);
		}
	}
};

function onSuccess(agents) {
	try {
		if (agents.length > 0) {
			SAAgent = agents[0];
			SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
			SAAgent.findPeerAgents();
		} else {
			alert("Not found SAAgent!");
		}
	} catch (err) {
		alert("exception : " + err.name + ", msg : " + err.message);
	}
}

function onError(err) {
	alert("안드로이드 앱과 연결할 수 없습니다.");
}

/*
 * 안드로이드 어플리케이션에 접속 요청.
 */
function connect() {
	if (SASocket) {
		disConnect();
	}

	try {
		webapis.sa.requestSAAgent(onSuccess, onError);
		console.log("Success connection.");
	} catch (err) {
		alert("exception : " + err.name + ", msg : " + err.message);
	}
}

/*
 * 안드로이드 어플리케이션과 접속을 끊음.
 */
function disConnect() {
	try {
		if (SASocket != null) {
			SASocket.close();
			SASocket = null;
		}
	} catch (err) {
		alert("exception : " + err.name + ", msg : " + err.message);
	}
}

/*
 * 안드로이드 어플리케이션에 정보 갱신 요청
 */
function fetch() {
	try {
		SASocket.sendData(CHANNELID, "fetch :");
		console.log("fetch send.");
	} catch (err) {
		console.log("exception : " + err.name + ", msg : " + err.message);
	}
}

/*
 * 안드로이드 어플리케이션으로 데이터 보내기
 */
function send() {
	try {
		SASocket.sendData(CHANNELID, "send :");
	} catch (err) {
		console.log("exception : " + err.name + ", msg : " + err.message);
	}
}
/** ************************ end connector ************************** */

/** ************************* watch *************************** */
/* global window, document, tizen, setTimeout */
/* jslint plusplus: true */

var canvas, context, clockRadius;
var middleFilterValue = 320 - 280;
var dayStr = [ "일", "월", "화", "수", "목", "금", "토" ];

window.requestAnimationFrame = window.requestAnimationFrame
		|| window.webkitRequestAnimationFrame
		|| window.mozRequestAnimationFrame || window.oRequestAnimationFrame
		|| window.msRequestAnimationFrame || function(callback) {
			'use strict';
			window.setTimeout(callback, 1000 / 60);
		};

var type = 0;
var typeCount = 2;
function clockChange() {
	type++;
	if (type >= typeCount) {
		type = 0;
	}
	window.requestAnimationFrame(watch);
}

function renderDigitalClock(dateObj) {
	var timeDiv = document.getElementById("timeDiv");
	var meridiemDiv = document.getElementById("merDiv");
	var dateDiv = document.getElementById("dateDiv");

	var meridiemTxt = "AM";
	var month = (dateObj.getMonth() + 1) + "월";
	var dayWeek = dayStr[dateObj.getDay()];
	var day = dateObj.getDate() + "일";

	var hour = dateObj.getHours();

	if (hour < 12) {
		meridiemTxt = "AM";
	} else {
		meridiemTxt = "PM";
		hour -= 12;
	}

	if (hour == 0) {
		hour = 12;
	}

	var zeroHour = "";
	if (hour < 10) {
		zeroHour = "0";
	}

	var zeroMin = "";
	var minutes = dateObj.getMinutes();
	if (minutes < 10) {
		zeroMin = "0";
	}

	timeDiv.innerHTML = zeroHour + hour + ":" + zeroMin + minutes;
	meridiemDiv.innerHTML = meridiemTxt;
	dateDiv.innerHTML = month + " " + day + " (" + dayWeek + ")";
}

function clearDigitalClock() {
	var timeDiv = document.getElementById("timeDiv");
	var meridiemDiv = document.getElementById("merDiv");
	var dateDiv = document.getElementById("dateDiv");

	timeDiv.innerHTML = "";
	meridiemDiv.innerHTML = "";
	dateDiv.innerHTML = "";
}

function renderDots() {
	'use strict';

	var dx = 0, dy = 0, i = 1, angle = null;

	context.save();

	// Assigns the clock creation location in the middle of the canvas
	context.translate(canvas.width / 2 + (middleFilterValue / 2),
			canvas.height / 2);

	// Assign the style of the number which will be applied to the clock plate
	context.beginPath();

	context.fillStyle = '#999999';

	// Create 4 dots in a circle
	for (i = 1; i <= 4; i++) {
		angle = (i - 3) * (Math.PI * 2) / 4;
		dx = clockRadius * 0.9 * Math.cos(angle);
		dy = clockRadius * 0.9 * Math.sin(angle);

		context.arc(dx, dy, 3, 0, 2 * Math.PI, false);
		context.fill();
	}
	context.closePath();

	// Render center dot
	context.beginPath();

	context.fillStyle = '#ff9000';
	context.strokeStyle = '#fff';
	context.lineWidth = 4;

	context.arc(0, 0, 7, 0, 2 * Math.PI, false);
	context.fill();
	context.stroke();
	context.closePath();
}

function renderNeedle(angle, radius) {
	'use strict';
	context.save();
	context.rotate(angle);
	context.beginPath();
	context.lineWidth = 4;
	context.strokeStyle = '#fff';
	context.moveTo(6, 0);
	context.lineTo(radius, 0);
	context.closePath();
	context.stroke();
	context.closePath();
	context.restore();
}

function renderHourNeedle(hour) {
	'use strict';

	var angle = null, radius = null;

	angle = (hour - 3) * (Math.PI * 2) / 12;
	radius = clockRadius * 0.55;
	renderNeedle(angle, radius);
}

function renderMinuteNeedle(minute) {
	'use strict';

	var angle = null, radius = null;

	angle = (minute - 15) * (Math.PI * 2) / 60;
	radius = clockRadius * 0.75;
	renderNeedle(angle, radius);
}

function watch() {
	'use strict';

	// Import the current time
	// noinspection JSUnusedAssignment
	var date = new Date(), hours = date.getHours(), minutes = date.getMinutes(), seconds = date
			.getSeconds(), hour = hours + minutes / 60, minute = minutes
			+ seconds / 60;

	// Erase the previous time
	context.clearRect(0, 0, context.canvas.width, context.canvas.height);

	switch (type) {
	case 0:
		canvas.width = document.width;
		canvas.height = canvas.width;
		clearDigitalClock();
		renderDots();
		renderHourNeedle(hour);
		renderMinuteNeedle(minute);
		break;

	case 1:
		canvas.width = 0;
		canvas.height = 0;
		renderDigitalClock(date);
		break;

	default:
		break;
	}

	context.restore();
}

/** ********************** end watch ************************* */

/** ************************ main *************************** */
window.onload = function() {
	'use strict';

	// search canvs from DOM Node(CSS or html)
	canvas = document.querySelector('canvas');
	context = canvas.getContext('2d');
	clockRadius = document.width / 2 - (middleFilterValue / 2);

	// Assigns the area that will use Canvas
	canvas.width = document.width;
	canvas.height = canvas.width;

	// add eventListener for tizenhwkey
	window.addEventListener('tizenhwkey', function(e) {
		if (e.keyName == 'back') {
			disConnect();
			tizen.application.getCurrentApplication().exit();
		}
	});

	connect();
	alert("SeaHorse Gear2! 안드로이드 앱과 연결합니다.");

	var nextMove = 30000 - new Date().getMilliseconds();
	setInterval(function() {
		connect();
		window.requestAnimationFrame(watch);
	}, nextMove);

	window.requestAnimationFrame(watch);
};
