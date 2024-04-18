#include<bits/stdc++.h>

using namespace std;

class point
{
	public:
	int x,y;
};
int comparex(const void *a,const void *b)
{
	point *p1=(point*)a,*p2=(point*)b;
	return (p1->x - p2->x);
}
int comparey(const void *a,const void *b)
{
	point *p1=(point*)a,*p2=(point*)b;
	return (p1->y - p1->y);
}
float dist(point p1,point p2)
{
	return sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
}
float bruteforce(point ar[],int n)
{
	float min=FLT_MAX;
	for(int i=0;i<n;i++)
	{
		for(int j=i+1;j<n;j++)
		{
			if(dist(ar[i],ar[j])<min)
			{
				min=dist(ar[i],ar[j]);
			}
		}
	}
	return min;
}
float strip_closest(point strip[],int j,float d)
{
	float min=d;
	int a1,a2,b1,b2;
	qsort(strip,j,sizeof(strip),comparey);
	for(int i=0;i<j;i++)
	{
		for(int m=i+1;m<j && (strip[m].y - strip[i].y);m++)
		{
			if(dist(strip[i],strip[m])<min)
			{
				min=dist(strip[i],strip[m]);
		    }
	
		}
	}
	return min;
}

float nearest_untill(point ar[],int n)
{
	if(n<=3)
	{
		return bruteforce(ar,n);
	}
	int mid=n/2;
	point midpnt=ar[mid];
	float dl=nearest_untill(ar,mid);
	float dr=nearest_untill(ar+mid,n-mid);
	
	float d= min(dl,dr);
	point strip[n];
	int j=0;
	for(int i=0;i<n;i++)
	{
		if(abs(ar[i].x-midpnt.x)<d)
		{
			strip[j]=ar[i];
			j++;
		}
	}
	return min(d,strip_closest(strip,j,d));
}


float min(float a,float b)
{
	return (a>b)?a:b;
}
float nearest(point ar[],int n)
{
	qsort(ar,n,sizeof(point),comparex);
	return nearest_untill(ar,n);
}

int main()
{
    int n;
   FILE *fptr;
	if((fptr=fopen("input.txt","r"))== NULL)
	{
		cout<<"no file exits..";
	}
	else
   {
	fscanf(fptr,"%d",&n);
    point ar[n];
    for(int i=0;i<n;i++)
	{
		fscanf(fptr,"%d %d",&ar[i].x,&ar[i].y);
	}
	fclose(fptr);
    cout<<"The smallest distance between nearest pair is: "<<setprecision(2)<<fixed<<nearest(ar,n);
}
}
