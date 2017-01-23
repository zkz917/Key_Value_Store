import client

def generate_configs():
    config = {}
    for i in [1, 4]:
        config['THREAD_NUM'] = i
        config['SESSION_PER_THREAD'] = 10
        if i > 1:
            config['SESSION_PER_THREAD'] = 1000
        for instruction in ['ALL', 'GET', 'PUT']:
            config['INSTRUCTION'] = instruction
            yield config

def main_test():
    for config in generate_configs():
        print(config)
        client.THREAD_NUM = config['THREAD_NUM']
        client.SESSION_PER_THREAD = config['SESSION_PER_THREAD']
        client.INSTRUCTION = config['INSTRUCTION']
        client.main()

if __name__ == '__main__':
    main_test()
