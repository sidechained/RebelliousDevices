// SC To Python

// port1 / laptop1 / 192.168.7.2
// port3 / device1 / 192.168.7.4

// sending:
// LAPTOP SCLANG sends 				"/actuate/led 1"	 				to DEVICE SCLANG 	on NetAddr(192.168.7.4, 9000)
// DEVICE SCLANG receives 			"/actuate/led 1" 										from NetAddr(192.168.7.4, 9000)
// DEVICE SCLANG sends 				"/python/actuate/led 1" 			to DEVICE PYTHON 	on NetAddr(127.0.0.1, 10000)
// DEVICE PYTHON receives 			"/python/actuate/led 1" 								from NetAddr(127.0.0.1, 10000)

// receiving:
// DEVICE SCLANG repeatedly sends	"/python/sense/request/ldr"			to DEVICE PYTHON	on NetAddr(127.0.0.1, 10000)
// DEVICE PYTHON receives 			"/python/sense/request/ldr"	 							from NetAddr(127.0.0.1, 10000)
// DEVICE PYTHON sends				"/python/sense/return/ldr [value]"	to DEVICE SCLANG	on NetAddr(127.0.0.1, 10001)
// DEVICE SCLANG receives			"/python/sense/return/ldr [value]"						on NetAddr(127.0.0.1, 10001)
// DEVICE SCLANG sends				"/sense/ldr [value]"				to LAPTOPS SCLANG	on NetAddr(192.168.7.2, 9000)
// LAPTOPS SCLANG receives			"/sense/ldr [value]"									from NetAddr(192.168.7.2, 9000)
// value could then be stored, ready to poll

RDLaptop {

	var me, addrBook;
	var ldrValueReceiver;
	var sclangActuateOscPath = '/actuate/led';
	var sclangSenseRequestOscPath = '/sense/request/ldr';
	var sclangSenseReturnOscPath = '/sense/return/ldr';

	*new {arg argMe, argAddrBook;
		^super.new.init(argMe, argAddrBook);
	}

	init {arg argMe, argAddrBook;
		me = argMe;
		addrBook = argAddrBook;
		this.join;
	}

	join {
		this.initLdrValueReceiver;
	}

	leave {
		ldrValueReceiver.free;
	}

	actuateLed {arg deviceName, value;
		var msg = [sclangActuateOscPath, value];
		(me.asString + "sending" + msg + "to" + addrBook[deviceName]).postln;
		addrBook.send(deviceName, *msg);
	}

	senseLdr {arg deviceName;
		// ask for a single value
		var msgToSend;
		msgToSend = [sclangSenseRequestOscPath, me.name];
		(me.name.asString + "sending:" + msgToSend + "to" + addrBook[deviceName]).postln;
		addrBook.send(deviceName, *msgToSend);
	}

	initLdrValueReceiver {
		ldrValueReceiver = OSCFunc({arg msg;
			(me.name.asString + "receiving: " + sclangSenseReturnOscPath.asString + msg).postln;
		}, sclangSenseReturnOscPath, nil, me.addr.port) // doesn't work if srcID bound to me.addr (why not?)
	}

}

