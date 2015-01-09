package com.zoran.gostapp.gost.gost;

import com.zoran.gostapp.gost.auxillary.Auxillary;

import android.util.Log;

public class GOSTblock {
	public long size;
	public long n1, n2;

	public GOSTblock()
	{
		n1 =  n2 = 0;
		size=0;
	}
	public GOSTblock(GOSTblock in)
	{
		set( in);
		
	}
	public void set(GOSTblock in)
	{
		this.n1 = in.n1;
		this.n2 = in.n2;
		this.size = in.size;
	}
	public void set(long in)
	{
		this.n1 = Auxillary.modulo32(in);
		this.n2 = Auxillary.modulo32(in >>> 32);
		this.size = 8;
		
	}
	public long toLong()
	{
		return (Auxillary.modulo32(n2) << 32) + Auxillary.modulo32(n1);
	}
	public void clear()
	{
		n1=n2=0;
		size=0;
	}
}
