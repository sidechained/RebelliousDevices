RDDevice {

	var me, addrBook;
	var ldrValueRequester, ldrValueReturner, ledValueReceiver;
	var sclangActuateOscPath = '/actuate/led', pythonActuateOscPath = '/python/actuate/led';
	var pythonSenseRequestOscPath = '/python/sense/request/ldr', pythonSenseReturnOscPath = '/python/sense/return/ldr', sclangSenseOscPath = '/sense/ldr';
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
		this.initLedValueReceiver;
		this.initLdrValueRequester;
		this.initLdrValueReturner;
	}

	leave {
		if (ldrValueRequester.isPlaying, {ldrValueRequester.stop});
		ledValueReceiver.free;
	}

	initLedValueReceiver {
		sclangActuateOscPath.postln;
		ledValueReceiver = OSCFunc({arg msg, time, addr, recvPort;
			var ledValue = msg[1];
			var msgToSend = [pythonActuateOscPath, ledValue];
			(me.asString + "sclang receiving" + msg + "from" + NetAddr(addr.ip, recvPort)).postln;
			(me.asString + "sclang sending" + msgToSend + "to" + pythonSendAddr).postln;
			pythonSendAddr.sendMsg(*msgToSend);
		}, sclangActuateOscPath ) // make sure this only responds on the node's registered address and port (post-registration)!
	}

	initLdrValueRequester {
		// ask python script to repeatedly poll a pinin and return a value
		ldrValueRequester = Routine({
			inf.do{
				var msgToSend;
				msgToSend = [pythonSenseRequestOscPath];
				(me.name.asString + "sending:" + msgToSend + "to" + pythonSendAddr).postln;
				pythonSendAddr.sendMsg(*msgToSend);
				0.5.wait;
			};
		})
	}

	initLdrValueReturner {
		// send back to laptops
		ldrValueReturner = OSCFunc({arg msg, time, senderAddr, senderPort;
			addrBook.peers.do{arg peer;
				if ((peer.name == \laptop1) || (peer.name == \laptop2), { // send messages only to laptops, not to other devices]
					var ldrValue = msg[1];
					var msgToSend = [sclangSenseOscPath, ldrValue];
					var reconstitutedSenderAddr = NetAddr(senderAddr.ip, senderPort);
					(me.name.asString + "received:" + msg + "from" + reconstitutedSenderAddr).postln;
					(me.name.asString + "sending:" + msgToSend + "to" + peer).postln;
					addrBook.send(peer.name, *msgToSend);
				});
			};
		}, pythonSenseReturnOscPath, nil, pythonReceivePort) // doesn't work if srcID bound to pythonReceiveAddr (why not?)
	}

	startSensing {
		ldrValueRequester.play;
	}

	stopSensing {
		ldrValueRequester.stop;
	}

}