(
var addrBook, laptop1Peer, device1Peer;
// a. run this on the laptop
addrBook = AddrBook();
thisProcess.openUDPPort(9000);
laptop1Peer = Peer(\laptop1, NetAddr("192.168.2.1", 9000));
device1Peer = Peer(\device1, NetAddr("192.168.2.4", 9002));
addrBook.add(laptop1Peer);
addrBook.add(device1Peer);
~laptop = RDLaptop(laptop1Peer, addrBook);
)

~laptop.actuateLed(\device1, 0)
~laptop.senseLdr(\device1);

(
Routine({
	inf.do{
		~laptop.senseLdr(\device1);
		0.5.wait
}).play
)