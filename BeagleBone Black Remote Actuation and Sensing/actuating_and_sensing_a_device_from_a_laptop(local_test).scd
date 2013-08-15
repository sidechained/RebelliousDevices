(
~addrBook = AddrBook();
thisProcess.openUDPPort(9000);
~laptop1Peer = Peer(\laptop1, NetAddr("127.0.0.1", 9000));
thisProcess.openUDPPort(9001);
~laptop2Peer = Peer(\laptop1, NetAddr("127.0.0.1", 9002));
thisProcess.openUDPPort(9003);
~device1Peer = Peer(\device1, NetAddr("127.0.0.1", 9003));
~addrBook.add(~laptop1Peer);
~addrBook.add(~laptop2Peer);
~addrBook.add(~device1Peer);
~laptop1 = RDLaptop(~laptop1Peer, ~addrBook);
~laptop2 = RDLaptop(~laptop2Peer, ~addrBook);
~device1 = RDDevice(~device1Peer, ~addrBook);
)

~laptop1.sendLedValue(\device1, 1);"";
~device1.startSensing
~device1.stopSensing


