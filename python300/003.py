import time


def my_func():
    start_time = time.time()
    s = 0
    for i in range(1, n + 1):
        s = s + 1

    end_time = time.time()
    return s, end_time - start_time


n = 5
print(my_func())
