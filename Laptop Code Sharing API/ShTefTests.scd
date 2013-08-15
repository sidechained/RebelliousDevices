(
// setup
thisProcess.openUDPPort(3000);
~peer1 = Peer(\me1, NetAddr.localAddr);
~addrBook1 = AddrBook().addMe(~peer1);
ShTdef.setupPool(~peer1, ~addrBook1);
ShTdef.new(\x, {\test.postln});
)

~addrBook1.send(\me1, '/hello')

OSCFunc.trace

(
// check if added:
ShTdef.pool.keys.postln;
Tdef.all.postln;
Tdef(\x).source.asCompileString;
)

ShTdef.pool.at(\x).creatorName

(
// replace func
ShTdef.new(\x, {\testy.postln});
)

(
// replace func
ShTdef.new(\x, {\testy.postln});
)






