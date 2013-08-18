(
// b. run this on the beagleboard
~bBaddrBook = AddrBook();
//~bBlaptop1Peer = Peer(\laptop1, NetAddr("192.168.7.3", 9000));
~bBlaptop1Peer = Peer(\laptop1, NetAddr("127.0.0.1", 9000));
thisProcess.openUDPPort(9002);
//~bBdevice1Peer = Peer(\device1, NetAddr("192.168.7.5", 9002));
~bBdevice1Peer = Peer(\device1, NetAddr("127.0.0.1", 9002));
~bBaddrBook.add(~bBlaptop1Peer);
~bBaddrBook.add(~bBdevice1Peer);
~bBdevice1 = RDDevice(~bBdevice1Peer, ~bBaddrBook);
)

