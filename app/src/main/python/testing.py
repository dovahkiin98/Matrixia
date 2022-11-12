# import io
import numpy as np

def multiply(a, b):
    return np.dot(a, b)

def plus(a, b):
    return np.add(a, b)

def minus(a, b):
    return np.subtract(a, b)

# import matplotlib.pyplot as plt

# def plot_sin(a, b):
#     x = np.arange(0,4*np.pi,0.1)   # start,stop,step
#     y = np.sin(x)
#     plt.plot(x,y)

#     f = io.BytesIO()
#     plt.savefig(f, format="png")
#     return f.getvalue()


result = plus(((1), (2)), (1, 2))

print(result)
