////////////////////////
// Use OSCDataSpace and OSCObjectSpace, in this case to make local instances for remote Servers and share SynthDefs
// as well as to share some parameters

// make some peers and address books
(
thisProcess.openUDPPort(3000);
~peer1 = Peer(\me1, NetAddr.localAddr);
~peer2 = Peer(\me2, NetAddr("127.0.0.1", 3000)); // I'll use a different port and simulate a different machine
~addrBook1 = AddrBook().addMe(~peer1);
~addrBook2 = AddrBook().addMe(~peer2);
~addrBook1.add(~peer2);
~addrBook2.add(~peer1);
// make some client IDs; we only need this if we want to play on each other's servers
~me1ClientID = 1;
~me2ClientID = 2;
)
// here each participant creates and boots her server
(
~server1 = Server(\me1Server, NetAddr("127.0.0.1", 57111), clientID:~me1ClientID);
~server2 = Server(\me2Server, NetAddr("127.0.0.1", 57112), clientID:~me2ClientID);
~server1.boot;
~server2.boot;
)

// here we will share server addresses
// OSCObjectSpace makes local copies of archivable objects stored remotely (see notes below)
(
~serverAddrs1 = OSCObjectSpace(~addrBook1, oscPath:'/serverAddrs'); // me1's local copy
~serverAddrs2 = OSCObjectSpace(~addrBook2, oscPath:'/serverAddrs'); // me2's local copy
)

(
~me1RemoteServers = IdentityDictionary.new; // here me1 will store her remote Server objects
~serverAddrs1.addDependant({|objectSpace, what, key, val|
	var newServer;
	if(key != ~addrBook1.me.name, {
		"New Server!!".postln;
		newServer = Server(key, val, clientID:~me1ClientID);
		~me1RemoteServers[key] = newServer;
		SynthDescLib.global.addServer(newServer); // the remote server now gets any defs I add
	});
});
)

~serverAddrs1.put(\me1, ~server1.addr); // me1 adds her Server addr
~serverAddrs2.put(\me2, ~server2.addr); // me2 adds his Server addr; now me1 should have a local Server object referring to it

// we'll get updated each time a new desc is added remotely
// and SynthDescRelay adds the desc to the specified lib
(
~synthDescRel1 = SynthDescRelay(~addrBook1);
~lib2 = SynthDescLib(\lib2, ~server2); // fake a different default SynthDescLib on a different machine
~synthDescRel2 = SynthDescRelay(~addrBook2, libName:\lib2);
)
// me2 adds an action for new descs
(
~synthDescRel2.addDependant({|descRelay, what, desc, defcode|
	"Check out this new def!!\n\n%\n".format(defcode).postln;
});
)

// simulate me1 making a def
SynthDef(\foo, {|freq = 440, dur = 1, amp = 0.1| Out.ar(0, SinOsc.ar(freq, 0, amp) * Env.sine(dur).kr(2));}).add;

// here we'll share some parameters
// OSCDataSpace allows OSC types only
// NB that Strings are converted to Symbols by SC's OSC implementation
(
~params1 = OSCDataSpace(~addrBook1, '/params');
~params2 = OSCDataSpace(~addrBook2, '/params');
)

// me2 sets the freq
~params2[\freq] = 880;

// me1 starts playing on me2's server
Pbind(\instrument, \foo, \freq, Pfunc({~params2[\freq]}), \server, ~me1RemoteServers[\me2]).play;

// me2 sets the freq which changes me1's stream
~params2[\freq] = 660;

// cleanup
(
~server1.quit;
~server2.quit;
~serverAddrs1.free;
~serverAddrs2.free;
~synthDescRel1.free;
~synthDescRel2.free;
~params1.free;
~params2.free;
)
