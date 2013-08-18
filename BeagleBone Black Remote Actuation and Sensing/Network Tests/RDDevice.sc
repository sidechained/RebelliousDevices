RDDevice {

	var me, addrBook;
	var laptopActuateLedResponder, laptopSenseLdrResponder, pythonSenseLdrResponder;
	var sclangActuateOscPath = '/actuate/led', pythonActuateOscPath = '/python/actuate/led';
	var sclangSenseRequestOscPath = '/sense/request/ldr', sclangSenseReturnOscPath = '/sense/return/ldr';
	var pythonSenseRequestOscPath = '/python/sense/request/ldr', pythonSenseReturnOscPath = '/python/sense/return/ldr';
	var pythonSendPort = 10000, pythonReceivePort = 10001;
	var pythonSendAddr, pythonReceiveAddr;

	*new {arg argMe, argAddrBook;
		^super.new.init(argMe, argAddrBook);
	}

	init {arg argMe, argAddrBook;
		me = argMe;
		addrBook = argAddrBook;
		thisProcess.openUDPPort(pythonReceivePort);
		pythonSendAddr = NetAddr("127.0.0.1", pythonSendPort);
		pythonReceiveAddr = NetAddr("127.0.0.1", pythonReceivePort);
		this.join;
	}

	join {
		this.initLatpopActuateLedResponder;
		this.initLaptopSenseLdrResponder;
		this.initPythonSenseLdrResponder;
	}

	leave {
		if (laptopSenseLdrResponder.isPlaying, {laptopSenseLdrResponder.stop});
		laptopActuateLedResponder.free;
	}

	initLatpopActuateLedResponder {
		laptopActuateLedResponder = OSCFunc({arg msg, time, addr, recvPort;
			var ledValue = msg[1];
			(me.asString + "sclang receiving" + msg + "from" + NetAddr(addr.ip, recvPort)).postln;
			this.tellPythonToActuateLed(ledValue);
		}, sclangActuateOscPath ) // make sure this only responds on the node's registered address and port (post-registration)!
	}

	tellPythonToActuateLed {arg ledValue;
		var msgToSend = [pythonActuateOscPath, ledValue];
		(me.asString + "sclang sending" + msgToSend + "to" + pythonSendAddr).postln;
		pythonSendAddr.sendMsg(*msgToSend);
	}

	initLaptopSenseLdrResponder {
		// locally forward sclang to python
		laptopSenseLdrResponder = OSCFunc({arg msg, time, addr, recvPort;
			var nameOfRequestingPlayer = msg[1];
			(me.asString + "sclang receiving" + msg + "from" + NetAddr(addr.ip, recvPort)).postln;
			this.tellPythonToRequestLdrValue(nameOfRequestingPlayer);
		}, sclangSenseRequestOscPath ) // make sure this only responds on the node's registered address and port (post-registration)!
	}

	tellPythonToRequestLdrValue {arg nameOfRequestingPlayer;
		// ask for a single value
		var msgToSend;
		msgToSend = [pythonSenseRequestOscPath, nameOfRequestingPlayer];
		(me.name.asString + "sclang sending:" + msgToSend + "to" + pythonSendAddr).postln;
		pythonSendAddr.sendMsg(*msgToSend);
	}

	initPythonSenseLdrResponder {
		// send back to laptops
		pythonSenseLdrResponder = OSCFunc({arg msg, time, senderAddr, senderPort;
			var nameOfPlayerToReturnTo = msg[1];
			var ldrValue = msg[2];
			var reconstitutedSenderAddr = NetAddr(senderAddr.ip, senderPort);
			// return only to the laptop that requested it
			(me.name.asString + "received:" + msg + "from" + reconstitutedSenderAddr).postln;
			this.returnLdrValue(nameOfPlayerToReturnTo, ldrValue);
		}, pythonSenseReturnOscPath, nil, pythonReceivePort) // doesn't work if srcID bound to pythonReceiveAddr (why not?)
	}

	returnLdrValue {arg nameOfPlayerToReturnTo, ldrValue;
		var msgToSend = [sclangSenseReturnOscPath, ldrValue];
		(me.name.asString + "sending:" + msgToSend + "to" + addrBook[nameOfPlayerToReturnTo]).postln;
		addrBook.send(nameOfPlayerToReturnTo, *msgToSend);
	}

}
