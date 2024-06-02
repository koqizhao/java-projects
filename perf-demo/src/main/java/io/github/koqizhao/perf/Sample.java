package io.github.koqizhao.perf;

public class Sample extends BaseSample {
	int data;
	int x, y, z, u, v, w;
	long a, b, c, d, e, f;
	Object obj = new Object();
	
	public void calculate(int i, int j) {
	    baseData += i;
	    data += j;
	}
	
	public int getData() {
	    return baseData + data;
	}

    @Override
    public String toString() {
        return "Sample [data=" + data + ", x=" + x + ", y=" + y + ", z=" + z + ", u=" + u + ", v=" + v + ", w=" + w + ", a=" + a + ", b=" + b + ", c=" + c
            + ", d=" + d + ", e=" + e + ", f=" + f + ", obj=" + obj + ", baseData=" + baseData + "]";
    }
	
}