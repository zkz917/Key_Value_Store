import random

VALUE_RANGE = (-99, 99)
SESSION_SIZE = 10
random.seed(0)      # use pseudo random

def randomSessions(n=10000, keys=list(range(10))):
    for i in range(n):
        yield list(randomInst(SESSION_SIZE, keys))

def randomPutSessions(n=10000, keys=list(range(10))):
    for i in range(n):
        yield list(randomPuts(SESSION_SIZE, keys))

def randomGetSessions(n=10000, keys=list(range(10))):
    for i in range(n):
        yield list(randomGets(SESSION_SIZE, keys))

def randomPuts(n=10, keys=list(range(10))):
    for i in range(n):
        key = keys[random.randint(0, len(keys) - 1)]
        value = random.randint(VALUE_RANGE[0], VALUE_RANGE[1])
        yield put(key, value)

def randomGets(n=10, keys=list(range(10))):
    for i in range(n):
        key = keys[random.randint(0, len(keys) - 1)]
        yield get(key)

def randomInst(n=10, keys=list(range(10))):
    for i in range(n):
        key = keys[random.randint(0, len(keys) - 1)]
        if random.randint(0, 2):
            value = random.randint(VALUE_RANGE[0], VALUE_RANGE[1])
            yield put(key, value)
        else:
            yield get(key)

def put(key, value):
    return "PUT {key} {value}".format(key=key, value=value)

def get(key):
    return "GET {key}".format(key=key)
