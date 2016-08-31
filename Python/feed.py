import socket
import time

TCP_IP = 'localhost'
TCP_PORT = 5009
TIMEOUT = 1000 * 60 * 60 * 8

config = { 'most_recent_protocol': '5.2',
           'product_id': 'EIMAR_BOESJES_12104' }



class feed:
    def __init__(self):
        self.host = TCP_IP
        self.port = TCP_PORT
        self.most_recent_protocol = config['most_recent_protocol']
        self.product_id = config['product_id']

    def recv_timeout(self, sock, timeout = 2):
        sock.setblocking(0)
        total_data = []
        data = ''
        begin = time.time()
        while 1:
            # if you got some data, then break after wait sec
            if total_data and time.time() - begin > timeout:
                break
            # if you got no data at all, wait a little longer
            elif time.time() - begin > timeout * 2:
                break
        
        try:
            data = sock.recv(8192)
            if data:
                total_data.append(data)
                begin = time.time()
            else:
                time.sleep(0.1)
        except:
            pass
        
        return ''.join(total_data)

    def authenticate(self, sock):
        sock.send('S,SET PROTOCOL,%s\r\n' %(self.most_recent_protocol))
        sock.send('S,SET CLIENT NAME,%s\r\n' %(self.product_id))


    def live_socket(self, sock, recv_buff = 4096):
        buffer = ''
        data = ''

        while True:
            data = sock.recv(recv_buff)
            buffer += data

        return buffer

    def watch_symbol(self, sock, symbol):
        message = 'w' + symbol + '\r\n'

        # Open a streaming socket to the local IQFeed server
        #sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        #sock.connect((self.host, self.port))
        sock.sendall(message)
        data = self.live_socket(sock)
        sock.close
        
print 'Initilizing feed...'
stock_feed = feed()
print 'Setting up socket...'
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((TCP_IP, TCP_PORT))
print 'Initializing connection to local server...'
stock_feed.recv_timeout(sock)
print 'Authenticating...'
stock_feed.authenticate(sock)
print 'Start tracking stocks...'
stock_feed.watch_symbol(sock, 'GOOG')
        
        
        

"""
# "main"
print "Creating socket...",
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print "...done."

print "Looking up port number...",
port = socket.getservbyname('http', 'tcp') # queries a list at /etc/services
print "...done."

print "Connecting to remote host on port %d..." %port,
s.connect((TCP_IP, TCP_PORT))
print "...done."

print "Connected from", s.getsockname()
print "Connected to", s.getpeername()

print(recv_timeout(s, 0.15))



s.send('S,SET PROTOCOL,%s\r\n' %(config['most_recent_protocol']))
s.send('S,SET CLIENT NAME,%s\r\n' %(config['product_id']))
print('Sending watch...')
s.send('wGOOG\r\n')



n = 1024
print 'Starting...'
data = s.recv(1024)

while data:
    print data
    data = s.recv(1024)
    n += 1024
    if data.find('!ENDMSG!') >= 0:
        print data
        print 'FOUND:', n
        break

"""
