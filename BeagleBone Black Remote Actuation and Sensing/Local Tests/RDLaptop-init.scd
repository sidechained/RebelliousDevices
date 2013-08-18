(
// a. run this on the laptop
~lTaddrBook = AddrBook();
thisProcess.openUDPPort(9000);
//~lTlaptop1Peer = Peer(\laptop1, NetAddr("192.168.7.3", 9000));
//~lTdevice1Peer = Peer(\device1, NetAddr("192.168.7.5", 9002));
~lTlaptop1Peer = Peer(\laptop1, NetAddr("127.0.0.1", 9000));
~lTdevice1Peer = Peer(\device1, NetAddr("127.0.0.1", 9002));
~lTaddrBook.add(~lTlaptop1Peer);
~lTaddrBook.add(~lTdevice1Peer);
~lTlaptop1 = RDLaptop(~lTlaptop1Peer, ~lTaddrBook);
)

~lTlaptop1.actuateLed(\device1, 1)
~lTlaptop1.senseLdr(\device1)