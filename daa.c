
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <float.h>

typedef struct point {
    int x, y;
} point;

int comparex(const void *a, const void *b) {
    point *p1 = (point *)a, *p2 = (point *)b;
    return (p1->x - p2->x);
}

int comparey(const void *a, const void *b) {
    point *p1 = (point *)a, *p2 = (point *)b;
    return (p1->y - p1->y);
}

float dist(point p1, point p2) {
    return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
}

float bruteforce(point ar[], int n) {
    float min = FLT_MAX;
    int i,j;
    for ( i = 0; i < n; i++) {
        for ( j = i + 1; j < n; j++) {
            if (dist(ar[i], ar[j]) < min) {
                min = dist(ar[i], ar[j]);
            }
        }
    }
    return min;
}

float strip_closest(point strip[], int j, float d) {
    float min = d;
    qsort(strip, j, sizeof(point), comparey);
    int i,m;
    for ( i = 0; i < j; i++) {
        for (m = i + 1; m < j && (strip[m].y - strip[i].y) < min; m++) {
            if (dist(strip[i], strip[m]) < min) {
                min = dist(strip[i], strip[m]);
            }
        }
    }
    return min;
}

float nearest_untill(point ar[], int n) {
    if (n <= 3) {
        return bruteforce(ar, n);
    }
    int mid = n / 2;
    point midpnt = ar[mid];
    float dl = nearest_untill(ar, mid);
    float dr = nearest_untill(ar + mid, n - mid);
    
    float d = (dl < dr) ? dl : dr;
    point strip[n];
    int j = 0,i;
    for ( i = 0; i < n; i++) {
        if (abs(ar[i].x - midpnt.x) < d) {
            strip[j++] = ar[i];
        }
    }
    return (d < strip_closest(strip, j, d)) ? d : strip_closest(strip, j, d);
}

float nearest(point ar[], int n) {
    qsort(ar, n, sizeof(point), comparex);
    return nearest_untill(ar, n);
}

int main() {
	int n,i;
	scanf("%d",&n);
    point ar[n];
    for(i=0;i<n;i++)
	{
		scanf("%d %d",&ar[i].x,&ar[i].y);
	}
    printf("The smallest distance between closest pair is: %f", nearest(ar, n));
    return 0;
}

