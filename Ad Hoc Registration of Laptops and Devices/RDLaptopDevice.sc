RDLaptop : NMLNode {

	var ldrValueReceiver;

	*new {arg name;
		^super.new(name);
	}

	join {
		super.join;
		this.prepareToRecvLDRValues;
	}

	leave {
		ldrValueReceiver.free;
	}

	sendLEDValue {arg deviceName, value;
		(nodeName.asString + "sending" + "\\actuate\\led" + value + "to device named: " + deviceName.asString).postln;
		hailer.addrBook.send(deviceName, '\actuate\led', value);
	}

	prepareToRecvLDRValues {
		ldrValueReceiver = OSCFunc({arg msg;
			(nodeName.asString + "receiving" + "\\sense\\ldr" + msg).postln;
		}, '\sense\ldr') // make sure this only responds on the node's registered address and port (post-registration)!
	}
}

RDDevice : NMLNode {

	var <hailer, nodeName, ldrValueSender, ledValueReceiver;

	*new {arg name;
		^super.new(name);
	}

	join {
		super.join;
		this.initLedValueReceiver;
		this.initLdrValueSender;
	}

	leave {
		ldrValueSender.stop;
		ledValueReceiver.free;
	}

	startSensing {
		ldrValueSender.play;
	}

	stopSensing {
		ldrValueSender.stop;
	}

	initLdrValueSender {arg deviceName, state;
		ldrValueSender = Routine({
			inf.do{
				var randVal = 100.rand;
				hailer.addrBook.peers.do{arg thisPeer;
					if ((thisPeer.name == \laptop1) || (thisPeer.name == \laptop2), { // send messages only to laptops, not to other devices
						(nodeName.asString + "sending" + "\\sense\\ldr" + randVal.asString + "to" + thisPeer.name).postln;
						hailer.addrBook.send(thisPeer.name, '\sense\ldr', randVal);
					});
				};
				0.5.wait;
			}
		})
	}

	initLedValueReceiver {
		ledValueReceiver = OSCFunc({arg msg;
			(nodeName.asString + "receiving" + '\\actuate\\led' + msg).postln;
		}, '\actuate\led' ) // make sure this only responds on the node's registered address and port (post-registration)!
	}

}

NMLNode {

	// a node is a combination of a peer and hailer

	var <hailer, nodeName, portAssigned;

	*new {arg name;
		^super.new.init(name)
	}

	init {arg name;
		nodeName = name;
		this.join;
	}

	join {
		var fakePeer;
		fakePeer = Peer(nodeName, online: false);
		hailer = Hail(me: fakePeer);

	}

	leave {
		hailer.free;
	}

}