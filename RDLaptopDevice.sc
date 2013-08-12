RDLaptop : NMLPeerRegistrar {

	*new {arg name;
		^super.new(name).init2(name);
	}

	init2 {arg name;
		this.join;
		this.prepareToRecvLDRValues;
	}

	sendLEDValue {arg deviceName, value;
		(peer.name.asString + "sending" + "\\actuate\\led" + value).postln;
		addrBook.send(deviceName, '\actuate\led', value);
	}

	prepareToRecvLDRValues {
		OSCFunc({arg msg;
			(peer.name.asString + "receiving" + "\\sense\\ldr" + msg).postln;
		}, '\sense\ldr')
	}
}

RDDevice : NMLPeerRegistrar {

	*new {arg name;
		^super.new(name).init2(name);
	}

	init2 {arg name;
		this.join;
		this.prepareToRecvAnLEDValue;
		this.repeatedlySendLDRValues;
	}

	repeatedlySendLDRValues {arg deviceName, state;
		Routine({
			inf.do{
				var randVal = 100.rand;
				addrBook.peers.do{arg thisPeer;
					if ((thisPeer.name == \laptop1) || (thisPeer.name == \laptop2), { // send messages only to laptops, not to other devices
						(peer.name.asString + "sending" + "\\sense\\ldr" + randVal.asString + "to" + thisPeer.name).postln;
						addrBook.send(thisPeer.name, '\sense\ldr', randVal);
					});
				};
				0.5.wait;
			}
		}).play
	}

	prepareToRecvAnLEDValue {
		OSCFunc({arg msg;
			(peer.name.asString + "receiving" + '\\actuate\\led' + msg).postln;
		}, '\actuate\led')
	}

}



