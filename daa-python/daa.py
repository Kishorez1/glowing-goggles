import math

class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

def comparex(p1, p2):
    return p1.x - p2.x

def comparey(p1, p2):
    return p1.y - p2.y

def dist(p1, p2):
    return math.sqrt((p1.x - p2.x)**2 + (p1.y - p2.y)**2)

def bruteforce(ar):
    min_dist = float('inf')
    for i in range(len(ar)):
        for j in range(i+1, len(ar)):
            dist = ar[i].dist(ar[j])
            if dist < min_dist:
                min_dist = dist
    return min_dist

def strip_closest(strip, d):
    min_dist = d
    strip.sort(key=lambda p: p.y)
    for i in range(len(strip)):
        for j in range(i+1, len(strip)):
            if strip[j].y - strip[i].y > min_dist:
                break
            dist = strip[i].dist(strip[j])
            if dist < min_dist:
                min_dist = dist
    return min_dist

def nearest_untill(ar):
    if len(ar) <= 3:
        return bruteforce(ar)
    mid = len(ar) // 2
    mid_point = ar[mid]
    dl = nearest_untill(ar[:mid])
    dr = nearest_untill(ar[mid:])
    d = min(dl, dr)
    strip = [p for p in ar if abs(p.x - mid_point.x) < d]
    return min(d, strip_closest(strip, d))

def nearest(ar):
    ar.sort(key=lambda p: p.x)
    return nearest_untill(ar)


def main():
    with open('input.txt', 'r') as f:
        n = int(f.readline())
        ar = [Point(*map(int, line.split())) for line in f]
    print("The smallest distance between nearest pair is: {:.2f}".format(nearest(ar)))

if __name__ == '__main__':
    main()
