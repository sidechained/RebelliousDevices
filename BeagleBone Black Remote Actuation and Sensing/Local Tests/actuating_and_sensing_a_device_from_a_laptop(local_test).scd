(
// 1. simulating the beagleboard
// - one laptop and one device running locally
// - need to run 'RDDevice.py -n' in python
~addrBook = AddrBook();
thisProcess.openUDPPort(9000);
~laptop1Peer = Peer(\laptop1, NetAddr("127.0.0.1", 9000));
thisProcess.openUDPPort(9002);
~device1Peer = Peer(\device1, NetAddr("127.0.0.1", 9002));
~addrBook.add(~laptop1Peer);
~addrBook.add(~device1Peer);
~laptop1 = RDLaptop(~laptop1Peer, ~addrBook);
~device1 = RDDevice(~device1Peer, ~addrBook);
)

// 2. running the device code on the beagleboard itself
// - laptop has the IP: 192.168.7.3
// - device has the IP: 192.168.7.5

// LATER add another laptop

~laptop2 = RDLaptop(~laptop2Peer, ~addrBook);
~addrBook.add(~laptop2Peer);
thisProcess.openUDPPort(9001);
~laptop2Peer = Peer(\laptop2, NetAddr("127.0.0.1", 9002));
