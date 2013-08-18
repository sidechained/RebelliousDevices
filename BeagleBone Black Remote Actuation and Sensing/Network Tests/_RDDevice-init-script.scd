(
// run this on the beagleboard
var addrBook, laptop1Peer, device1Peer;
addrBook = AddrBook();
laptop1Peer = Peer(\laptop1, NetAddr("192.168.2.1", 9000));
thisProcess.openUDPPort(9002);
device1Peer = Peer(\device1, NetAddr("192.168.2.4", 9002));
addrBook.add(laptop1Peer);
addrBook.add(device1Peer);
RDDevice(device1Peer, addrBook);
"Running".postln
)

