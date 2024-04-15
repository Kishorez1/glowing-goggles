import math
import sys

class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

def compare_x(p1, p2):
    return p1.x - p2.x

def compare_y(p1, p2):
    return p1.y - p2.y

def dist(p1, p2):
    return math.sqrt((p1.x - p2.x)**2 + (p1.y - p2.y)**2)

def brute_force(arr, n):
    min_dist = sys.float_info.max
    for i in range(n):
        for j in range(i+1, n):
            if dist(arr[i], arr[j]) < min_dist:
                min_dist = dist(arr[i], arr[j])
    return min_dist

def strip_closest(strip, j, d):
    min_dist = d
    strip.sort(key=lambda x: x.y)
    for i in range(j):
        for m in range(i+1, j):
            if strip[m].y - strip[i].y < d:
                if dist(strip[i], strip[m]) < min_dist:
                    min_dist = dist(strip[i], strip[m])
            else:
                break
    return min_dist

def nearest_until(arr, n):
    if n <= 3:
        return brute_force(arr, n)
    
    mid = n // 2
    mid_point = arr[mid]
    
    dl = nearest_until(arr[:mid], mid)
    dr = nearest_until(arr[mid:], n-mid)
    
    d = min(dl, dr)
    
    strip = []
    j = 0
    for i in range(n):
        if abs(arr[i].x - mid_point.x) < d:
            strip.append(arr[i])
            j += 1
    
    return min(d, strip_closest(strip, j, d))

def nearest(arr, n):
    arr.sort(key=compare_x)
    return nearest_until(arr, n)

if __name__ == "__main__":
    n = int(input())
    arr = []
    for _ in range(n):
        x, y = map(int, input().split())
        arr.append(Point(x, y))
    
    print(f"The smallest distance between nearest pair is: {nearest(arr, n)}")


