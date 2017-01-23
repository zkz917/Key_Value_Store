import socket
from multiprocessing import Pool
from multiprocessing.dummy import Pool as ThreadPool
import _thread
import time
import rand
import config

HOST = "localhost"
PORT = config.PORT
THREAD_NUM = 4
SESSION_PER_THREAD = 1000
IS_MULTI_THREAD = False
INSTRUCTION = 'PUT' # 'GET' | 'PUT' | 'ALL'

def main():
    global HOST, PORT
    service = input('Input service address: ')
    if service:
        service = service.split(':')
        HOST = service[0] if service[0] else HOST
        PORT = int(service[1]) if service[1] else PORT

    randomSessions = {'ALL': rand.randomSessions, 
                      'GET': rand.randomGetSessions, 
                      'PUT': rand.randomPutSessions}[INSTRUCTION]

    if IS_MULTI_THREAD:
        # multi thread v1
        # for i in range(5):
        #     _thread.start_new_thread(millionRequestTest, ())
        # while True:
        #     pass

        # multi thread v2
        pool = ThreadPool(THREAD_NUM)
        start = time.time()
        results = pool.map(test_request, randomSessions(THREAD_NUM*SESSION_PER_THREAD))
        end = time.time()
        pool.close()
        pool.join()
        with open(time.strftime("%Y-%m-%d-%H%M%S", time.localtime()) + '.txt', 'w') as f:
            f.write(repr(end-start))
            f.write('\n')
            for element in results:
                f.write(repr(element['message']) + '\n')
                f.write(repr(element['result']) + '\n')
    else:
        # single thread 
        msg = input('Input instruction: ')
        while True:
            print('msg: %s' % msg)
            if msg == 'exit':
                break
            if msg == 'batch':
                if INSTRUCTION == 'PUT':
                    instructions = rand.randomPuts
                elif INSTRUCTION == 'GET':
                    instructions = rand.randomGets
                elif INSTRUCTION == 'ALL':
                    instructions = rand.randomInst
                    
                total = 10000
                start = time.time()
                for inst in instructions(n=total):
                    request(msg)
                end = time.time()
                print('avg: %f' % (end-start)/total)
            else:
                print(request(msg))
                msg = input('Input instruction: ')

def million_request_test():
    for messages in rand.randomSessions(SESSION_PER_THREAD):
        request(messages)

def test_request(messages):
    return {'message': messages, 'result': request(messages)}

def request(messages):
    s = Socket()
    s.connect(HOST, PORT)
    if type(messages) is str:
        s.send(messages)
        ret = s.receive().strip()
    else:
        ret = []
        for msg in messages:
            s.send(msg)
            ret += [s.receive().strip()]
    s.close()
    return ret

class Socket:
    def __init__(self, sock=None):
        if sock is None:
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        else:
            self.sock = sock

    def connect(self, host, port):
        self.sock.connect((host, port))

    def send(self, msg):
        if not msg.endswith('\r\n'):
            msg += '\r\n'
        sent = self.sock.send(bytes(msg, 'utf8'))
        if sent == 0:
            raise RuntimeError('socket connection broken')

    def receive(self):
        msg = ''
        c = self.sock.recv(1).decode('utf8')
        while c != '\n' and c != '':
            msg += c
            c = self.sock.recv(1).decode('utf8')
        msg += c
        return msg

    def close(self):
        self.sock.shutdown(socket.SHUT_RDWR)
        self.sock.close()

if __name__ == '__main__':
    main()
